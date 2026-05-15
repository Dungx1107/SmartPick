package com.example.smartpick.features.notification.data

import Notification
import com.example.smartpick.core.utils.TimeFormatter


enum class NotificationType {
    ORDER,     // thông báo đơn hàng
    COMMUNITY,// tương tác cộng đồng
    PROMO, // khuyến mãi / ưu đãi
    SYSTEM,// thông báo hệ thống

    ;

    companion object {
        fun fromString(type: String): NotificationType {
            return entries.find { it.name == type } ?: SYSTEM
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
        id = this.id ?: "",
        title = this.title,
        content = this.content,
//        // Logic chuyển đổi thời gian (Cần thêm thư viện format date hoặc helper class)
//        timeAgo = this.createdAt?.let { /* Gọi hàm format date tại đây */ "Vừa xong" } ?: "",
        timeAgo = TimeFormatter.formatTimeAgo(this.createdAt),
        type = NotificationType.fromString(this.type),
        isUnread = !this.isRead,
        targetId = this.targetId
    )
}