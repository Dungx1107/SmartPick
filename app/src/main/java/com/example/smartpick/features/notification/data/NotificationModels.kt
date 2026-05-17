package com.example.smartpick.features.notification.data

import com.example.smartpick.core.model.Notification
import com.example.smartpick.core.utils.TimeFormatter
enum class NotificationType(val databaseValue: String) {
    ORDER("ORDER"),
    COMMUNITY("COMMUNITY"),
    PROMO("PROMO"),
    SYSTEM("SYSTEM");

    companion object {
        fun fromString(type: String): NotificationType {
            return when (type.trim().lowercase()) {
                "order" -> ORDER
                "promo" -> PROMO
                "community", "comment", "like" -> COMMUNITY
                else -> SYSTEM
            }
        }
    }
}

data class AppNotification(
    val id: String,
    val title: String,
    val content: String,
    val timeAgo: String,
    val type: NotificationType,
    val isUnread: Boolean,
    val targetId: String? = null
)

// 3. Hàm Mapper (Kết nối 2 class)
fun Notification.toUiModel(): AppNotification {
    return AppNotification(
        id = this.id,
        title = this.title,
        content = this.content,
        timeAgo = TimeFormatter.formatTimeAgo(this.createdAt),
        type = NotificationType.fromString(this.type),
        isUnread = !this.isRead,
        targetId = this.targetId
    )
}