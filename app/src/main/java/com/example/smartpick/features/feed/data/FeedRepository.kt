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

/**
 * Repository chịu trách nhiệm lấy dữ liệu Feed từ Supabase.
 *
 * Nhiệm vụ chính:
 * - Query dữ liệu từ database
 * - Join dữ liệu giữa posts, users, products
 * - Mapping dữ liệu DTO -> Model
 * - Trả dữ liệu về cho ViewModel
 *
 * @property supabase Client dùng để kết nối Supabase
 */
@Singleton
class FeedRepository @Inject constructor(
    private val supabase: SupabaseClient
) {

    /**
     * Lấy danh sách bài đăng kèm thông tin User và Product.
     *
     * Flow xử lý:
     * 1. Query dữ liệu từ Supabase
     * 2. Join bảng posts, users, products
     * 3. Decode response thành DTO
     * 4. Mapping DTO -> Model
     * 5. Trả về List<Triple<Post, User, Product?>>
     *
     * Chạy trong Dispatchers.IO vì có thao tác network/database.
     */
    suspend fun getPostsWithUsers():
            List<Triple<Post, User, Product?>> = withContext(Dispatchers.IO) {
        try {

            /**
             * Query dữ liệu từ bảng posts.
             *
             * select:
             * - *              -> lấy toàn bộ cột của posts
             * - users(*)       -> join bảng users
             * - products(*)    -> join bảng products
             *
             * order:
             * - Sắp xếp bài viết mới nhất lên đầu
             */
            val response = supabase.postgrest[TABLE_POSTS]
                .select(columns = Columns.raw("*, users(*), products(*)")) {
                    order("created_at", Order.DESCENDING)
                }

            /* Decode JSON response thành List DTO. */
            val rawData = response.decodeList<FullPostResponse>()

            /**
             * Mapping dữ liệu DTO sang Model app sử dụng.
             */
            rawData.map { item ->

                /**
                 * Tạo object Post từ dữ liệu response.
                 */
                val post = Post(
                    id = item.id,
                    userId = item.userId,
                    productId = item.productId,
                    content = item.content,
                    mediaUrls = item.mediaUrls,
                    createdAt = item.createdAt
                )

                /**
                 * Nếu dữ liệu user bị null
                 * thì tạo user mặc định.
                 */
                val user = item.users
                    ?: User(
                        id = item.userId,
                        fullName = "Người dùng SmartPick"
                    )

                /* Trả về Triple<Post, User, Product?> */
                Triple(post, user, item.products)
            }

        } catch (e: Exception) {

            /**
             * Nếu có lỗi:
             * - In log lỗi
             * - Trả về danh sách rỗng
             */
            Log.e(
                "FEED_REPOSITORY",
                "Lỗi tải dữ liệu feed: ${e.message}"
            )

            emptyList()
        }
    }
}