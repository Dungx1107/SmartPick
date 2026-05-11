package com.example.smartpick.features.post_detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Post
import com.example.smartpick.features.post_detail.data.PostDetailRepository
import com.example.smartpick.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val repository: PostDetailRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState = _uiState.asStateFlow()

    // Lấy postId từ Navigation argument (sử dụng hằng số từ Routes)
    private val postId: String? = savedStateHandle[Routes.PostDetail.ARG_POST_ID]

    init {
        postId?.let { loadPostDetail(it) }
    }

    fun loadPostDetail(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repository.getPostDetail(id)

                if (response != null) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        post = Post(
                            id = response.id,
                            userId = response.user.id,
                            content = response.content,
                            mediaUrls = response.mediaUrls,
                            createdAt = response.createdAt
                        ),
                        user = response.user,
                        product = response.product,
                        // Ở đây bạn có thể map thêm likesCount, commentsCount nếu cần hiển thị tổng
                    ) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Không tìm thấy bài viết") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }
}