package com.example.smartpick.features.notification.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.features.notification.data.AppNotification
import com.example.smartpick.features.notification.data.NotificationType
import com.example.smartpick.features.notification.ui.components.NotificationFilterRow
import com.example.smartpick.features.notification.ui.components.NotificationItem
import com.example.smartpick.features.notification.viewmodel.NotificationViewModel

@Composable
fun NotificationsScreen(
    paddingValues: PaddingValues,
    viewModel: NotificationViewModel = hiltViewModel(),
    currentUserId: String, // Cần truyền ID người dùng hiện tại từ AppNavigation
    onNotificationClick: (AppNotification) -> Unit = {}
) {
    // 1. Lắng nghe dữ liệu từ ViewModel
    val notifications by viewModel.uiNotifications.collectAsState()
    var selectedFilter by rememberSaveable { mutableStateOf("Tất cả") }

    // 2. Kích hoạt lấy dữ liệu khi màn hình mở ra
    LaunchedEffect(currentUserId) {
        viewModel.subscribeToNotifications(currentUserId)
    }

    // 3. Logic lọc dữ liệu (Filter)
    val filteredNotifications = rememberSaveable(notifications, selectedFilter) {
        when (selectedFilter) {
            "Chưa đọc" -> notifications.filter { it.isUnread }
            "Đơn hàng" -> notifications.filter { it.type == NotificationType.ORDER }
            "Cộng đồng" -> notifications.filter { it.type == NotificationType.COMMUNITY }
            else -> notifications
        }
    }

    NotificationsContent(
        paddingValues = paddingValues,
        notifications = filteredNotifications, // Dùng danh sách đã lọc
        selectedFilter = selectedFilter,
        onFilterSelected = { selectedFilter = it },
        onNotificationClick = { notification ->
            viewModel.markAsRead(notification.id) // Đánh dấu đã đọc khi click
            onNotificationClick(notification)
        }
    )
}

@Composable
fun NotificationsContent(
    paddingValues: PaddingValues,
    notifications: List<AppNotification>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    onNotificationClick: (AppNotification) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(paddingValues)
    ) {
        NotificationFilterRow(
            selectedFilter = selectedFilter,
            onFilterSelected = onFilterSelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(notifications) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = { onNotificationClick(notification) }
                )
                HorizontalDivider(
                    color = Color(0xFFE2E8F0).copy(alpha = 0.5f),
                    thickness = 1.dp
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Notifications Screen",
    showBackground = true,
    backgroundColor = 0xFFF8FAFC,
    showSystemUi = true
)
@Composable
fun NotificationsScreenPreview() {

    val notifications = listOf(
        AppNotification(
            "1",
            "Đơn hàng đang giao",
            "Bàn phím cơ Keychron Q1 Pro của bạn đang được giao đến.",
            "10 phút trước",
            NotificationType.ORDER,
            true
        ),
        AppNotification(
            "2",
            "Lê Hải An đã bình luận",
            "Gõ cực êm nha bác, build nhôm đầm tay lắm.",
            "45 phút trước",
            NotificationType.COMMUNITY,
            true
        ),
        AppNotification(
            "3",
            "Khuyến mãi cuối tuần!",
            "Giảm ngay 20% cho tất cả thiết bị âm thanh.",
            "2 giờ trước",
            NotificationType.PROMO,
            false
        ),
        AppNotification(
            "4",
            "Cập nhật ứng dụng",
            "SmartPick phiên bản mới đã sẵn sàng.",
            "1 ngày trước",
            NotificationType.SYSTEM,
            false
        ),
        AppNotification(
            "5",
            "Giao hàng thành công",
            "Đơn hàng #SP88921 đã được giao thành công.",
            "2 ngày trước",
            NotificationType.ORDER,
            false
        )
    )

    NotificationsContent(
        paddingValues = PaddingValues(0.dp),
        notifications = notifications,
        selectedFilter = "Tất cả",
        onFilterSelected = {},
        onNotificationClick = {}
    )
}