package com.example.smartpick.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: String? = null,
    @SerialName("post_id") val postId: String,
    @SerialName("user_id") val userId: String,
    val content: String,
    @SerialName("parent_id") val parentId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)