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

    private var notificationJob: Job? = null    // Khai báo Job để quản lý luồng real-time


    fun subscribeToNotifications(userId: String) {
        notificationJob?.cancel()

        viewModelScope.launch {
            repository.observeNotifications(userId).collect { dataList ->
                val sortedDataList = dataList.sortedByDescending { it.createdAt ?: "9999-12-31T23:59:59Z" }

                val mappedList = sortedDataList.map { it.toUiModel() }
                _uiNotifications.value = mappedList

                val unreadItems = sortedDataList.filter { !it.isRead }

                _unreadCount.value = unreadItems.size

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