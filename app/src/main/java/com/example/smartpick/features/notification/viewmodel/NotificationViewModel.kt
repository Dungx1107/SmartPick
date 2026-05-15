package com.example.smartpick.features.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Notification
import com.example.smartpick.features.notification.data.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    private val _latestNotification = MutableStateFlow<Notification?>(null)
    val latestNotification = _latestNotification.asStateFlow()

    // Thêm StateFlow để giữ danh sách thông báo thực tế
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    fun subscribeToNotifications(userId: String) {
        viewModelScope.launch {
            repository.observeNotifications(userId).collect { list ->
                _notifications.value = list // Cập nhật danh sách thực
                val unread = list.filter { !it.isRead }
                _unreadCount.value = unread.size
                if (unread.isNotEmpty()) {
                    _latestNotification.value = unread.maxBy { it.createdAt }
                }
            }
        }
    }
}