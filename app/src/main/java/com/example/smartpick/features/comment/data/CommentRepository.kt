package com.example.smartpick.features.comment.data

import Notification
import android.util.Log
import com.example.smartpick.core.model.Comment
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

@Singleton
class CommentRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val notificationRepository: NotificationRepository
) {
    // Lấy danh sách bình luận kèm thông tin User
    suspend fun getComments(postId: String): List<Comment> = withContext(Dispatchers.IO) {
        val response = supabase.postgrest["comments"]
            .select(columns = Columns.raw("*, users(*)")) {
                filter { eq("post_id", postId) }
                order("created_at", Order.ASCENDING)
            }.decodeList<CommentResponse>()

        // Chuyển đổi DTO sang Domain Model
        return@withContext response.map { dto ->
            Comment(
                id = dto.id,
                postId = dto.postId,
                userId = dto.userId,
                content = dto.content,
                createdAt = dto.createdAt,
                user = dto.user,
                likesCount = dto.likesCount,
                isLiked = dto.isLiked,
                parentId = dto.parentId
            )
        }
    }

    // Gửi bình luận mới
    suspend fun insertComment(
        postId: String,
        userId: String,
        receiverId: String, // ID của người sẽ nhận thông báo (Chủ bài viết HOẶC chủ bình luận)
        content: String,
        parentId: String? = null
    ) = withContext(Dispatchers.IO) {
        // Sử dụng mutableMapOf để có thể thêm phần tử sau khi khởi tạo
        val data = mutableMapOf(
            "post_id" to postId,
            "user_id" to userId,
            "content" to content.trim(),
            "created_at" to Clock.System.now().toString()
        )

        if (parentId != null) {
            data["parent_id"] = parentId
        }

        // 1. Lưu bình luận vào bảng comments
        Log.d("CommentDebug", "Repository INSERT: $data")
        supabase.postgrest["comments"].insert(data)

        // 2. Logic gửi thông báo:
        // Chỉ gửi nếu người bình luận (userId) KHÔNG PHẢI là chủ bài viết (postOwnerId)
        if (userId != receiverId) {
            val notificationTitle = if (parentId == null) "Bình luận mới" else "Phản hồi mới"

            val notification = Notification(
                receiverId = receiverId,
                senderId = userId,
                postId = postId,
                type = NotificationType.COMMUNITY.toString(),
                title = notificationTitle,
                content = content.trim(),
                targetId = postId // điều hướng về đúng bài viết
            )

            notificationRepository.sendNotification(notification)
        }
    }

    // Logic xử lý Like bình luận (sử dụng bảng comment_likes đã tạo)
    suspend fun toggleLike(commentId: String, userId: String, isLiked: Boolean) = withContext(Dispatchers.IO) {
        if (isLiked) {
            supabase.postgrest["comment_likes"].delete {
                filter {
                    eq("comment_id", commentId)
                    eq("user_id", userId)
                }
            }
        } else {
            supabase.postgrest["comment_likes"].insert(mapOf(
                "comment_id" to commentId,
                "user_id" to userId
            ))
        }
    }
}