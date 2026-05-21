package com.example.smartpick.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String? = null,
    val content: String? = null,
    @SerialName("media_urls") val mediaUrls: List<String> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class PostReactionDto(
    val id: String? = null,
    @SerialName("post_id") val postId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("reaction_type") val reactionType: String,
    @SerialName("created_at") val createdAt: String? = null
)

// FIX: DTO chuyên dùng để INSERT (Bỏ id và created_at để Supabase tự sinh tự động)
@Serializable
data class PostReactionInsertDto(
    @SerialName("post_id") val postId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("reaction_type") val reactionType: String
)