package com.example.smartpick.features.feed.data

import android.util.Log
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.data.mapper.toPostDomain
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
    suspend fun getPostsWithUsers(): List<Triple<Post, User, Product?>> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest[TABLE_POSTS]
                .select(columns = Columns.raw("*, users(*), products(*)")) {
                    order("created_at", Order.DESCENDING)
                }

            val rawData = response.decodeList<FullPostResponse>()

            rawData.map { item ->
                // 1. Map Post bằng toPostDomain()
                val post = item.toPostDomain()

                // 2. Map User: Nếu null thì tạo default User model thuần
                val user = item.users?.toDomain() ?: User(
                    id = item.userId,
                    fullName = "Người dùng SmartPick"
                )

                // 3. Map Product: Chuyển ProductDto sang Product model
                val product = item.products?.toDomain()

                Triple(post, user, product)
            }
        } catch (e: Exception) {
            Log.e("FEED_REPOSITORY", "Lỗi tải dữ liệu feed: ${e.message}")
            emptyList()
        }
    }
}