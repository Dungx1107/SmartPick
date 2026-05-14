package com.example.smartpick.features.post_creation.viewmodel

/**
 * Đại diện cho trạng thái UI của màn hình tạo bài viết.
 */
sealed class CreatePostUiState {

    /* Trạng thái mặc định khi chưa thao tác gì. */
    object Idle : CreatePostUiState()

    /* Trạng thái đang xử lý đăng bài. */
    object Loading : CreatePostUiState()

    /* Trạng thái đăng bài thành công. */
    object Success : CreatePostUiState()

    /**
     * Trạng thái xảy ra lỗi.
     *
     * @property message Nội dung lỗi hiển thị cho UI
     */
    data class Error(
        val message: String
    ) : CreatePostUiState()
}