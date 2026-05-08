package com.example.smartpick.features.post_creation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.post_creation.data.PostCreationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postCreationRepository: PostCreationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<CreatePostUiState>(CreatePostUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun createPost(
        content: String,
        mediaUris: List<Uri>,
        product: Product?,
        context: Context
    ) {
        viewModelScope.launch {
            _uiState.value = CreatePostUiState.Loading

            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _uiState.value = CreatePostUiState.Error("Bạn chưa đăng nhập")
                    return@launch
                }

                postCreationRepository.createFullPost(
                    userId = user.id,
                    content = content,
                    mediaUris = mediaUris,
                    productData = product,
                    context = context
                )

                Toast.makeText(context, "Đăng bài thành công!", Toast.LENGTH_SHORT).show()
                _uiState.value = CreatePostUiState.Success
            } catch (e: Exception) {
                Log.e("POST_CREATION", "Error: ${e.message}")
                _uiState.value = CreatePostUiState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }
}

sealed class CreatePostUiState {
    object Idle : CreatePostUiState()
    object Loading : CreatePostUiState()
    object Success : CreatePostUiState()
    data class Error(val message: String) : CreatePostUiState()
}
