package com.example.smartpick.features.profile.viewmodel

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
            } catch (e: Exception) { }
            finally { _isLoading.value = false }
        }
    }

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
        val updatedPosts = _userPosts.value.map { (post, user, product) ->
            var updatedPost = post
            if (updatedPost.id == postId) updatedPost = updatePostReaction(updatedPost, reactionType)
            if (updatedPost.sharedPost?.id == postId) updatedPost = updatedPost.copy(sharedPost = updatePostReaction(updatedPost.sharedPost, reactionType))
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
                    if (result.isSuccess) { onSuccess(); loadUserPosts(currentUserId, currentUserId) }
                }
            } catch (e: Exception) { }
        }
    }
}