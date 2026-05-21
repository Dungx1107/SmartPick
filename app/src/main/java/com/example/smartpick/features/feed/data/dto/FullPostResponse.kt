package com.example.smartpick.features.feed.data.dto

import com.example.smartpick.core.data.dto.PostDto
import com.example.smartpick.core.data.dto.ProductDto
import com.example.smartpick.core.data.dto.UserDto
import com.example.smartpick.core.data.dto.PostReactionDto
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
    // Sử dụng nullable để chống lỗi parse khi dữ liệu nested trống
    val users: UserDto? = null,
    val products: ProductDto? = null,
    @SerialName("post_reactions") val postReactions: List<PostReactionDto>? = emptyList()
)