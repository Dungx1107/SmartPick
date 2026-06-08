package com.example.smartpick.core.data.dto

@kotlinx.serialization.Serializable
data class NotificationInsertDto(
    @kotlinx.serialization.SerialName("receiver_id") val receiverId: String,
    @kotlinx.serialization.SerialName("sender_id") val senderId: String,
    @kotlinx.serialization.SerialName("post_id") val postId: String?,
    val type: String,
    val content: String?,
    val title: String = "Thông báo mới",
    @kotlinx.serialization.SerialName("target_id") val targetId: String? = null
)