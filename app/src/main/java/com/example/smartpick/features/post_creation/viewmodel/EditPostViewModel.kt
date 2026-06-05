package com.example.smartpick.features.post_creation.viewmodel

import android.content.Context
import android.net.Uri
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

sealed class EditPostUiState {
    object Idle : EditPostUiState()
    object Loading : EditPostUiState()
    data class Success(val post: Post, val user: User, val product: Product?) : EditPostUiState()
    data class UpdateSuccess(val message: String) : EditPostUiState()
    data class Error(val message: String) : EditPostUiState()
}

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditPostUiState>(EditPostUiState.Idle)
    val uiState: StateFlow<EditPostUiState> = _uiState.asStateFlow()

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _uiState.value = EditPostUiState.Loading
            val result = feedRepository.getPostById(postId)
            if (result.isSuccess) {
                val data = result.getOrNull()!!
                _uiState.value = EditPostUiState.Success(data.first, data.second, data.third)
            } else {
                _uiState.value = EditPostUiState.Error("Không thể tải bài viết")
            }
        }
    }

    fun savePostChanges(
        postId: String,
        content: String,
        existingUrls: List<String>,
        newUris: List<Uri>,
        product: Product?,
        context: Context
    ) {
        viewModelScope.launch {
            _uiState.value = EditPostUiState.Loading
            try {
                // 1. Upload các URI mới lên Supabase Storage
                val uploadedUrls = newUris.mapNotNull { uri ->
                    feedRepository.uploadMedia(context, uri)
                }

                // 2. Trộn mảng URL cũ (chưa bị người dùng xóa) và URL mới vừa upload
                val finalMediaUrls = existingUrls + uploadedUrls

                // 3. Update DB
                val result = feedRepository.updatePostFull(postId, content, finalMediaUrls, product)

                if (result.isSuccess) {
                    _uiState.value = EditPostUiState.UpdateSuccess("Cập nhật bài viết thành công!")
                } else {
                    _uiState.value = EditPostUiState.Error("Lỗi cập nhật: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _uiState.value = EditPostUiState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }
}