package com.example.smartpick.features.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.User
import com.example.smartpick.features.feed.data.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Trạng thái giao diện của màn hình Feed
 */
sealed interface FeedUiState {
    object Loading : FeedUiState
    data class Success(val posts: List<Pair<Post, User>>) : FeedUiState
    data class Error(val message: String) : FeedUiState
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FeedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            try {
                // Gọi repository để lấy dữ liệu thật từ Database
                val postsWithUsers = repository.getPostsWithUsers()
                _uiState.value = FeedUiState.Success(postsWithUsers)

            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error(e.message ?: "Lỗi tải dữ liệu")
            }
        }
    }
}
