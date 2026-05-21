package com.example.smartpick.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewUser(
    val id: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null
)

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
    val products: Product? = null,
    @SerialName("users") val user: ReviewUser? = null
)