package com.example.smartpick.features.post_creation.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.smartpick.core.data.dto.PostDto
import com.example.smartpick.core.data.dto.PostProductInsertDto
import com.example.smartpick.core.data.dto.ProductDto
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.data.mapper.toDto
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.network.ModerationException
import com.example.smartpick.core.network.ModerationService
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
import java.lang.reflect.Array.set
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostCreationRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val moderationService: ModerationService // FIX: Bơm ModerationService vào Repository
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
//            if (content.isNotBlank()) {
//                val isContentSafe = moderationService.checkTextContent(content)
//                if (!isContentSafe) {
//                    // Sử dụng ModerationException để đồng bộ với file ModerationService của bạn
//                    throw ModerationException("Nội dung bài viết chứa từ ngữ vi phạm tiêu chuẩn cộng đồng.")
//                }
//            }
//
//            productData?.let {
//                if (it.name.isNotBlank()) {
//                    val isProductNameSafe = moderationService.checkTextContent(it.name)
//                    if (!isProductNameSafe) {
//                        throw ModerationException("Tên sản phẩm chứa từ ngữ vi phạm tiêu chuẩn cộng đồng.")
//                    }
//                }
//            }


            val currentTimestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date())

            val uploadedUrls = coroutineScope {
                mediaUris.map { uri -> async { uploadMedia(uri, context) } }.awaitAll().filter { it.isNotEmpty() }
            }

            val imageUrls = uploadedUrls.filter { url ->
                !url.lowercase().contains(".mp4") && !url.lowercase().contains(".mov")
            }

//            if (imageUrls.isNotEmpty()) {
//                val imageSafetyResults = coroutineScope {
//                    imageUrls.map { url ->
//                        async { moderationService.checkImageContent(url) }
//                    }.awaitAll()
//                }
//
//                if (imageSafetyResults.contains(false)) {
//                    throw ModerationException("Hình ảnh chứa nội dung nhạy cảm hoặc bạo lực. Vui lòng chọn ảnh khác.")
//                }
//            }


            var finalProductId: String? = null

            productData?.let {
                val generatedProductId = UUID.randomUUID().toString()

                // Phân tách danh sách ảnh và video từ mảng dữ liệu đã upload thành công
                val productImages = uploadedUrls.filter { url -> !url.lowercase().contains(".mp4") && !url.lowercase().contains(".mov") }
                val productVideo = uploadedUrls.find { url -> url.lowercase().contains(".mp4") || url.lowercase().contains(".mov") }

                // Khởi tạo đối tượng DTO thô, không chứa cấu hình quan hệ phức tạp
                val rawProductData = PostProductInsertDto(
                    id = generatedProductId,
                    ownerId = userId,
                    name = it.name,
                    brand = it.brand,
                    category = it.category,
                    price = it.price,
                    stock = it.stock,
                    imageUrls = productImages,
                    videoUrl = productVideo
                )

                // Thực hiện insert đối tượng thô chuẩn xác vào bảng sản phẩm
                supabase.postgrest[TABLE_PRODUCTS].insert(rawProductData)

                // Gán trực tiếp ID vừa tạo ở local cho bài viết
                finalProductId = generatedProductId
            }

            val newPost = Post(
                id = UUID.randomUUID().toString(),
                userId = userId,
                productId = finalProductId,
                content = content,
                mediaUrls = uploadedUrls,
                createdAt = currentTimestamp
            )

            supabase.postgrest[TABLE_POSTS].insert(newPost.toDto())
        } catch (e: Exception) {
            Log.e("POST_CREATION", "Lỗi chi tiết: ${e.localizedMessage}")
            e.printStackTrace()
            // Ném lỗi lên trên để CreatePostViewModel catch và show UI
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