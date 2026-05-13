package com.example.smartpick.features.comment.data

import com.example.smartpick.features.comment.data.dto.CommentResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    // Lấy danh sách bình luận kèm thông tin User
    suspend fun getComments(postId: String): List<CommentResponse> = withContext(Dispatchers.IO) {
        return@withContext supabase.postgrest["comments"]
            .select(columns = Columns.raw("*, users(*)")) {
                filter { eq("post_id", postId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<CommentResponse>()
    }

    // Gửi bình luận mới
    suspend fun insertComment(postId: String, userId: String, content: String) = withContext(Dispatchers.IO) {
        val map = mapOf(
            "post_id" to postId,
            "user_id" to userId,
            "content" to content
        )
        supabase.postgrest["comments"].insert(map)
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