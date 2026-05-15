package com.example.smartpick.features.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.features.notification.data.NotificationRepository
import com.example.smartpick.features.notification.data.AppNotification
import com.example.smartpick.features.notification.data.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.filter
import kotlin.collections.map

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    private val _latestNotification = MutableStateFlow<AppNotification?>(null)
    val latestNotification = _latestNotification.asStateFlow()

    private val _uiNotifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val uiNotifications = _uiNotifications.asStateFlow()

    // Khai báo Job để quản lý luồng real-time
    private var notificationJob: Job? = null

    fun subscribeToNotifications(userId: String) {
        // Hủy luồng lắng nghe cũ nếu có, tránh duplicate connection
        notificationJob?.cancel()

        viewModelScope.launch {

            // repository.observeNotifications trả về Flow<List<Notification>>
            repository.observeNotifications(userId).collect { dataList ->
                // dataList là List<Notification>

                // 1. Map sang UI Model
                val mappedList = dataList.map { it.toUiModel() }
                _uiNotifications.value = mappedList

                // 2. Lọc các thông báo chưa đọc
                // Nếu compiler không biết Notification là gì, 'it' sẽ bị đỏ
                val unreadItems = dataList.filter { !it.isRead }

                // 3. Cập nhật số lượng (size sẽ không lỗi nếu dataList được nhận diện là List)
                _unreadCount.value = unreadItems.size

                // 4. Tìm thông báo mới nhất
                if (unreadItems.isNotEmpty()) {
                    val latest = unreadItems.maxByOrNull { it.createdAt ?: "" }
                    _latestNotification.value = latest?.toUiModel()
                }
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markAsRead(notificationId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        notificationJob?.cancel()
    }
}