package com.example.smartpick.features.notification.data

import Notification
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val supabase: SupabaseClient
) {

    suspend fun sendNotification(notification: Notification): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabase.from("notifications").insert(notification)
            println("DEBUG_NOTIFICATION: Gửi thông báo thành công cho ${notification.receiverId}")
            Result.success(Unit)
        } catch (e: Exception) {
            println("ERROR_NOTIFICATION: Lỗi khi gửi thông báo: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    // Lấy notifications
    fun observeNotifications(userId: String): Flow<List<Notification>> = flow {

        val result = supabase
            .from("notifications")
            .select {
                filter {
                    eq("receiver_id", userId)
                }
            }
            .decodeList<Notification>()

        emit(result)
    }

    // Đánh dấu đã đọc
    suspend fun markAsRead(notificationId: String) = withContext(Dispatchers.IO) {

        supabase
            .from("notifications")
            .update(
                {
                    set("is_read", true)
                }
            ) {
                filter {
                    eq("id", notificationId)
                }
            }
    }
}