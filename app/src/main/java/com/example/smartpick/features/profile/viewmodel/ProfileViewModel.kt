package com.example.smartpick.features.profile.viewmodel

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
class ProfileViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userPosts = MutableStateFlow<List<Triple<Post, User, Product?>>>(emptyList())
    val userPosts: StateFlow<List<Triple<Post, User, Product?>>> = _userPosts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadUserPosts(profileUserId: String, currentUserId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val posts = feedRepository.getUserPosts(profileUserId, currentUserId)
                _userPosts.value = posts
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Lỗi tải bài viết trang cá nhân: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // FIX: Quét cập nhật trạng thái nếu thả tim vào bài viết Gốc
    fun toggleReaction(postId: String, reactionType: ReactionType) {
        val updatedPosts = _userPosts.value.map { (post, user, product) ->
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
        _userPosts.value = updatedPosts

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
                        loadUserPosts(currentUserId, currentUserId) // Load lại trang cá nhân
                    }
                }
            } catch (e: Exception) { }
        }
    }
}