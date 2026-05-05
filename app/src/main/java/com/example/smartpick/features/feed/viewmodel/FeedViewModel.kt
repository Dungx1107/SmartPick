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
                // Tạm thời trả về danh sách trống vì chưa cấu hình lấy dữ liệu từ DB
                // Sau này bạn sẽ viết hàm repository.getPosts() để lấy dữ liệu thật
                _uiState.value = FeedUiState.Success(emptyList())
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
