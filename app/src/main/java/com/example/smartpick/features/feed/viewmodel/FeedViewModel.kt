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
                val user = authRepository.getCurrentUser()
                val currentUserId = user?.id ?: ""
                val posts = feedRepository.getPostsWithUsers(currentUserId)
                val feedPosts = posts.filter { it.first.sharedPostId == null }
                _uiState.value = FeedUiState.Success(feedPosts)
            } catch (e: Exception) {
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
                val feedPosts = posts.filter { it.first.sharedPostId == null }
                _uiState.value = FeedUiState.Success(feedPosts)
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

    // FIX: Quét cập nhật trạng thái nếu thả tim vào bài viết Gốc nằm trong bài Share
    fun toggleReaction(postId: String, reactionType: ReactionType) {
        val currentState = _uiState.value
        if (currentState is FeedUiState.Success) {
            val updatedPosts = currentState.posts.map { (post, user, product) ->
                var updatedPost = post

                if (updatedPost.id == postId) {
                    val isRemoving = updatedPost.currentUserReaction == reactionType
                    val newReaction = if (isRemoving) null else reactionType
                    val newCount = if (isRemoving) maxOf(0, updatedPost.reactionCount - 1) else if (updatedPost.currentUserReaction == null) updatedPost.reactionCount + 1 else updatedPost.reactionCount
                    updatedPost = updatedPost.copy(currentUserReaction = newReaction, reactionCount = newCount)
                }

                if (updatedPost.sharedPost?.id == postId) {
                    val innerPost = updatedPost.sharedPost
                    val isRemoving = innerPost.currentUserReaction == reactionType
                    val newReaction = if (isRemoving) null else reactionType
                    val newCount = if (isRemoving) maxOf(0, innerPost.reactionCount - 1) else if (innerPost.currentUserReaction == null) innerPost.reactionCount + 1 else innerPost.reactionCount
                    updatedPost = updatedPost.copy(sharedPost = innerPost.copy(currentUserReaction = newReaction, reactionCount = newCount))
                }

                Triple(updatedPost, user, product)
            }
            _uiState.value = FeedUiState.Success(updatedPosts)
        }

        val updatedReacted = _reactedPosts.value.mapNotNull { (post, user, product) ->
            var updatedPost = post
            if (updatedPost.id == postId) {
                val isRemoving = updatedPost.currentUserReaction == reactionType
                if (isRemoving) return@mapNotNull null
                val newCount = if (updatedPost.currentUserReaction == null) updatedPost.reactionCount + 1 else updatedPost.reactionCount
                updatedPost = updatedPost.copy(currentUserReaction = reactionType, reactionCount = newCount)
            }
            if (updatedPost.sharedPost?.id == postId) {
                val innerPost = updatedPost.sharedPost
                val isRemoving = innerPost.currentUserReaction == reactionType
                val newReaction = if (isRemoving) null else reactionType
                val newCount = if (isRemoving) maxOf(0, innerPost.reactionCount - 1) else if (innerPost.currentUserReaction == null) innerPost.reactionCount + 1 else innerPost.reactionCount
                updatedPost = updatedPost.copy(sharedPost = innerPost.copy(currentUserReaction = newReaction, reactionCount = newCount))
            }
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
                    if (result.isSuccess) {
                        onSuccess()
                        refreshFeedSilently()
                    }
                }
            } catch (e: Exception) { }
        }
    }
}