package com.example.smartpick.features.notification.data

import com.example.smartpick.core.data.dto.NotificationDto
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.data.mapper.toDto
import com.example.smartpick.core.model.Notification
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val supabase: SupabaseClient
) {

    // Lắng nghe thay đổi bảng notifications theo thời gian thực
    fun observeNotifications(userId: String): Flow<List<Notification>> {
        val channel = supabase.channel("notifications_$userId")

        return channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "notifications"
            filter = "receiver_id=eq.$userId"
        }.map {
            // 1. Decode ra danh sách DTO thay vì Domain Model
            val listDto = supabase.postgrest.from("notifications")
                .select {
                    filter { eq("receiver_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<NotificationDto>()

            // 2. Map từng phần tử sang Domain Model dùng cho UI
            listDto.map { it.toDomain() }
        }.onStart {
            val initialDto = supabase.postgrest.from("notifications")
                .select {
                    filter { eq("receiver_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<NotificationDto>()

            emit(initialDto.map { it.toDomain() })
            channel.subscribe()
        }
    }
    // Gửi thông báo
    suspend fun sendNotification(notification: Notification): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                supabase.postgrest.from("notifications").insert(notification.toDto())
                println("DEBUG_NOTIFICATION: Gửi thông báo thành công cho ${notification.receiverId}")
                Result.success(Unit)
            } catch (e: Exception) {
                println("ERROR_NOTIFICATION: Lỗi khi gửi thông báo: ${e.message}")
                e.printStackTrace()
                Result.failure(e)
            }
        }

    // Đánh dấu đã đọc
    suspend fun markAsRead(notificationId: String) = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest.from("notifications").update(
                { set("is_read", true) }
            ) {
                filter {
                    eq("id", notificationId)
                }
            }
        } catch (e: Exception) {
            println("ERROR_NOTIFICATION: Lỗi khi đánh dấu đã đọc: ${e.message}")
        }
    }
}