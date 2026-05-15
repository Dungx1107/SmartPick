package com.example.smartpick.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String? = null,
    @SerialName("receiver_id") val receiverId: String,
    @SerialName("sender_id") val senderId: String? = null,
    @SerialName("post_id") val postId: String? = null,
    val type: String,
    val content: String,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    val title: String,
    @SerialName("target_id") val targetId: String? = null
)