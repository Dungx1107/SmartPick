package com.example.smartpick.features.review.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Review
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.review.data.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val repository: ReviewRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _productReviews = MutableStateFlow<List<Review>>(emptyList())
    val productReviews: StateFlow<List<Review>> = _productReviews.asStateFlow()

    private val _canReview = MutableStateFlow(false)
    val canReview: StateFlow<Boolean> = _canReview.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    /**
     * Tải danh sách đánh giá và kiểm tra điều kiện của user đối với sản phẩm hiện tại
     */
    fun loadReviewData(productId: String) {
        viewModelScope.launch {
            // Chạy song song cả 2 tác vụ để tối ưu thời gian phản hồi UI
            launch {
                _productReviews.value = repository.getProductReviews(productId)
            }
            launch {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    _canReview.value = repository.checkUserBoughtProduct(user.id, productId)
                } else {
                    _canReview.value = false
                }
            }
        }
    }

    /**
     * Thực hiện gửi đánh giá từ người dùng
     */
    fun submitProductReview(
        productId: String,
        rating: Int,
        content: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (content.isBlank()) {
            onError("Nội dung đánh giá không được để trống")
            return
        }

        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user == null) {
                onError("Vui lòng đăng nhập để thực hiện tính năng này")
                return@launch
            }

            _isSubmitting.value = true

            val result = repository.submitReview(
                userId = user.id,
                productId = productId,
                rating = rating,
                content = content
            )
            _isSubmitting.value = false

            if (result.isSuccess) {
                // Tải lại danh sách review mới sau khi submit thành công
                loadReviewData(productId)
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Lỗi gửi đánh giá hệ thống")
            }
        }
    }
}