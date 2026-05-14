package com.example.smartpick.features.comment.data.dto

import com.example.smartpick.core.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val id: String,
    @SerialName("post_id") val postId: String,
    @SerialName("user_id") val userId: String,
    val content: String,
    @SerialName("created_at") val createdAt: String,

    // Thông tin Join từ bảng users
    @SerialName("users") val user: User,

    // Dữ liệu tính toán (Aggregated)
    @SerialName("likes_count") val likesCount: Int = 0,
    @SerialName("is_liked") val isLiked: Boolean = false,
    @SerialName("parent_id") val parentId: String? = null

)
