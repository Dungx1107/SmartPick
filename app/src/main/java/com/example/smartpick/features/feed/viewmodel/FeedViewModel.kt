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

// (Đã xóa block sealed class FeedUiState ở đây để tránh lỗi Redeclaration)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            try {
                // FIX: Dùng getCurrentUser() thay vì currentUser.value
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

    fun toggleReaction(postId: String, reactionType: ReactionType) {
        // BƯỚC 1: CẬP NHẬT GIAO DIỆN TỨC THÌ (OPTIMISTIC UPDATE)
        val currentState = _uiState.value
        if (currentState is FeedUiState.Success) {
            val updatedPosts = currentState.posts.map { (post, user, product) ->
                if (post.id == postId) {
                    val isRemoving = post.currentUserReaction == reactionType
                    val newReaction = if (isRemoving) null else reactionType

                    val newCount = if (isRemoving) {
                        maxOf(0, post.reactionCount - 1)
                    } else if (post.currentUserReaction == null) {
                        post.reactionCount + 1
                    } else {
                        post.reactionCount
                    }

                    Triple(post.copy(currentUserReaction = newReaction, reactionCount = newCount), user, product)
                } else {
                    Triple(post, user, product)
                }
            }
            _uiState.value = FeedUiState.Success(updatedPosts)
        }

        // BƯỚC 2: GỌI API LƯU LÊN DATABASE NGẦM
        viewModelScope.launch {
            try {
                // FIX: Dùng getCurrentUser() thay vì currentUser.value
                val user = authRepository.getCurrentUser()
                val currentUserId = user?.id

                if (currentUserId != null) {
                    val result = feedRepository.toggleReaction(postId, currentUserId, reactionType)
                    if (result.isFailure) {
                        Log.e("FeedViewModel", "Lỗi lưu cảm xúc: ${result.exceptionOrNull()?.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Exception khi thả cảm xúc", e)
            }
        }
    }

    // Hàm tải lại Feed ngầm, không kích hoạt trạng thái Loading làm giật màn hình
    fun refreshFeedSilently() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                val currentUserId = user?.id ?: ""
                val posts = feedRepository.getPostsWithUsers(currentUserId)
                _uiState.value = FeedUiState.Success(posts)
            } catch (e: Exception) {
                // Nếu lỗi thì im lặng bỏ qua, giữ nguyên giao diện cũ
            }
        }
    }
}