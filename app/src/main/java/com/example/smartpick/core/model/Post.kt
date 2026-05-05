package com.example.smartpick.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String? = null, // UUID sẽ do DB tự tạo hoặc mình tạo
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String? = null,
    val content: String? = null,
    @SerialName("media_urls") val mediaUrls: List<String> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null
)
