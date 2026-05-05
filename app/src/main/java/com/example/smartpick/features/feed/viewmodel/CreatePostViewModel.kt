package com.example.smartpick.features.feed.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.feed.data.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel chịu trách nhiệm xử lý logic tạo bài post mới.
 *
 * Bao gồm:
 * - Lấy thông tin user hiện tại
 * - Gửi dữ liệu bài post (text, media, product) lên repository
 * - Quản lý trạng thái UI thông qua StateFlow
 */
@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    /* State nội bộ để quản lý trạng thái UI */
    private val _uiState = MutableStateFlow<CreatePostUiState>(CreatePostUiState.Idle)

    /* State public để UI observe */
    val uiState = _uiState.asStateFlow()

    /**
     * Hàm tạo bài post mới
     *
     * @param content Nội dung text của bài viết
     * @param mediaUris Danh sách URI của ảnh/video
     * @param product Sản phẩm được gắn kèm (có thể null)
     * @param context Context dùng cho xử lý upload media
     */
    fun createPost(
        content: String,
        mediaUris: List<Uri>,
        product: Product?,
        context: Context
    ) {
        viewModelScope.launch {
            /* Cập nhật trạng thái loading */
            _uiState.value = CreatePostUiState.Loading

            try {
                /* 1. Lấy thông tin user hiện tại */
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _uiState.value = CreatePostUiState.Error("Bạn chưa đăng nhập")
                    return@launch
                }

                /*
                 * 2. Gọi repository để:
                 * - Upload media (ảnh/video)
                 * - Lưu dữ liệu bài post vào database
                 */
                feedRepository.createFullPost(
                    userId = user.id,
                    content = content,
                    mediaUris = mediaUris,
                    productData = product,
                    context = context
                )

                /* 3. PHƯƠNG THỨC CHECK: Lấy lại bài vừa đăng để kiểm tra */
                val lastPost = feedRepository.checkLastPost(user.id)
                if (lastPost != null) {
                    Log.d("CHECK_DATA", "--- KIỂM TRA BÀI ĐĂNG ---")
                    Log.d("CHECK_DATA", "ID Người đăng: ${lastPost.userId}")
                    Log.d("CHECK_DATA", "Nội dung: ${lastPost.content}")
                    Log.d("CHECK_DATA", "Danh sách Media: ${lastPost.mediaUrls}")
                    Log.d("CHECK_DATA", "-------------------------")
                }

                /* 4. Thành công -> cập nhật state */
                Toast.makeText(context, "Đăng bài thành công!", Toast.LENGTH_SHORT).show()
                _uiState.value = CreatePostUiState.Success
            } catch (e: Exception) {
                /* 4. Xử lý lỗi */
                _uiState.value = CreatePostUiState.Error(
                    e.message ?: "Lỗi không xác định"
                )
            }
        }
    }
}

/**
 * Sealed class biểu diễn các trạng thái UI khi tạo post
 */
sealed class CreatePostUiState {

    /* Trạng thái ban đầu */
    object Idle : CreatePostUiState()

    /* Đang xử lý (loading) */
    object Loading : CreatePostUiState()

    /* Tạo post thành công */
    object Success : CreatePostUiState()

    /*
     * Có lỗi xảy ra
     * @param message thông báo lỗi
     */
    data class Error(val message: String) : CreatePostUiState()
}