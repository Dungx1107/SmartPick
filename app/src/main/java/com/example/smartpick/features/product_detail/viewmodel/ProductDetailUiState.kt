package com.example.smartpick.features.product_detail.viewmodel

import com.example.smartpick.core.model.Product

/**
 * Định nghĩa trạng thái UI cho màn hình Chi tiết sản phẩm
 */
data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val error: String? = null
)