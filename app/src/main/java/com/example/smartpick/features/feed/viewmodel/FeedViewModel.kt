package com.example.smartpick.features.feed.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.features.feed.data.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel quản lý dữ liệu cho màn hình Feed.
 *
 * Nhiệm vụ chính:
 * - Gọi Repository để lấy dữ liệu feed
 * - Quản lý trạng thái UI bằng StateFlow
 * - Xử lý loading / success / error
 *
 * @property feedRepository Repository dùng để lấy dữ liệu feed
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel() {

    /**
     * StateFlow nội bộ có thể thay đổi dữ liệu.
     * Ban đầu sẽ là trạng thái Loading.
     */
    private val _uiState =
        MutableStateFlow<FeedUiState>(FeedUiState.Loading)

    /**
     * StateFlow public chỉ cho phép đọc.
     * UI sẽ observe biến này để cập nhật giao diện.
     */
    val uiState: StateFlow<FeedUiState> =
        _uiState.asStateFlow()

    /**
     * init block sẽ tự động chạy khi ViewModel được tạo.
     * Dùng để tải dữ liệu feed lần đầu.
     */
    init {
        loadFeed()
    }

    /**
     * Hàm tải dữ liệu feed từ Repository.
     *
     * Flow xử lý:
     * 1. Chuyển UI sang trạng thái Loading
     * 2. Gọi Repository lấy dữ liệu
     * 3. Nếu thành công -> cập nhật Success
     * 4. Nếu lỗi -> cập nhật Error
     *
     * Sử dụng viewModelScope.launch để chạy coroutine
     * an toàn theo vòng đời ViewModel.
     */
    fun loadFeed() {

        /**
         * viewModelScope: Coroutine sẽ tự hủy khi ViewModel bị destroy.
         */
        viewModelScope.launch {

            /* Ghi log để debug quá trình tải dữ liệu.*/
            Log.d(TAG, "Bắt đầu tải dữ liệu Feed...")

            /**
             * Chuyển UI sang trạng thái Loading.
             * UI có thể hiển thị progress bar tại đây.
             */
            _uiState.value = FeedUiState.Loading

            try {
                /**
                 * Gọi Repository để lấy danh sách bài viết
                 * kèm thông tin User và Product.
                 */
                val posts = feedRepository.getPostsWithUsers()

                /* Log số lượng bài viết tải được. */
                Log.d(TAG, "Tải thành công: ${posts.size} bài viết.")

                /* In chi tiết từng bài viết để debug. */
                posts.forEachIndexed { index, item ->
                    Log.d(
                        TAG,
                        "Post[$index]: " +
                                "ID=${item.first.id}, " +
                                "User=${item.second.fullName}, " +
                                "HasProduct=${item.third != null}"
                    )
                }

                /**
                 * Cập nhật UI sang trạng thái Success
                 * và truyền dữ liệu cho giao diện.
                 */
                _uiState.value = FeedUiState.Success(posts)

            } catch (e: Exception) {

                /**
                 * Nếu có lỗi:
                 * - Ghi log lỗi
                 * - Chuyển UI sang trạng thái Error
                 */
                Log.e(TAG, "Lỗi khi tải Feed: ${e.message}", e)

                _uiState.value =
                    FeedUiState.Error(
                        e.message ?: "Lỗi hệ thống không xác định"
                    )
            }
        }
    }
}