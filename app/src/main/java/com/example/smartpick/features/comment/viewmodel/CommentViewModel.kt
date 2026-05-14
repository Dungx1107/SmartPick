package com.example.smartpick.features.comment.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Comment
import com.example.smartpick.core.utils.TimeFormatter
import com.example.smartpick.features.comment.data.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList
import kotlin.collections.filter

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository
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
    fun loadComments(postId: String, postOwnerId: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: List<Comment> = repository.getComments(postId)

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
            } catch (e: Exception) {
                _error.value = "Lỗi tải bình luận"
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
        postOwnerId: String?
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

                repository.insertComment(
                    postId = postId,
                    userId = userId,
                    content = content.trim(),
                    parentId = actualParentId
                )

                Log.i("CommentDebug", "Gửi thành công!")

                // Sau khi gửi thành công:
                _replyingTo.value = null // Thoát chế độ reply
                // Reload comments
                loadComments(postId, postOwnerId)

            } catch (e: Exception) {
                Log.e("CommentDebug", "LỖI KHI GỬI: ${e.message}", e)
                _error.value = e.message ?: "Không thể gửi bình luận"
            } finally {
                _isSending.value = false
            }
        }
    }


}