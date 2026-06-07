package com.example.smartpick.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewUserDto(
    val id: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null
)

@Serializable
data class ReviewRequestDto(
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("order_item_id") val orderItemId: String,
    val rating: Int,
    val content: String
)

@Serializable
data class ReviewResponseDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("order_item_id") val orderItemId: String? = null,
    val rating: Int,
    val content: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("products") val products: ProductDto? = null,
    @SerialName("users") val user: ReviewUserDto? = null
)
