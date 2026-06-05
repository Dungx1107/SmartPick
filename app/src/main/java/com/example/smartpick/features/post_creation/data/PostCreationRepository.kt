package com.example.smartpick.features.post_creation.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.smartpick.core.data.dto.PostDto
import com.example.smartpick.core.data.dto.ProductDto
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.data.mapper.toDto
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.utils.Constants.TABLE_POSTS
import com.example.smartpick.core.utils.Constants.TABLE_PRODUCTS
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostCreationRepository @Inject constructor(
    private val supabase: SupabaseClient
) {

    private fun uriToFlow(context: Context, uri: Uri) = flow {
        val contentResolver = context.contentResolver
        val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return@flow
        val buffer = ByteArray(64 * 1024)
        try {
            inputStream.use { stream ->
                while (true) {
                    val bytesRead = stream.read(buffer)
                    if (bytesRead == -1) break
                    val chunk = buffer.copyOfRange(0, bytesRead)
                    emit(chunk)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun uploadMedia(
        uri: Uri,
        context: Context
    ): String = withContext(Dispatchers.IO) {
        var tempFile: File? = null
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

            var extension = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            if (extension.isNullOrEmpty()) {
                extension = if (mimeType.startsWith("video/")) "mp4" else "jpg"
            }

            val fileName = "post_media_${UUID.randomUUID()}.$extension"
            tempFile = File(context.cacheDir, fileName)

            contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val bucket = supabase.storage.from("media")

            bucket.upload(
                path = fileName,
                file = tempFile,
                upsert = false
            )

            return@withContext bucket.publicUrl(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext ""
        } finally {
            tempFile?.delete()
        }
    }

    suspend fun createFullPost(
        userId: String,
        content: String,
        mediaUris: List<Uri>,
        productData: Product?,
        context: Context
    ) = withContext(Dispatchers.IO) {
        try {
            // FIX: Lấy chính xác thời điểm hàm được gọi (Lúc ấn nút Đăng)
            // Format sang chuẩn ISO 8601 (VD: 2026-06-05T12:34:56.789Z) và ép về múi giờ UTC để đồng bộ Supabase
            val currentTimestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date())

            val uploadedUrls = coroutineScope {
                mediaUris.map { uri -> async { uploadMedia(uri, context) } }.awaitAll().filter { it.isNotEmpty() }
            }

            var finalProductId: String? = null

            productData?.let {
                val newProduct = it.copy(
                    id = UUID.randomUUID().toString(),
                    ownerId = userId,
                    imageUrls = uploadedUrls.filter { url -> !url.lowercase().contains(".mp4") && !url.lowercase().contains(".mov") },
                    videoUrl = uploadedUrls.find { url -> url.lowercase().contains(".mp4") || url.lowercase().contains(".mov") },
                    // Nếu Model Product của bạn có createdAt, có thể chèn luôn vào đây
                    // createdAt = currentTimestamp
                )

                val savedProduct = supabase.postgrest[TABLE_PRODUCTS]
                    .insert(newProduct.toDto()) { select() }
                    .decodeSingle<ProductDto>()
                    .toDomain()

                finalProductId = savedProduct.id
            }

            // Gắn cứng thời gian vừa tạo vào bài viết
            val newPost = Post(
                id = UUID.randomUUID().toString(),
                userId = userId,
                productId = finalProductId,
                content = content,
                mediaUrls = uploadedUrls,
                createdAt = currentTimestamp // <-- CHÈN THỜI GIAN ẤN NÚT VÀO ĐÂY
            )

            supabase.postgrest[TABLE_POSTS].insert(newPost.toDto())
        } catch (e: Exception) {
            Log.e("POST_CREATION", "Lỗi chi tiết: ${e.localizedMessage}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun checkLastPost(userId: String): Post? = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest[TABLE_POSTS].select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
                limit(1)
            }.decodeSingleOrNull<PostDto>()?.toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}