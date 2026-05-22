package com.example.smartpick.features.review.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReviewResponse
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.review.data.ReviewRepository // Đổi sang ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewHubViewModel @Inject constructor(
    private val repository: ReviewRepository, // FIX: Sử dụng đúng Repository chuyên trách
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _productsToReview = MutableStateFlow<List<Product>>(emptyList())
    val productsToReview: StateFlow<List<Product>> = _productsToReview.asStateFlow()

    private val _reviewedProducts = MutableStateFlow<List<ReviewResponse>>(emptyList())
    val reviewedProducts: StateFlow<List<ReviewResponse>> = _reviewedProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchReviewData()
    }

    fun fetchReviewData() {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            user?.id?.let { uid ->
                _productsToReview.value = repository.getProductsToReview(uid)
                _reviewedProducts.value = repository.getMyReviewedProducts(uid)
            }
            _isLoading.value = false
        }
    }
}