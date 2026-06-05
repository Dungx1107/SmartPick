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

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _comments = MutableStateFlow<List<CommentUIState>>(emptyList())
    val comments = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending = _isSending.asStateFlow()

    private val _replyingTo = MutableStateFlow<CommentUIState?>(null)
    val replyingTo = _replyingTo.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun setReplyingTo(comment: CommentUIState?) {
        _replyingTo.value = comment
    }

    fun clearError() {
        _error.value = null
    }

    fun loadComments(postId: String, postOwnerId: String?, currentUserId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: List<Comment> = repository.getComments(postId, currentUserId)
                val topLevelComments = response.filter { it.parentId == null }

                _comments.value = topLevelComments.map { parent ->
                    val childReplies = response.filter { it.parentId == parent.id }.map { child ->
                        val replyTo = response.find { it.id == child.parentId }?.user?.fullName
                        mapToUIState(child, postOwnerId, replyTo, emptyList())
                    }
                    mapToUIState(parent, postOwnerId, null, childReplies)
                }
                Log.d("CommentDebug", "LOAD_COMMENTS: Đã tải xong ${response.size} mục từ DB.")
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
        Log.d("CommentDebug", "=== BẮT ĐẦU SEND_COMMENT ===")
        Log.d("CommentDebug", "Input: postId=$postId, userId=$userId, content='$content'")

        if (postId.isBlank() || userId.isBlank() || content.isBlank()) {
            _error.value = "Dữ liệu bình luận không hợp lệ"
            Log.e("CommentDebug", "Validation Failed: Có trường dữ liệu bị rỗng")
            return
        }

        if (_isSending.value) {
            Log.w("CommentDebug", "REJECTED: Hàm sendComment đang chạy ngầm, chặn click lặp từ UI")
            return
        }

        viewModelScope.launch {
            _isSending.value = true
            _error.value = null
            try {
                val targetComment = _replyingTo.value
                val actualParentId = targetComment?.parentId ?: targetComment?.id
                val finalReceiverId = targetComment?.authorId ?: (postOwnerId ?: "")

                Log.d("CommentDebug", "Luồng xử lý: parentId=$actualParentId, receiverId=$finalReceiverId")

                // 1. Lưu xuống DB
                Log.d("CommentDebug", "STEP 1: Đang gọi repository.insertComment...")
                val newCommentId = repository.insertComment(
                    postId = postId,
                    userId = userId,
                    content = content.trim(),
                    receiverId = finalReceiverId,
                    parentId = actualParentId
                )
                Log.d("CommentDebug", "STEP 1 SUCCESS: DB phản hồi sinh ra ID thật = $newCommentId")

                // 2. Khởi tạo đối tượng UI State mới
                val newCommentUi = CommentUIState(
                    id = newCommentId,
                    authorId = userId,
                    authorName = currentUserName,
                    authorAvatar = null,
                    content = content.trim(),
                    timeAgo = "Vừa xong",
                    likesCount = 0,
                    isLiked = false,
                    isAuthor = userId == postOwnerId,
                    parentId = actualParentId,
                    replyToName = targetComment?.authorName,
                    replies = emptyList()
                )

                // 3. KHỬ TRÙNG CỤC BỘ VÀ IN LOG ĐỐI CHIẾU
                val currentList = _comments.value
                Log.d("CommentDebug", "STEP 2 (Local Update): Kích thước danh sách hiện tại trên RAM = ${currentList.size}")
                currentList.forEachIndexed { index, item ->
                    Log.d("CommentDebug", "   -> Hiện tại [$index]: ID=${item.id}, Content='${item.content}'")
                }

                if (actualParentId == null) {
                    val isDuplicate = !currentList.none { it.id == newCommentId }
                    Log.d("CommentDebug", "Kiểm tra trùng ID gốc: isDuplicate=$isDuplicate (none=${currentList.none { it.id == newCommentId }})")

                    if (!isDuplicate) {
                        _comments.value = currentList + newCommentUi
                        Log.d("CommentDebug", "-> Đã chèn cục bộ comment gốc thành công vào State.")
                    } else {
                        Log.w("CommentDebug", "-> BỎ QUA chèn cục bộ vì ID $newCommentId đã tồn tại sẵn trong danh sách!")
                    }
                } else {
                    Log.d("CommentDebug", "Tiến hành quét danh sách để chèn Reply...")
                    _comments.value = currentList.map { parent ->
                        if (parent.id == actualParentId) {
                            val isReplyDuplicate = !parent.replies.none { it.id == newCommentId }
                            Log.d("CommentDebug", "Kiểm tra trùng Reply: isReplyDuplicate=$isReplyDuplicate")

                            if (!isReplyDuplicate) {
                                Log.d("CommentDebug", "-> Đã chèn cục bộ reply vào parent ID=${parent.id}")
                                parent.copy(replies = parent.replies + newCommentUi)
                            } else {
                                Log.w("CommentDebug", "-> BỎ QUA chèn reply vì ID $newCommentId đã có trong parent.")
                                parent
                            }
                        } else parent
                    }
                }

                // Log danh sách sau cùng để kiểm tra kết quả
                val finalSnapshot = _comments.value
                Log.d("CommentDebug", "STEP 3 (Post-Update Snapshot): Kích thước danh sách mới = ${finalSnapshot.size}")
                finalSnapshot.forEachIndexed { index, item ->
                    Log.d("CommentDebug", "   -> Sau update [$index]: ID=${item.id}, Content='${item.content}'")
                }

                // 4. Bắn thông báo Push
                if (finalReceiverId.isNotEmpty() && finalReceiverId != userId) {
                    viewModelScope.launch {
                        try {
                            val pushTitle = if (targetComment != null) "Phản hồi mới" else "Bình luận mới"
                            val pushBody = "$currentUserName đã phản hồi: ${content.take(40)}"
                            notificationRepository.triggerPushNotification(
                                receiverId = finalReceiverId, title = pushTitle, body = pushBody,
                                type = "comment", postId = postId, targetId = newCommentId
                            )
                        } catch (e: Exception) {
                            Log.e("CommentDebug", "Lỗi trigger Push: ${e.message}")
                        }
                    }
                }

                _replyingTo.value = null
            } catch (e: Exception) {
                Log.e("CommentDebug", "CRASH TẠI SEND_COMMENT: ${e.message}", e)
                _error.value = e.message ?: "Không thể gửi bình luận"
            } finally {
                _isSending.value = false
                Log.d("CommentDebug", "=== KẾT THÚC LUỒNG SEND_COMMENT ===")
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
        val oldComments = _comments.value

        fun updateLikeInList(list: List<CommentUIState>): List<CommentUIState> {
            return list.map { comment ->
                if (comment.id == commentId) {
                    val newIsLiked = !comment.isLiked
                    val newLikesCount = if (newIsLiked) comment.likesCount + 1 else maxOf(0, comment.likesCount - 1)
                    comment.copy(isLiked = newIsLiked, likesCount = newLikesCount)
                } else if (comment.replies.isNotEmpty()) {
                    comment.copy(replies = updateLikeInList(comment.replies))
                } else {
                    comment
                }
            }
        }

        val updatedList = updateLikeInList(oldComments)
        _comments.value = updatedList

        val targetComment = updatedList.find { it.id == commentId }
            ?: updatedList.flatMap { it.replies }.find { it.id == commentId }

        if (targetComment == null) return

        viewModelScope.launch {
            try {
                repository.toggleLike(
                    commentId = commentId,
                    userId = currentUserId,
                    isLiked = !targetComment.isLiked,
                    commentOwnerId = targetComment.authorId,
                    postId = postId
                )

                if (targetComment.isLiked && targetComment.authorId != currentUserId) {
                    notificationRepository.triggerPushNotification(
                        receiverId = targetComment.authorId,
                        title = "Lượt thích mới",
                        body = "$currentUserName đã thích bình luận của bạn",
                        type = "like",
                        postId = postId
                    )
                }
            } catch (e: Exception) {
                _comments.value = oldComments
                _error.value = "Không thể thực hiện tương tác. Vui lòng thử lại."
                Log.e("CommentDebug", "LỖI KHI TOGGLE LIKE: ${e.message}", e)
            }
        }
    }
}