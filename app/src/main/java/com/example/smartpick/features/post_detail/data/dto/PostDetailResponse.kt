package com.example.smartpick.features.post_detail.data.dto

import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDetailResponse(
    val id: String,
    val content: String?,
    @SerialName("media_urls") val mediaUrls: List<String>,
    @SerialName("created_at") val createdAt: String?,

    @SerialName("users") val user: User,
    @SerialName("products") val product: Product?,

    // Thông tin tương tác (Aggregated data)
    @SerialName("likes_count") val likesCount: Int = 0,
    @SerialName("comments_count") val commentsCount: Int = 0,

    // Trạng thái cá nhân hóa
    @SerialName("is_liked") val isLiked: Boolean = false
)