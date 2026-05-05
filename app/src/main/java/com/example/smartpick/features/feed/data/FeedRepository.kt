package com.example.smartpick.features.feed.data

import android.content.Context
import android.net.Uri
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.utils.Constants.TABLE_POSTS
import com.example.smartpick.core.utils.Constants.TABLE_PRODUCTS
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import androidx.compose.foundation.layout.size

/**
 * Repository xử lý toàn bộ logic liên quan đến Feed:
 * - Upload media lên Supabase Storage
 * - Tạo Product (nếu có)
 * - Tạo Post và liên kết với Product
 */
@Singleton
class FeedRepository @Inject constructor(
    private val supabase: SupabaseClient
) {

    /**
     * Upload một file (ảnh/video) từ thiết bị lên Supabase Storage.
     *
     * @param uri Uri của file cần upload (từ gallery/camera)
     * @param context Context để đọc dữ liệu file
     * @return URL public của file sau khi upload, hoặc "" nếu thất bại
     *
     * Luồng hoạt động:
     * 1. Đọc file từ Uri → chuyển thành byte[]
     * 2. Tạo tên file ngẫu nhiên bằng UUID
     * 3. Upload lên bucket "media"
     * 4. Trả về public URL
     */
    private suspend fun uploadMedia(uri: Uri, context: Context): String =
        withContext(Dispatchers.IO) {
            try {
                val fileName = "media_${UUID.randomUUID()}"

                val bytes = context.contentResolver
                    .openInputStream(uri)
                    ?.use { it.readBytes() }
                    ?: return@withContext ""

                val bucket = supabase.storage.from("media")

                bucket.upload(fileName, bytes)

                return@withContext bucket.publicUrl(fileName)

            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext ""
            }
        }

    /**
     * Tạo một bài viết hoàn chỉnh:
     * - Upload media
     * - Tạo product (nếu có)
     * - Tạo post
     *
     * @param userId ID người đăng
     * @param content Nội dung bài viết
     * @param mediaUris Danh sách Uri ảnh/video
     * @param productData Thông tin sản phẩm (nullable)
     * @param context Context để xử lý file
     *
     * Luồng xử lý:
     *
     * Bước 1: Upload toàn bộ media → lấy danh sách URL
     * Bước 2: Nếu có product:
     *   - Gắn imageUrls (ảnh)
     *   - Gắn videoUrl (video đầu tiên nếu có)
     *   - Insert vào bảng products → lấy productId
     *
     * Bước 3: Tạo post:
     *   - Gắn mediaUrls
     *   - Gắn productId (nếu có)
     *   - Insert vào bảng posts
     */
    suspend fun createFullPost(
        userId: String,
        content: String,
        mediaUris: List<Uri>,
        productData: Product?,
        context: Context
    ) = withContext(Dispatchers.IO) {

        // 1. Upload media lên Storage theo hướng song song (Concurrent/Parallel)
        // Sử dụng coroutineScope để quản lý vòng đời của các async block
        val uploadedUrls = coroutineScope {
            mediaUris.map { uri ->
                async { uploadMedia(uri, context) }
            }.awaitAll().filter { it.isNotEmpty() }
        }

        var finalProductId: String? = null

        // 2. Nếu có sản phẩm → lưu vào bảng products
        productData?.let {

            val newProduct = it.copy(
                id = UUID.randomUUID().toString(),
                ownerId = userId,

                // Lấy danh sách ảnh (loại bỏ video)
                imageUrls = uploadedUrls.filter { url ->
                    !url.contains(".mp4")
                },

                // Lấy video đầu tiên (nếu có)
                videoUrl = uploadedUrls.find { url ->
                    url.contains(".mp4")
                }
            )

            /**
             * insert + select():
             * → trả về object vừa insert (để lấy ID)
             */
            val savedProduct = supabase.postgrest[TABLE_PRODUCTS]
                .insert(newProduct) {
                    select()
                }
                .decodeSingle<Product>()

            finalProductId = savedProduct.id
        }

        // 3. Tạo Post
        val newPost = Post(
            id = UUID.randomUUID().toString(),
            userId = userId,
            productId = finalProductId,
            content = content,
            mediaUrls = uploadedUrls
        )

        // Insert vào bảng posts
        supabase.postgrest[TABLE_POSTS].insert(newPost)
    }

    @Serializable
    data class PostWithUserResponse(
        val id: String? = null,
        @SerialName("user_id") val userId: String,
        @SerialName("product_id") val productId: String? = null,
        val content: String? = null,
        @SerialName("media_urls") val mediaUrls: List<String> = emptyList(),
        @SerialName("created_at") val createdAt: String? = null,
        val users: User? = null // Đây là nơi thông tin User lồng vào
    )

    /**
     * Lấy danh sách bài đăng từ Database, kèm theo thông tin User
     * Trả về: Danh sách các cặp (Bài đăng, Người đăng)
     */
    suspend fun getPostsWithUsers(): List<Pair<Post, User>> = withContext(Dispatchers.IO) {
        try {
            Log.d("FEED_DEBUG", "Bắt đầu tải bài viết từ Supabase...")

            // Lấy dữ liệu từ bảng posts và "nhúng" luôn dữ liệu từ bảng users vào
            // columns = "*, users(*)" nghĩa là: lấy hết cột của post và lấy hết cột của user liên quan
            val response = supabase.postgrest[TABLE_POSTS]
                .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("*, users(*)")) {
                    order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING) // Bài mới nhất lên đầu
                }

            // Giải mã kết quả trả về
            // Lưu ý: Bạn cần một class trung gian hoặc map thủ công
            val rawData = response.decodeList<PostWithUserResponse>()
            Log.d("FEED_DEBUG", "Đã lấy được ${rawData.size} bài viết")

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
                Pair(post, user)
            }
        } catch (e: Exception) {
            Log.e("FEED_DEBUG", "Lỗi tải dữ liệu: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }


    /**
     * Hàm kiểm tra bài đăng mới nhất của một User
     * Giúp xác nhận dữ liệu đã lên Database chuẩn chưa
     */
    suspend fun checkLastPost(userId: String): Post? {
        return try {
            supabase.postgrest["posts"].select {
                filter {
                    eq("user_id", userId) // Lọc đúng user đó
                }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING) // Lấy bài mới nhất
                limit(1) // Chỉ lấy 1 bản ghi
            }.decodeSingleOrNull<Post>()
        } catch (e: Exception) {
            null
        }
    }
}