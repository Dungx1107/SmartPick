package com.example.smartpick.features.feed.viewmodel

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


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _reactedPosts = MutableStateFlow<List<Triple<Post, User, Product?>>>(emptyList())
    val reactedPosts: StateFlow<List<Triple<Post, User, Product?>>> = _reactedPosts.asStateFlow()

    private val _isReactedLoading = MutableStateFlow(false)
    val isReactedLoading: StateFlow<Boolean> = _isReactedLoading.asStateFlow()

    init { loadFeed() }

    fun loadFeed() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            try {
                val currentUserId = authRepository.getCurrentUser()?.id ?: ""
                val posts = feedRepository.getPostsWithUsers(currentUserId)
                _uiState.value = FeedUiState.Success(posts.filter { it.first.sharedPostId == null })
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error(e.message ?: "Lỗi")
            }
        }
    }

    fun refreshFeedSilently() {
        viewModelScope.launch {
            try {
                val currentUserId = authRepository.getCurrentUser()?.id ?: ""
                val posts = feedRepository.getPostsWithUsers(currentUserId)
                _uiState.value = FeedUiState.Success(posts.filter { it.first.sharedPostId == null })
                _reactedPosts.value = feedRepository.getReactedPosts(currentUserId)
            } catch (e: Exception) { }
        }
    }

    fun loadReactedPosts() {
        viewModelScope.launch {
            _isReactedLoading.value = true
            try {
                val user = authRepository.getCurrentUser()
                if (user?.id?.isNotEmpty() == true) {
                    _reactedPosts.value = feedRepository.getReactedPosts(user.id)
                }
            } catch (e: Exception) { }
            finally { _isReactedLoading.value = false }
        }
    }

    // TÍNH TOÁN NGẦM ĐỂ TĂNG/GIẢM CẢM XÚC
    private fun updatePostReaction(post: Post, newReaction: ReactionType): Post {
        val oldReaction = post.currentUserReaction
        val breakdown = post.reactionBreakdown.toMutableMap()
        var count = post.reactionCount

        if (oldReaction == newReaction) {
            breakdown[oldReaction] = maxOf(0, (breakdown[oldReaction] ?: 0) - 1)
            if (breakdown[oldReaction] == 0) breakdown.remove(oldReaction)
            count = maxOf(0, count - 1)
            return post.copy(currentUserReaction = null, reactionCount = count, reactionBreakdown = breakdown)
        } else {
            if (oldReaction != null) {
                breakdown[oldReaction] = maxOf(0, (breakdown[oldReaction] ?: 0) - 1)
                if (breakdown[oldReaction] == 0) breakdown.remove(oldReaction)
            } else count += 1
            breakdown[newReaction] = (breakdown[newReaction] ?: 0) + 1
            return post.copy(currentUserReaction = newReaction, reactionCount = count, reactionBreakdown = breakdown)
        }
    }

    fun toggleReaction(postId: String, reactionType: ReactionType) {
        val currentState = _uiState.value
        if (currentState is FeedUiState.Success) {
            val updatedPosts = currentState.posts.map { (post, user, product) ->
                var updatedPost = post
                if (updatedPost.id == postId) updatedPost = updatePostReaction(updatedPost, reactionType)
                if (updatedPost.sharedPost?.id == postId) updatedPost = updatedPost.copy(sharedPost = updatePostReaction(updatedPost.sharedPost, reactionType))
                Triple(updatedPost, user, product)
            }
            _uiState.value = FeedUiState.Success(updatedPosts)
        }

        val updatedReacted = _reactedPosts.value.mapNotNull { (post, user, product) ->
            var updatedPost = post
            if (updatedPost.id == postId) {
                if (updatedPost.currentUserReaction == reactionType) return@mapNotNull null
                updatedPost = updatePostReaction(updatedPost, reactionType)
            }
            if (updatedPost.sharedPost?.id == postId) updatedPost = updatedPost.copy(sharedPost = updatePostReaction(updatedPost.sharedPost, reactionType))
            Triple(updatedPost, user, product)
        }
        _reactedPosts.value = updatedReacted

        viewModelScope.launch {
            try {
                val currentUserId = authRepository.getCurrentUser()?.id
                if (currentUserId != null) feedRepository.toggleReaction(postId, currentUserId, reactionType)
            } catch (e: Exception) { }
        }
    }

    fun sharePost(postId: String, caption: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val currentUserId = authRepository.getCurrentUser()?.id
                if (currentUserId != null) {
                    val result = feedRepository.sharePost(postId, currentUserId, caption)
                    if (result.isSuccess) { onSuccess(); refreshFeedSilently() }
                }
            } catch (e: Exception) { }
        }
    }

    // HÀM XÓA BÀI VIẾT TỪ FEED
    fun deletePost(postId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = feedRepository.deletePost(postId)
            if (result.isSuccess) {
                val currentState = _uiState.value
                if (currentState is FeedUiState.Success) {
                    _uiState.value = FeedUiState.Success(currentState.posts.filter { it.first.id != postId })
                }
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Có lỗi xảy ra khi xóa")
            }
        }
    }
}