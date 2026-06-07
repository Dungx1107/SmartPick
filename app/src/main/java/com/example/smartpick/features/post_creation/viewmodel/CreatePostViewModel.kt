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
    private val _uiState =
        MutableStateFlow<CreatePostUiState>(
            CreatePostUiState.Idle
        )
    val uiState = _uiState.asStateFlow()

    fun clearError() {
        if (_uiState.value is CreatePostUiState.Error) {
            _uiState.value = CreatePostUiState.Idle
        }
    }

    fun createPost(
        content: String,
        mediaUris: List<Uri>,
        product: Product?,
        context: Context
    ) {


        viewModelScope.launch {

            /* Chuyển UI sang trạng thái Loading. */
            _uiState.value = CreatePostUiState.Loading

            try {
                /* Lấy thông tin user hiện tại đang đăng nhập. */
                val user = authRepository.getCurrentUser()

                /**
                 * Nếu chưa đăng nhập:
                 * - Hiển thị lỗi
                 * - Dừng coroutine
                 */
                if (user == null) {
                    Log.e("POST_CREATION", "Đăng bài thất bại: Thao tác yêu cầu user đăng nhập.")
                    _uiState.value =
                        CreatePostUiState.Error(
                            "Bạn chưa đăng nhập"
                        )
                    return@launch
                }

                /**
                 * Gọi Repository để tạo bài viết hoàn chỉnh.
                 *
                 * Bao gồm:
                 * - Upload media
                 * - Upload product
                 * - Tạo post trong database
                 */
                postCreationRepository.createFullPost(
                    userId = user.id,
                    content = content,
                    mediaUris = mediaUris,
                    productData = product,
                    context = context
                )

                /* Hiển thị thông báo đăng bài thành công. */
                Toast.makeText(
                    context,
                    "Đăng bài thành công!",
                    Toast.LENGTH_SHORT
                ).show()

                /* Chuyển UI sang trạng thái Success. */
                _uiState.value = CreatePostUiState.Success

            } catch (e: Exception) {

                /**
                 * Nếu xảy ra lỗi:
                 * - In log lỗi
                 * - Chuyển UI sang Error
                 */
                Log.e("POST_CREATION", "Quá trình đăng bài xảy ra ngoại lệ: ", e)

                _uiState.value =
                    CreatePostUiState.Error(
                        e.message ?: "Lỗi không xác định"
                    )
            }
        }
    }
}

