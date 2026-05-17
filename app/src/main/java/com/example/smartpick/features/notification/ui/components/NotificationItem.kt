package com.example.smartpick.features.notification.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.features.notification.data.AppNotification
import com.example.smartpick.features.notification.data.NotificationType

@Composable
fun NotificationItem(
    notification: AppNotification,
    onClick: () -> Unit
) {
    // Mapping colors from hex to meaningful system colors or theme-aware colors
    val (icon, iconBgColor, iconTintColor) = when (notification.type) {
        NotificationType.ORDER -> Triple(
            Icons.Outlined.LocalShipping,
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.primary
        )

        NotificationType.COMMUNITY -> Triple(
            Icons.Outlined.ChatBubbleOutline,
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.secondary
        )

        NotificationType.PROMO -> Triple(
            Icons.Outlined.Loyalty,
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.tertiary
        )

        NotificationType.SYSTEM -> Triple(
            Icons.Outlined.SettingsSuggest,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (notification.isUnread) 
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) 
                else 
                    MaterialTheme.colorScheme.surface
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                fontSize = 15.sp,
                fontWeight = if (notification.isUnread) FontWeight.Bold else FontWeight.SemiBold,
                color = if (notification.isUnread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.content,
                fontSize = 14.sp,
                color = if (notification.isUnread) MaterialTheme.colorScheme.onSurface else TextMuted,
                lineHeight = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.timeAgo,
                fontSize = 12.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )
        }

        if (notification.isUnread) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}


@androidx.compose.ui.tooling.preview.Preview(
    name = "Unread Notification",
    showBackground = true
)
@Composable
fun NotificationItemUnreadPreview() {
    SmartPickTheme {
        NotificationItem(
            notification = AppNotification(
                id = "1",
                title = "Đơn hàng đang giao",
                content = "Bàn phím cơ Keychron Q1 Pro của bạn đang được giao đến.",
                timeAgo = "10 phút trước",
                type = NotificationType.ORDER,
                isUnread = true
            ),
            onClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Read Notification",
    showBackground = true
)
@Composable
fun NotificationItemReadPreview() {
    SmartPickTheme {
        NotificationItem(
            notification = AppNotification(
                id = "2",
                title = "Khuyến mãi cuối tuần!",
                content = "Giảm ngay 20% cho tất cả thiết bị âm thanh trong hôm nay.",
                timeAgo = "2 giờ trước",
                type = NotificationType.PROMO,
                isUnread = false
            ),
            onClick = {}
        )
    }
}