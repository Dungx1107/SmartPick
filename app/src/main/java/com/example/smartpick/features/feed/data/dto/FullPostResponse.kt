package com.example.smartpick.features.feed.data.dto

import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullPostResponse(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String? = null,
    val content: String? = null,
    @SerialName("media_urls") val mediaUrls: List<String> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    val users: User? = null,
    val products: Product? = null
)
