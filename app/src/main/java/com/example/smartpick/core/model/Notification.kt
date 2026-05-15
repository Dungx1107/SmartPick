package com.example.smartpick.core.model

data class Notification(
    val id: String = "",
    val receiverId: String,
    val senderId: String? = null,
    val postId: String? = null,
    val type: String,
    val content: String,
    val isRead: Boolean = false,
    val createdAt: String? = null,
    val title: String,
    val targetId: String? = null
)