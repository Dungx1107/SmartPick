package com.example.smartpick.core.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String,
    @SerialName("receiver_id") val receiverId: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("post_id") val postId: String,
    val type: String,
    val content: String? = null,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: Instant
)