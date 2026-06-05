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
    ): String = withContext(Dispatchers.IO) {

        val commentId = java.util.UUID.randomUUID().toString()
        val createdAt = Clock.System.now().toString()

        val data = mutableMapOf(
            "id" to commentId,
            "post_id" to postId,
            "user_id" to userId,
            "content" to content.trim(),
            "created_at" to createdAt
        )

        if (parentId != null) {
            data["parent_id"] = parentId
        }

        // Thực hiện chèn vào Database công khai
        supabase.postgrest[TABLE_COMMENTS].insert(data)

        return@withContext commentId 
    }
    suspend fun toggleLike(
        commentId: String,
        userId: String,
        isLiked: Boolean,
        commentOwnerId: String,
        postId: String
    ) = withContext(Dispatchers.IO) {
        Log.d("NotifDebug", "==================================================")
        Log.d("NotifDebug", "[SEND STEP 1] toggleLike gọi tại Repository")
        Log.d("NotifDebug", "   -> Người thực hiện (userId): $userId")
        Log.d("NotifDebug", "   -> Chủ bình luận (commentOwnerId): $commentOwnerId")
        Log.d("NotifDebug", "   -> Trạng thái click (isLiked): $isLiked")

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
                Log.d("NotifDebug", "[SEND STEP 2] Ghi nhận lượt thích vào bảng comment_likes thành công.")
            } catch (e: Exception) {
                val errorMsg = e.message ?: ""
                if (errorMsg.contains("duplicate key") || errorMsg.contains("already exists")) {
                    Log.d("NotifDebug", "[IDEMPOTENT] Trùng khóa thích. Bỏ qua.")
                } else {
                    Log.e("NotifDebug", "[SEND ERROR] Lỗi phát sinh tại toggleLike: ${e.message}", e)
                    throw e
                }
            }
        }
    }
}