package com.example.smartpick.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke

// 1. Định nghĩa các loại thông báo
enum class NotificationType {
    ORDER,     // Đơn hàng
    COMMUNITY, // Bình luận/Tương tác
    PROMO,     // Khuyến mãi
    SYSTEM     // Hệ thống
}

// 2. Data class cho Thông báo
data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: NotificationType,
    val isUnread: Boolean = false
)

@Composable
fun NotificationsScreen(
    paddingValues: PaddingValues, // Nhận padding từ Scaffold cha chứa TopBar/BottomBar
    onNotificationClick: (AppNotification) -> Unit = {}
) {
    // Dữ liệu mẫu
    val notifications = listOf(
        AppNotification("1", "Đơn hàng đang giao", "Bàn phím cơ Keychron Q1 Pro của bạn đang được giao đến. Vui lòng chú ý điện thoại.", "10 phút trước", NotificationType.ORDER, true),
        AppNotification("2", "Lê Hải An đã bình luận", "Gõ cực êm nha bác, build nhôm đầm tay lắm. Nên mua switch red nhen! 🔥", "45 phút trước", NotificationType.COMMUNITY, true),
        AppNotification("3", "Khuyến mãi cuối tuần!", "Giảm ngay 20% cho tất cả thiết bị âm thanh và màn hình. Nhập mã WEEKEND20 ngay.", "2 giờ trước", NotificationType.PROMO, false),
        AppNotification("4", "Cập nhật ứng dụng", "SmartPick phiên bản mới đã sẵn sàng. Trải nghiệm mượt mà hơn và thêm tính năng mới.", "1 ngày trước", NotificationType.SYSTEM, false),
        AppNotification("5", "Giao hàng thành công", "Đơn hàng #SP88921 (Giá đỡ màn hình Human Motion) đã được giao thành công.", "2 ngày trước", NotificationType.ORDER, false)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(paddingValues) // Áp dụng padding để không lẹm Top/Bottom Bar
    ) {
        // Bộ lọc danh mục thông báo (giữ cố định phía trên)
        NotificationFilterRow()
        Spacer(modifier = Modifier.height(8.dp))

        // Danh sách thông báo
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(notifications) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = { onNotificationClick(notification) }
                )
                // Đường kẻ gạch dưới mờ giữa các thông báo
                HorizontalDivider(color = Color(0xFFE2E8F0).copy(alpha = 0.5f), thickness = 1.dp)
            }
        }
    }
}

@Composable
fun NotificationFilterRow() {
    val filters = listOf("Tất cả", "Chưa đọc", "Đơn hàng", "Cộng đồng")
    var selectedFilter by remember { mutableStateOf(filters[0]) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC)) // Match with main background
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter == selectedFilter
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) Color(0xFF1E3A8A) else Color.White,
                border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
                modifier = Modifier.clickable { selectedFilter = filter }
            ) {
                Text(
                    text = filter,
                    color = if (isSelected) Color.White else Color(0xFF64748B),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: AppNotification,
    onClick: () -> Unit
) {
    // Tùy chỉnh Icon và Màu nền icon theo Loại thông báo
    val (icon: ImageVector, iconBgColor: Color, iconTintColor: Color) = when (notification.type) {
        NotificationType.ORDER -> Triple(Icons.Outlined.LocalShipping, Color(0xFFE0F2FE), Color(0xFF0284C7)) // Xanh dương nhạt
        NotificationType.COMMUNITY -> Triple(Icons.Outlined.ChatBubbleOutline, Color(0xFFF3E8FF), Color(0xFF9333EA)) // Tím
        NotificationType.PROMO -> Triple(Icons.Outlined.Loyalty, Color(0xFFFFEDD5), Color(0xFFEA580C)) // Cam
        NotificationType.SYSTEM -> Triple(Icons.Outlined.SettingsSuggest, Color(0xFFF1F5F9), Color(0xFF475569)) // Xám
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Nền hơi xanh nhẹ nếu chưa đọc, trắng nếu đã đọc
            .background(if (notification.isUnread) Color(0xFFF1F5F9).copy(alpha = 0.5f) else Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTintColor, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Nội dung
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

        // Dấu chấm đỏ báo chưa đọc
        if (notification.isUnread) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444)) // Đỏ nổi bật
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen(
        paddingValues = PaddingValues(0.dp)
    )
}