package com.example.smartpick.features.post_creation.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.utils.Constants.TABLE_POSTS
import com.example.smartpick.core.utils.Constants.TABLE_PRODUCTS
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository xử lý logic tạo bài viết và upload media lên Supabase.
 *
 * Chức năng chính:
 * - Upload ảnh/video lên Supabase Storage.
 * - Tạo bài viết mới.
 * - Tạo sản phẩm đi kèm bài viết bán hàng.
 * - Kiểm tra bài viết mới nhất của người dùng.
 *
 * @property supabase Client dùng để thao tác với Supabase.
 */
@Singleton
class PostCreationRepository @Inject constructor(
    private val supabase: SupabaseClient
) {

    /**
     * Upload file media từ Uri lên Supabase Storage.
     *
     * Hàm hỗ trợ upload:
     * - Ảnh (jpg, png, webp, ...)
     * - Video (mp4, mov, ...)
     * - Các loại file media khác.
     *
     * Quy trình xử lý:
     * 1. Lấy MIME type thực tế từ Uri.
     * 2. Chuyển MIME type thành extension file.
     * 3. Sinh tên file ngẫu nhiên bằng UUID.
     * 4. Đọc dữ liệu file thành ByteArray.
     * 5. Upload file lên bucket "media".
     * 6. Trả về public URL sau khi upload thành công.
     *
     * Ví dụ:
     * - image/png  -> .png
     * - video/mp4 -> .mp4
     *
     * @param uri Uri của media cần upload.
     * @param context Context dùng để truy cập ContentResolver.
     *
     * @return Public URL của file nếu upload thành công,
     *         trả về chuỗi rỗng ("") nếu xảy ra lỗi.
     */
    private suspend fun uploadMedia(
        uri: Uri,
        context: Context
    ): String = withContext(Dispatchers.IO) {

        try {

            val contentResolver = context.contentResolver

            // Lấy MIME type thực tế của file
            // Ví dụ: image/png, video/mp4
            val mimeType = contentResolver.getType(uri)
                ?: "application/octet-stream"

            // Chuyển MIME type sang extension
            // Ví dụ: image/png -> png
            val extension = android.webkit.MimeTypeMap
                .getSingleton()
                .getExtensionFromMimeType(mimeType)
                ?: ""

            // Tạo tên file duy nhất bằng UUID
            // Nếu không lấy được extension thì chỉ dùng UUID
            val fileName =
                if (extension.isNotEmpty()) {
                    "post_media_${UUID.randomUUID()}.$extension"
                } else {
                    "post_media_${UUID.randomUUID()}"
                }

            // Đọc toàn bộ dữ liệu file thành ByteArray
            val bytes = contentResolver.openInputStream(uri)
                ?.use { it.readBytes() }
                ?: return@withContext ""

            // Truy cập bucket "media" trên Supabase Storage
            val bucket = supabase.storage.from("media")

            // Upload file lên Supabase
            bucket.upload(
                path = fileName,
                data = bytes,
                upsert = false
            )

            return@withContext bucket.publicUrl(fileName) // Trả về public URL của file

        } catch (e: Exception) {
            e.printStackTrace()  // In lỗi để debug
            return@withContext ""  // Trả về chuỗi rỗng nếu upload thất bại

        }
    }

    /**
     * Tạo bài viết hoàn chỉnh kèm media và sản phẩm (nếu có).
     *
     * Quy trình:
     * 1. Upload toàn bộ media song song bằng coroutine.
     * 2. Nếu có dữ liệu sản phẩm:
     *    - Tạo Product mới.
     *    - Gán ảnh/video tương ứng.
     *    - Lưu vào bảng products.
     * 3. Tạo Post mới và lưu vào bảng posts.
     *
     * @param userId ID người tạo bài viết.
     * @param content Nội dung bài viết.
     * @param mediaUris Danh sách Uri media cần upload.
     * @param productData Dữ liệu sản phẩm (nullable).
     * @param context Context dùng để đọc media.
     */
    suspend fun createFullPost(
        userId: String,
        content: String,
        mediaUris: List<Uri>,
        productData: Product?,
        context: Context
    ) = withContext(Dispatchers.IO) {
        try {
            /* Upload media song song */
            val uploadedUrls = coroutineScope {
                mediaUris.map { uri ->
                    async {
                        uploadMedia(uri, context)
                    }
                }.awaitAll().filter { it.isNotEmpty() }
            }

            var finalProductId: String? = null

            // Nếu là bài đăng bán hàng thì tạo Product
            productData?.let {

                val newProduct = it.copy(
                    id = UUID.randomUUID().toString(),
                    ownerId = userId,

                    // Chỉ lấy ảnh
                    imageUrls = uploadedUrls.filter { url ->
                        !url.contains(".mp4")
                    },

                    // Lấy video đầu tiên nếu có
                    videoUrl = uploadedUrls.find { url ->
                        url.contains(".mp4")
                    }
                )

                // Lưu Product vào database
                val savedProduct =
                    supabase.postgrest[TABLE_PRODUCTS]
                        .insert(newProduct) {
                            select()
                        }
                        .decodeSingle<Product>()

                finalProductId = savedProduct.id
            }

            // Tạo bài viết
            val newPost = Post(
                id = UUID.randomUUID().toString(),
                userId = userId,
                productId = finalProductId,
                content = content,
                mediaUrls = uploadedUrls
            )

            // Lưu bài viết vào database
            supabase.postgrest[TABLE_POSTS].insert(newPost)

        } catch (e: Exception) {
            Log.e("POST_CREATION", "Lỗi chi tiết: ${e.localizedMessage}")
            e.printStackTrace()
            throw e // Ném lỗi để UI nhận biết
        }

    }

    /**
     * Kiểm tra bài viết mới nhất của người dùng.
     *
     * Sử dụng để:
     * - Kiểm tra spam bài viết.
     * - Giới hạn tần suất đăng bài.
     * - Hiển thị bài viết gần nhất.
     *
     * @param userId ID người dùng cần kiểm tra.
     *
     * @return Post mới nhất nếu tồn tại,
     *         trả về null nếu không có hoặc xảy ra lỗi.
     */
    suspend fun checkLastPost(
        userId: String
    ): Post? = withContext(Dispatchers.IO) {

        try {

            supabase.postgrest[TABLE_POSTS]
                .select {

                    // Lọc theo user_id
                    filter {
                        eq("user_id", userId)
                    }

                    // Sắp xếp mới nhất
                    order(
                        "created_at",
                        Order.DESCENDING
                    )

                    // Chỉ lấy 1 bài
                    limit(1)

                }
                .decodeSingleOrNull<Post>()

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}