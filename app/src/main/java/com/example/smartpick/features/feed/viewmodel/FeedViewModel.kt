package com.example.smartpick.features.feed.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.model.User
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.feed.data.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// LƯU Ý: Đã xóa khối `sealed class FeedUiState` ở đây để tránh lỗi Redeclaration.
// Hệ thống sẽ tự dùng class FeedUiState mà bạn đã có sẵn.

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    // Quản lý danh sách bài viết đã thích cho tab Saved
    private val _reactedPosts = MutableStateFlow<List<Triple<Post, User, Product?>>>(emptyList())
    val reactedPosts: StateFlow<List<Triple<Post, User, Product?>>> = _reactedPosts.asStateFlow()

    private val _isReactedLoading = MutableStateFlow(false)
    val isReactedLoading: StateFlow<Boolean> = _isReactedLoading.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            try {
                val user = authRepository.getCurrentUser()
                val currentUserId = user?.id ?: ""
                val posts = feedRepository.getPostsWithUsers(currentUserId)
                _uiState.value = FeedUiState.Success(posts)
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Lỗi tải feed", e)
                _uiState.value = FeedUiState.Error(e.message ?: "Đã xảy ra lỗi")
            }
        }
    }

    fun refreshFeedSilently() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                val currentUserId = user?.id ?: ""
                val posts = feedRepository.getPostsWithUsers(currentUserId)
                _uiState.value = FeedUiState.Success(posts)

                // Refresh luôn cả tab Đã thích ngầm
                val reacted = feedRepository.getReactedPosts(currentUserId)
                _reactedPosts.value = reacted
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Lỗi refresh ngầm", e)
            }
        }
    }

    // Tải danh sách bài viết đã thích
    fun loadReactedPosts() {
        viewModelScope.launch {
            _isReactedLoading.value = true
            try {
                val user = authRepository.getCurrentUser()
                val currentUserId = user?.id ?: ""
                if (currentUserId.isNotEmpty()) {
                    val posts = feedRepository.getReactedPosts(currentUserId)
                    _reactedPosts.value = posts
                }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Lỗi tải bài viết đã thích", e)
            } finally {
                _isReactedLoading.value = false
            }
        }
    }

    fun toggleReaction(postId: String, reactionType: ReactionType) {
        // Cập nhật ngầm trên Feed
        val currentState = _uiState.value
        if (currentState is FeedUiState.Success) {
            val updatedPosts = currentState.posts.map { (post, user, product) ->
                if (post.id == postId) {
                    val isRemoving = post.currentUserReaction == reactionType
                    val newReaction = if (isRemoving) null else reactionType
                    val newCount = if (isRemoving) maxOf(0, post.reactionCount - 1) else if (post.currentUserReaction == null) post.reactionCount + 1 else post.reactionCount
                    Triple(post.copy(currentUserReaction = newReaction, reactionCount = newCount), user, product)
                } else {
                    Triple(post, user, product)
                }
            }
            _uiState.value = FeedUiState.Success(updatedPosts)
        }

        // Cập nhật ngầm trên tab Đã thích (Xóa khỏi danh sách nếu Unlike)
        val currentReacted = _reactedPosts.value
        val updatedReacted = currentReacted.mapNotNull { (post, user, product) ->
            if (post.id == postId) {
                val isRemoving = post.currentUserReaction == reactionType
                if (isRemoving) null // Xóa khỏi danh sách nếu gỡ cảm xúc
                else {
                    val newCount = if (post.currentUserReaction == null) post.reactionCount + 1 else post.reactionCount
                    Triple(post.copy(currentUserReaction = reactionType, reactionCount = newCount), user, product)
                }
            } else {
                Triple(post, user, product)
            }
        }
        _reactedPosts.value = updatedReacted

        // Lưu lên DB
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                val currentUserId = user?.id
                if (currentUserId != null) {
                    feedRepository.toggleReaction(postId, currentUserId, reactionType)
                }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Exception khi thả cảm xúc", e)
            }
        }
    }
}