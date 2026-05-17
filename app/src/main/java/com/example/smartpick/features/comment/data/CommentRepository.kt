package com.example.smartpick.features.comment.data

import android.util.Log
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.model.Comment
import com.example.smartpick.core.model.Notification
import com.example.smartpick.core.utils.Constants.TABLE_COMMENTS
import com.example.smartpick.core.utils.Constants.TABLE_COMMENT_LIKES
import com.example.smartpick.features.comment.data.dto.CommentResponse
import com.example.smartpick.features.notification.data.NotificationRepository
import com.example.smartpick.features.notification.data.NotificationType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository xử lý dữ liệu bình luận và tương tác comment.
 */
@Singleton
class CommentRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val notificationRepository: NotificationRepository
) {
    /**
     * Lấy toàn bộ comment của bài viết, tính toán likes_count và is_liked thông qua RPC.
     */
    suspend fun getComments(postId: String, currentUserId: String): List<Comment> =
        withContext(Dispatchers.IO) {
            /* Thay thế select thông thường bằng lệnh gọi hàm RPC get_comments_with_likes */
            val response = supabase.postgrest.rpc(
                "get_comments_with_likes",
                mapOf(
                    "current_post_id" to postId,
                    "current_user_id" to currentUserId
                )
            ).decodeList<CommentResponse>()

            return@withContext response.map { it.toDomain() }
        }

    /**
     * Thêm comment mới và gửi notification nếu cần.
     */
    suspend fun insertComment(
        postId: String,
        userId: String,
        receiverId: String,
        content: String,
        parentId: String? = null
    ) = withContext(Dispatchers.IO) {

        /* Tạo data comment để insert */
        val data = mutableMapOf(
            "post_id" to postId,
            "user_id" to userId,
            "content" to content.trim(),
            "created_at" to Clock.System.now().toString()
        )

        /* Nếu là reply thì thêm parent_id */
        if (parentId != null) {
            data["parent_id"] = parentId
        }

        Log.d("CommentDebug", "Repository INSERT: $data")        /* Log debug dữ liệu comment */
        supabase.postgrest[TABLE_COMMENTS].insert(data)        /* Lưu comment vào database */
        if (userId != receiverId) {        /* Không gửi notification cho chính mình */
            /* Đổi title theo loại comment */
            val notificationTitle =
                if (parentId == null) "Bình luận mới"
                else "Phản hồi mới"

            /* Tạo object notification */
            val notification = Notification(
                receiverId = receiverId,
                senderId = userId,
                postId = postId,
                type = NotificationType.COMMUNITY.databaseValue,
                title = notificationTitle,
                content = content.trim(),
                targetId = postId
            )

            /* Gửi notification */
            notificationRepository.sendNotification(notification)
        }
    }
// File: CommentRepository.kt

    suspend fun toggleLike(
        commentId: String,
        userId: String,
        isLiked: Boolean,
        commentOwnerId: String,
        postId: String
    ) = withContext(Dispatchers.IO) {
        android.util.Log.d("NotifDebug", "==================================================")
        android.util.Log.d("NotifDebug", "[SEND STEP 1] toggleLike gọi tại Repository")
        android.util.Log.d("NotifDebug", "   -> Người thực hiện (userId): $userId")
        android.util.Log.d("NotifDebug", "   -> Chủ bình luận (commentOwnerId): $commentOwnerId")
        android.util.Log.d("NotifDebug", "   -> Trạng thái click (isLiked): $isLiked")

        if (isLiked) {
            supabase.postgrest[TABLE_COMMENT_LIKES].delete {
                filter {
                    eq("comment_id", commentId)
                    eq("user_id", userId)
                }
            }
        } else {
            try {
                supabase.postgrest[TABLE_COMMENT_LIKES].insert(
                    mapOf(
                        "comment_id" to commentId,
                        "user_id" to userId
                    )
                )
                android.util.Log.d("NotifDebug", "[SEND STEP 2] Ghi nhận lượt thích vào bảng comment_likes thành công.")

                /* KIỂM TRA ĐIỀU KIỆN GỬI THÔNG BÁO */
                if (userId != commentOwnerId) {
                    android.util.Log.d("NotifDebug", "[SEND STEP 3] Thỏa mãn điều kiện gửi (Người thích khác chủ bình luận). Tiến hành build object.")
                    val notification = Notification(
                        receiverId = commentOwnerId,
                        senderId = userId,
                        postId = postId,
                        type = NotificationType.COMMUNITY.databaseValue,
                        title = "Lượt thích mới",
                        content = "Một người dùng đã thích bình luận của bạn.",
                        targetId = postId
                    )

                    val result = notificationRepository.sendNotification(notification)
                    android.util.Log.d("NotifDebug", "[SEND STEP 5] Đã thực hiện xong lệnh sendNotification. Kết quả Result: ${result.isSuccess}")
                } else {
                    android.util.Log.d("NotifDebug", "[SEND SKIPPED] Tự thích bình luận của chính mình ($userId == $commentOwnerId). Bỏ qua luồng gửi thông báo.")
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: ""
                if (errorMsg.contains("duplicate key") || errorMsg.contains("already exists")) {
                    android.util.Log.d("NotifDebug", "[IDEMPOTENT] Trùng khóa thích. Bỏ qua.")
                } else {
                    android.util.Log.e("NotifDebug", "[SEND ERROR] Lỗi phát sinh tại toggleLike: ${e.message}", e)
                    throw e
                }
            }
        }
    }
}