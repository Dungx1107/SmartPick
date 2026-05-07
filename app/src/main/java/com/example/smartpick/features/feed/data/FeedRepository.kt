package com.example.smartpick.features.feed.data

import android.util.Log
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.utils.Constants.TABLE_POSTS
import com.example.smartpick.features.feed.data.dto.FullPostResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    /**
     * Lấy danh sách bài đăng kèm thông tin User và Product (nếu có)
     * Trả về: List<Triple<Bài đăng, Người đăng, Sản phẩm>>
     */
    suspend fun getPostsWithUsers(): List<Triple<Post, User, Product?>> = withContext(Dispatchers.IO) {
        try {
            // Join 3 bảng: posts, users, và products
            val response = supabase.postgrest[TABLE_POSTS]
                .select(columns = Columns.raw("*, users(*), products(*)")) {
                    order("created_at", Order.DESCENDING)
                }

            val rawData = response.decodeList<FullPostResponse>()

            rawData.map { item ->
                val post = Post(
                    id = item.id,
                    userId = item.userId,
                    productId = item.productId,
                    content = item.content,
                    mediaUrls = item.mediaUrls,
                    createdAt = item.createdAt
                )
                val user = item.users ?: User(id = item.userId, fullName = "Người dùng SmartPick")
                Triple(post, user, item.products)
            }
        } catch (e: Exception) {
            Log.e("FEED_REPOSITORY", "Lỗi tải dữ liệu feed: ${e.message}")
            emptyList()
        }
    }
}
