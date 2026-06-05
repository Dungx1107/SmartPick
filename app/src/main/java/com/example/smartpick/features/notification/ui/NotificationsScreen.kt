package com.example.smartpick.features.notification.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.features.notification.data.AppNotification
import com.example.smartpick.features.notification.data.NotificationType
import com.example.smartpick.features.notification.ui.components.NotificationFilterRow
import com.example.smartpick.features.notification.ui.components.NotificationItem
import com.example.smartpick.features.notification.viewmodel.NotificationViewModel
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.SmartPickTheme

@Composable
fun NotificationsScreen(
    paddingValues: PaddingValues,
    viewModel: NotificationViewModel ,
    currentUserId: String,
    onNotificationClick: (AppNotification) -> Unit = {} // Lambda nhận sự kiện click từ bên ngoài NavHost
) {
    val labelAll = stringResource(R.string.TatCa)
    val labelUnread = stringResource(R.string.ChuaDoc)
    val labelOrder = stringResource(R.string.DonHang)
    val labelCommunity = stringResource(R.string.CongDong)
    val labelPromo = stringResource(R.string.KhuyenMai)
    val labelSystem = stringResource(R.string.HeThong)

    val notifications by viewModel.uiNotifications.collectAsState()
    var selectedFilter by rememberSaveable { mutableStateOf(labelAll) }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.subscribeToNotifications(currentUserId)
        }
    }

    val filteredNotifications = remember(notifications, selectedFilter) {
        when (selectedFilter) {
            labelUnread -> notifications.filter { it.isUnread }
            labelOrder -> notifications.filter { it.type == NotificationType.ORDER }
            labelCommunity -> notifications.filter { it.type == NotificationType.COMMUNITY }
            labelPromo -> notifications.filter { it.type == NotificationType.PROMO }
            labelSystem -> notifications.filter { it.type == NotificationType.SYSTEM }
            else -> notifications
        }
    }

    NotificationsContent(
        paddingValues = paddingValues,
        notifications = filteredNotifications,
        selectedFilter = selectedFilter,
        onFilterSelected = { selectedFilter = it },
        onNotificationClick = { notification ->
            // 1. Đánh dấu thông báo đã đọc dưới database thông qua ViewModel cục bộ
            viewModel.markAsRead(notification.id)
            // 2. Bắn thực thể thông báo ra ngoài cho NavGraph xử lý điều hướng màn hình
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
            .background(MaterialTheme.colorScheme.background)
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
                    onClick = { onNotificationClick(notification) } // Chuyển tiếp callback sạch, không lồng logic
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Notifications Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun NotificationsScreenPreview() {
    SmartPickTheme {
        val notifications = listOf(
            AppNotification("1", "Đơn hàng đang giao", "Bàn phím cơ Keychron Q1 Pro của bạn đang được giao đến.", "10 phút trước", NotificationType.ORDER, true),
            AppNotification("2", "Lê Hải An đã bình luận", "Gõ cực êm nha bác, build nhôm đầm tay lắm.", "45 phút trước", NotificationType.COMMUNITY, true),
            AppNotification("3", "Khuyến mãi cuối tuần!", "Giảm ngay 20% cho tất cả thiết bị âm thanh.", "2 giờ trước", NotificationType.PROMO, false),
            AppNotification("4", "Cập nhật ứng dụng", "SmartPick phiên bản mới đã sẵn sàng.", "1 ngày trước", NotificationType.SYSTEM, false),
            AppNotification("5", "Giao hàng thành công", "Đơn hàng #SP88921 đã được giao thành công.", "2 ngày trước", NotificationType.ORDER, false)
        )

        NotificationsContent(
            paddingValues = PaddingValues(0.dp),
            notifications = notifications,
            selectedFilter = "Tất cả",
            onFilterSelected = {},
            onNotificationClick = {}
        )
    }
}