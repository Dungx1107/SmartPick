package com.example.smartpick.features.comment.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Comment
import com.example.smartpick.core.utils.TimeFormatter
import com.example.smartpick.features.comment.data.CommentRepository
import com.example.smartpick.features.notification.data.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList
import kotlin.collections.filter

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _comments = MutableStateFlow<List<CommentUIState>>(emptyList())
    val comments = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Loading riêng cho gửi comment
    private val _isSending = MutableStateFlow(false)
    val isSending = _isSending.asStateFlow()

    // Trạng thái: Người dùng đang reply comment nào?
    // Nếu null tức là đang viết comment chính
    private val _replyingTo = MutableStateFlow<CommentUIState?>(null)
    val replyingTo = _replyingTo.asStateFlow()

    fun setReplyingTo(comment: CommentUIState?) {
        _replyingTo.value = comment
    }

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    fun loadComments(postId: String, postOwnerId: String?, currentUserId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: List<Comment> = repository.getComments(postId, currentUserId)
                // 1. Lọc bình luận gốc (Tầng 1)
                val topLevelComments = response.filter { it.parentId == null }

                _comments.value = topLevelComments.map { parent ->
                    // 2. Tìm và gộp các con của nó (Tầng 2)
                    val childReplies = response.filter { it.parentId == parent.id }.map { child ->
                        // TÌM TÊN NGƯỜI ĐƯỢC REPLY (Cha trực tiếp của reply này)
                        val replyTo = response.find { it.id == child.parentId }?.user?.fullName

                        mapToUIState(child, postOwnerId, replyTo, emptyList())
                    }
                    mapToUIState(parent, postOwnerId, null, childReplies)
                }
                Log.d(
                    "LikeDebug",
                    "[UI UPDATE] Đã tải xong danh sách mới từ DB. UI chuẩn bị render lại trạng thái."
                )
            } catch (e: Exception) {
                _error.value = "Lỗi tải bình luận"
                e.printStackTrace()
                Log.e("CommentDebug", "LỖI KHI TẢI: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun mapToUIState(
        comment: Comment,
        postOwnerId: String?,
        replyToName: String?,
        replies: List<CommentUIState>
    ): CommentUIState {
        return CommentUIState(
            id = comment.id,
            authorId = comment.userId,
            authorName = comment.user.fullName ?: "Người dùng",
            authorAvatar = comment.user.avatarUrl,
            content = comment.content,
            timeAgo = TimeFormatter.formatTimeAgo(comment.createdAt),
            likesCount = comment.likesCount,
            isLiked = comment.isLiked,
            isAuthor = comment.userId == postOwnerId,
            parentId = comment.parentId,
            replyToName = replyToName,
            replies = replies
        )
    }

    fun sendComment(
        postId: String,
        userId: String,
        content: String,
        postOwnerId: String?,
        currentUserName: String
    ) {

        if (postId.isBlank()) {
            _error.value = "ID bài viết không hợp lệ"
            return
        }

        if (userId.isBlank()) {
            _error.value = "Người dùng không hợp lệ"
            return
        }

        if (content.isBlank()) {
            _error.value = "Vui lòng nhập bình luận"
            return
        }

        viewModelScope.launch {
            _isSending.value = true
            _error.value = null
            try {

                val targetComment = _replyingTo.value

                // Nếu đang reply một B, parentId vẫn phải là thằng gốc (parentId của thằng con)
                // Nếu đang reply A, parentId là ID của A
                val actualParentId = targetComment?.parentId ?: targetComment?.id

                // XÁC ĐỊNH NGƯỜI NHẬN THÔNG BÁO
                val finalReceiverId =
                    targetComment?.// Nếu đang reply, người nhận là chủ của bình luận đó
                    authorId ?: (// Nếu là comment mới, người nhận là chủ bài viết
                            postOwnerId ?: "")

                repository.insertComment(
                    postId = postId,
                    userId = userId,
                    content = content.trim(),
                    receiverId = finalReceiverId, // Truyền ID đã xác định
                    parentId = actualParentId
                )

                Log.i("CommentDebug", "Gửi thành công!")

                // --- BẮT ĐẦU ĐOẠN MÃ MỚI TÍCH HỢP PUSH NOTIFICATION ---
                if (finalReceiverId.isNotEmpty() && finalReceiverId != userId) {
                    // Chạy background job để gọi Edge Function, tránh làm chậm UI luồng chính
                    viewModelScope.launch {
                        try {
                            val pushTitle = if (targetComment != null) "Phản hồi mới" else "Bình luận mới"
                            val pushBody = "$currentUserName đã phản hồi: ${content.take(40)}${if (content.length > 40) "..." else ""}"

                            Log.d("FCM_TRIGGER", "Đang gọi Edge Function để báo push cho user $finalReceiverId")

                            notificationRepository.triggerPushNotification(
                                receiverId = finalReceiverId,
                                title = pushTitle,
                                body = pushBody,
                                type = "comment",
                                postId = postId,
                                targetId = null // Cập nhật targetId nếu cần Deep Link tới thẳng dòng comment
                            )
                        } catch (e: Exception) {
                            Log.e("FCM_TRIGGER", "Lỗi trigger Push", e)
                        }
                    }
                }

                // Sau khi gửi thành công:
                _replyingTo.value = null // Thoát chế độ reply
                // Reload comments
                loadComments(postId, postOwnerId, userId)

            } catch (e: Exception) {
                Log.e("CommentDebug", "LỖI KHI GỬI: ${e.message}", e)
                _error.value = e.message ?: "Không thể gửi bình luận"
            } finally {
                _isSending.value = false
            }
        }
    }


    fun toggleLikeComment(
        commentId: String,
        currentUserId: String,
        postId: String,
        postOwnerId: String?,
        currentUserName: String
    ) {
        Log.d(
            "LikeDebug",
            "[START] Người dùng $currentUserId thực hiện Click Like commentId: $commentId"
        )
        viewModelScope.launch {
            // Tìm comment trong danh sách UI hiện tại (quét cả bình luận gốc và các phản hồi con)
            val currentList = _comments.value
            val targetComment = currentList.find { it.id == commentId }
                ?: currentList.flatMap { it.replies }.find { it.id == commentId }

            if (targetComment == null) {
                Log.e("CommentDebug", "Không tìm thấy comment với ID: $commentId để thực hiện like")
                return@launch
            }

            try {
                Log.d("LikeDebug", "[REQUEST] Đang gửi yêu cầu toggleLike xuống Repository...")
                // Gọi sang repository xử lý (Đã bao gồm logic gửi thông báo ở bước trước)
                repository.toggleLike(
                    commentId = commentId,
                    userId = currentUserId,
                    isLiked = targetComment.isLiked,
                    commentOwnerId = targetComment.authorId,
                    postId = postId
                )
                Log.d(
                    "LikeDebug",
                    "[SUCCESS] Repository xử lý thành công. Tiến hành gọi loadComments để đồng bộ..."
                )
                // MỞ RỘNG: Tích hợp Gửi thông báo Push khi một người like comment của bạn
                if (!targetComment.isLiked && targetComment.authorId != currentUserId) {
                    viewModelScope.launch {
                        notificationRepository.triggerPushNotification(
                            receiverId = targetComment.authorId,
                            title = "Lượt thích mới",
                            body = "$currentUserName đã thích bình luận của bạn",
                            type = "like",
                            postId = postId
                        )
                    }
                }

                // Tải lại danh sách bình luận để cập nhật UI mới nhất
                loadComments(postId, postOwnerId, currentUserId)
            } catch (e: Exception) {
                Log.e("CommentDebug", "LỖI KHI TOGGLE LIKE: ${e.message}", e)
                _error.value = "Không thể thực hiện tương tác"
            }
        }
    }

}