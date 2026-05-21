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

                // LỌC: Ẩn các bài đã chia sẻ khỏi Bảng tin (Chỉ hiện bài gốc)
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
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Lỗi refresh ngầm", e)
            }
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
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Lỗi tải bài đã thích", e)
            } finally {
                _isReactedLoading.value = false
            }
        }
    }

    fun toggleReaction(postId: String, reactionType: ReactionType) {
        val currentState = _uiState.value
        if (currentState is FeedUiState.Success) {
            val updatedPosts = currentState.posts.map { (post, user, product) ->
                if (post.id == postId) {
                    val isRemoving = post.currentUserReaction == reactionType
                    val newReaction = if (isRemoving) null else reactionType
                    val newCount = if (isRemoving) maxOf(0, post.reactionCount - 1) else if (post.currentUserReaction == null) post.reactionCount + 1 else post.reactionCount
                    Triple(post.copy(currentUserReaction = newReaction, reactionCount = newCount), user, product)
                } else { Triple(post, user, product) }
            }
            _uiState.value = FeedUiState.Success(updatedPosts)
        }

        val updatedReacted = _reactedPosts.value.mapNotNull { (post, user, product) ->
            if (post.id == postId) {
                val isRemoving = post.currentUserReaction == reactionType
                if (isRemoving) null
                else Triple(post.copy(currentUserReaction = reactionType, reactionCount = if (post.currentUserReaction == null) post.reactionCount + 1 else post.reactionCount), user, product)
            } else { Triple(post, user, product) }
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