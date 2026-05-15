package com.example.smartpick.features.notification.model

enum class NotificationType {
    ORDER, COMMUNITY, PROMO, SYSTEM
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: NotificationType,
    val isUnread: Boolean = false
)