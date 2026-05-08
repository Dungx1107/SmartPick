package com.example.smartpick.features.feed.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.features.feed.data.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FeedUiState {
    data object Loading : FeedUiState()
    data class Success(val posts: List<Triple<Post, User, Product?>>) : FeedUiState()
    data class Error(val message: String) : FeedUiState()
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            Log.d(TAG, "Bắt đầu tải dữ liệu Feed...")
            _uiState.value = FeedUiState.Loading
            try {
                val posts = feedRepository.getPostsWithUsers()

                Log.d(TAG, "Tải thành công: ${posts.size} bài viết.")
                posts.forEachIndexed { index, item ->
                    Log.d(
                        TAG,
                        "Post[$index]: ID=${item.first.id}, User=${item.second.fullName}, HasProduct=${item.third != null}"
                    )
                }

                _uiState.value = FeedUiState.Success(posts)

            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi tải Feed: ${e.message}", e)
                _uiState.value = FeedUiState.Error(e.message ?: "Lỗi hệ thống không xác định")
            }
        }
    }
}
