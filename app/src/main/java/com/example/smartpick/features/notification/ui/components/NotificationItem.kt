package com.example.smartpick.features.notification.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.features.notification.model.AppNotification
import com.example.smartpick.features.notification.model.NotificationType

@Composable
fun NotificationItem(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val (icon, iconBgColor, iconTintColor) = when (notification.type) {
        NotificationType.ORDER -> Triple(
            Icons.Outlined.LocalShipping,
            Color(0xFFE0F2FE),
            Color(0xFF0284C7)
        )

        NotificationType.COMMUNITY -> Triple(
            Icons.Outlined.ChatBubbleOutline,
            Color(0xFFF3E8FF),
            Color(0xFF9333EA)
        )

        NotificationType.PROMO -> Triple(
            Icons.Outlined.Loyalty,
            Color(0xFFFFEDD5),
            Color(0xFFEA580C)
        )

        NotificationType.SYSTEM -> Triple(
            Icons.Outlined.SettingsSuggest,
            Color(0xFFF1F5F9),
            Color(0xFF475569)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (notification.isUnread) Color(0xFFF1F5F9).copy(alpha = 0.5f) else Color.White)
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
                color = if (notification.isUnread) Color(0xFF1E3A8A) else Color(0xFF334155)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.message,
                fontSize = 14.sp,
                color = if (notification.isUnread) Color(0xFF475569) else Color(0xFF64748B),
                lineHeight = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.timeAgo,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
        }

        if (notification.isUnread) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444))
                    .align(Alignment.CenterVertically)
            )
        }
    }
}