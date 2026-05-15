package com.example.smartpick.features.notification.data

import com.example.smartpick.core.model.Notification
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