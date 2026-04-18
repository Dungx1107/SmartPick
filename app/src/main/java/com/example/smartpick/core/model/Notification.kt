package com.example.smartpick.core.model

import kotlinx.serialization.SerialName


enum class NotificationType {
    @SerialName("like")
    LIKE,

    @SerialName("comment")
    COMMENT,

    @SerialName("follow")
    FOLLOW,

    @SerialName("system")
    SYSTEM
}

data class Notification(
    val id: String,
    val type: NotificationType,
    val postID: String,
    val isRead: Boolean = false,
    val createAt: String,
    val senderId: String,
    val receiverId: String
)
