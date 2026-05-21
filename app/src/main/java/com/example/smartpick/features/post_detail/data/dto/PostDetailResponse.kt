package com.example.smartpick.features.post_detail.data.dto

import com.example.smartpick.core.data.dto.ProductDto
import com.example.smartpick.core.data.dto.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDetailResponse(
    val id: String,
    val content: String?,
    @SerialName("media_urls") val mediaUrls: List<String>,
    @SerialName("created_at") val createdAt: String?,
    @SerialName("users") val user: UserDto, // Chuyển từ User sang UserDto
    @SerialName("products") val product: ProductDto?, // Chuyển từ Product sang ProductDto
    @SerialName("likes_count") val likesCount: Int = 0,
    @SerialName("comments_count") val commentsCount: Int = 0,
    @SerialName("is_liked") val isLiked: Boolean = false,
    @SerialName("shared_post_id") val sharedPostId: String? = null,
    @SerialName("shared_post") val sharedPost: PostDetailResponse? = null

)