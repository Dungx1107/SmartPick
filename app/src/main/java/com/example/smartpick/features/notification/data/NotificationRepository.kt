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
        }.map { action ->
            println("DEBUG_NOTIFICATION: [Real-time Event] Phát hiện thay đổi hành động: ${action.toString()}")

            // 1. Decode ra danh sách DTO thay vì Domain Model
            val listDto = supabase.postgrest.from("notifications")
                .select {
                    filter { eq("receiver_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<NotificationDto>()

            println("DEBUG_NOTIFICATION: [Real-time Fetch] Danh sách DTO thô từ DB (${listDto.size} mục):")
            listDto.forEachIndexed { index, dto ->
                println("   -> DTO [$index]: ID=${dto.id}, Type=${dto.type}, Title='${dto.title}', CreatedAt=${dto.createdAt}")
            }

            // 2. Map từng phần tử sang Domain Model dùng cho UI
            val domainList = listDto.map { it.toDomain() }

            println("DEBUG_NOTIFICATION: [Real-time Mapped] Danh sách Domain sau khi Map:")
            domainList.forEachIndexed { index, domain ->
                println("   -> Domain [$index]: ID=${domain.id}, Type=${domain.type}, Title='${domain.title}'")
            }

            domainList
        }.onStart {
            println("DEBUG_NOTIFICATION: [Initial Fetch] Bắt đầu lấy dữ liệu lần đầu cho userId: $userId")

            val initialDto = supabase.postgrest.from("notifications")
                .select {
                    filter { eq("receiver_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<NotificationDto>()

            println("DEBUG_NOTIFICATION: [Initial Fetch] Danh sách DTO ban đầu thu được (${initialDto.size} mục):")
            initialDto.forEachIndexed { index, dto ->
                println("   -> DTO ban đầu [$index]: ID=${dto.id}, Type=${dto.type}, Title='${dto.title}', CreatedAt=${dto.createdAt}")
            }

            emit(initialDto.map { it.toDomain() })
            channel.subscribe()
            println("DEBUG_NOTIFICATION: [Real-time Subscribed] Đã kết nối channel lắng nghe real-time.")
        }
    }

    // Gửi thông báo
    suspend fun sendNotification(notification: Notification): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val dto = notification.toDto()

                println("DEBUG_NOTIFICATION: [Prepare Insert] Chuẩn bị gửi thông báo:")
                println("   -> Dữ liệu gốc (Domain): ID='${notification.id}', Type='${notification.type}', Title='${notification.title}'")
                println("   -> Dữ liệu chuyển đổi (DTO): ID='${dto.id}', Type='${dto.type}', ReceiverId='${dto.receiverId}'")

                supabase.postgrest.from("notifications").insert(dto)

                println("DEBUG_NOTIFICATION: [Insert Success] Gửi thông báo thành công cho receiver_id: ${notification.receiverId}")
                Result.success(Unit)
            } catch (e: Exception) {
                println("ERROR_NOTIFICATION: [Insert Failed] Lỗi khi gửi thông báo: ${e.message}")
                e.printStackTrace()
                Result.failure(e)
            }
        }

    // Đánh dấu đã đọc
    suspend fun markAsRead(notificationId: String) = withContext(Dispatchers.IO) {
        try {
            println("DEBUG_NOTIFICATION: [Mark As Read] Thực hiện cập nhật is_read = true cho ID: $notificationId")

            supabase.postgrest.from("notifications").update(
                { set("is_read", true) }
            ) {
                filter {
                    eq("id", notificationId)
                }
            }
        } catch (e: Exception) {
            println("ERROR_NOTIFICATION: [Mark As Read Failed] Lỗi khi đánh dấu đã đọc: ${e.message}")
        }
    }
}