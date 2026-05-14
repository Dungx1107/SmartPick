package com.example.smartpick.features.comment.data

import android.util.Log
import com.example.smartpick.core.model.Comment
import com.example.smartpick.features.comment.data.dto.CommentResponse
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
    private val supabase: SupabaseClient
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

        Log.d("CommentDebug", "Repository INSERT: $data")
        supabase.postgrest["comments"].insert(data)
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