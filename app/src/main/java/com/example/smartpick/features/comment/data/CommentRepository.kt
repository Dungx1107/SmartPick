package com.example.smartpick.features.comment.data

import com.example.smartpick.core.model.Notification
import android.util.Log
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.model.Comment
import com.example.smartpick.core.utils.Constants.TABLE_COMMENTS
import com.example.smartpick.core.utils.Constants.TABLE_COMMENT_LIKES
import com.example.smartpick.features.comment.data.dto.CommentResponse
import com.example.smartpick.features.notification.data.NotificationRepository
import com.example.smartpick.features.notification.data.NotificationType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
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
     * Lấy toàn bộ comment của bài viết,
     * kèm thông tin user và sắp xếp theo thời gian.
     */
    suspend fun getComments(postId: String): List<Comment> = withContext(Dispatchers.IO) {
        /* Query danh sách comment từ Supabase */
        val response = supabase.postgrest[TABLE_COMMENTS]
            .select(columns = Columns.raw("*, users(*)")) {
                filter { eq("post_id", postId) }                /* Lọc comment theo postId */
                order("created_at", Order.ASCENDING)                /* Sắp xếp comment cũ -> mới */
            }.decodeList<CommentResponse>()
        return@withContext response.map { it.toDomain() }        /* Chuyển DTO sang Domain Model */

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

    /**
     * Xử lý like hoặc unlike comment.
     */
    suspend fun toggleLike(
        commentId: String,
        userId: String,
        isLiked: Boolean
    ) = withContext(Dispatchers.IO) {

        /* Nếu đã like thì unlike */
        if (isLiked) {
            supabase.postgrest[TABLE_COMMENT_LIKES].delete {
                /* Xóa like theo comment và user */
                filter {
                    eq("comment_id", commentId)
                    eq("user_id", userId)
                }
            }
        } else {
            /* Nếu chưa like thì thêm mới */
            supabase.postgrest[TABLE_COMMENT_LIKES].insert(
                mapOf(
                    "comment_id" to commentId,
                    "user_id" to userId
                )
            )
        }
    }
}