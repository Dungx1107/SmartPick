// File: app/src/main/java/com/example/smartpick/core/model/ReviewModels.kt
package com.example.smartpick.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    val rating: Int,
    val content: String
)

@Serializable
data class ReviewResponse(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    val rating: Int,
    val content: String,
    @SerialName("created_at") val createdAt: String,
    // Thêm để lấy thông tin sản phẩm khi xem lịch sử đánh giá
    val products: Product? = null
)
