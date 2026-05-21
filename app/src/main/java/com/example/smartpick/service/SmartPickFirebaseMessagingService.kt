package com.example.smartpick.service

import android.util.Log
import com.example.smartpick.core.utils.NotificationHelper
import com.example.smartpick.features.notification.data.NotificationRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmartPickFirebaseMessagingService : FirebaseMessagingService() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var repository: NotificationRepository

    @Inject
    lateinit var supabase: SupabaseClient

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Token mới: $token")

        scope.launch {
            uploadTokenToSupabase(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Lấy dữ liệu từ block 'data' (từ Server gửi về) hoặc 'notification' (từ Firebase Console)
        val data = message.data
        val title = data["title"] ?: message.notification?.title ?: "SmartPick"
        val body = data["content"] ?: message.notification?.body ?: "Bạn có thông báo mới"

        val type = data["type"] ?: "system"
        val postId = data["post_id"]
        val targetId = data["target_id"]

        // Xây dựng cấu trúc deep link nội bộ ứng dụng
        val deepLink = "smartpick://notification/$type?post_id=$postId&target_id=$targetId"
        val notificationId = data["id"]?.hashCode() ?: System.currentTimeMillis().toInt()

        // Hiển thị thông báo lên Tray (hoạt động cả khi app đang mở)
        NotificationHelper.showNotification(
            context = this,
            title = title,
            body = body,
            deepLink = deepLink,
            notificationId = notificationId
        )
    }

    private suspend fun uploadTokenToSupabase(token: String) {
        try {
            Log.d("FCM_SERVICE", "Đang chuẩn bị đồng bộ token lên DB...")
            val currentUser = supabase.auth.currentUserOrNull()
            if (currentUser != null) {
                repository.upsertPushToken(token, currentUser.id)
            } else {
                Log.d("FCM_SERVICE", "Chưa login, token sẽ được sync sau.")
            }
        } catch (e: Exception) {
            Log.e("FCM_SERVICE", "Lỗi đồng bộ token", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}