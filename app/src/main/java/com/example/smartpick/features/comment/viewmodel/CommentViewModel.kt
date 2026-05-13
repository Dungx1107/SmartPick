package com.example.smartpick.features.comment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.utils.TimeFormatter
import com.example.smartpick.features.comment.data.CommentRepository
import com.example.smartpick.features.comment.ui.components.CommentUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    fun loadComments(postId: String, postOwnerId: String?) {

        if (postId.isBlank()) {
            _error.value = "ID bài viết không hợp lệ"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.getComments(postId)
                _comments.value = response.map { res ->
                    CommentUIState(
                        id = res.id,
                        authorName = res.user.fullName ?: "Người dùng",
                        authorAvatar = res.user.avatarUrl,
                        content = res.content,
                        timeAgo = TimeFormatter.formatTimeAgo(res.createdAt),
                        likesCount = res.likesCount,
                        isLiked = res.isLiked,
                        isAuthor = res.userId == postOwnerId
                    )
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Không thể tải bình luận"
            } finally {
                _isLoading.value = false
            }
        }
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
                repository.insertComment(
                    postId = postId,
                    userId = userId,
                    content = content.trim()
                )
                // Reload comments
                loadComments(postId, postOwnerId)

            } catch (e: Exception) {
                _error.value = e.message ?: "Không thể gửi bình luận"
            } finally {
                _isSending.value = false
            }
        }
    }
}