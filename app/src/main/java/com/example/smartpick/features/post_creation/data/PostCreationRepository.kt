package com.example.smartpick.features.post_creation.data

import android.content.Context
import android.net.Uri
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

@Singleton
class PostCreationRepository @Inject constructor(
    private val supabase: SupabaseClient
) {

    private suspend fun uploadMedia(uri: Uri, context: Context): String = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
            val extension = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: ""
            val fileName = "post_media_${UUID.randomUUID()}.${extension}".removeSuffix(".")

            val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@withContext ""
            val bucket = supabase.storage.from("media")
            bucket.upload(path = fileName, data = bytes, upsert = false)
            return@withContext bucket.publicUrl(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext ""
        }
    }

    suspend fun createFullPost(
        userId: String,
        content: String,
        mediaUris: List<Uri>,
        productData: Product?,
        context: Context
    ) = withContext(Dispatchers.IO) {
        val uploadedUrls = coroutineScope {
            mediaUris.map { uri -> async { uploadMedia(uri, context) } }.awaitAll().filter { it.isNotEmpty() }
        }

        var finalProductId: String? = null

        productData?.let {
            val newProduct = it.copy(
                id = UUID.randomUUID().toString(),
                ownerId = userId,
                imageUrls = uploadedUrls.filter { url -> !url.contains(".mp4") },
                videoUrl = uploadedUrls.find { url -> url.contains(".mp4") }
            )
            val savedProduct = supabase.postgrest[TABLE_PRODUCTS].insert(newProduct) { select() }.decodeSingle<Product>()
            finalProductId = savedProduct.id
        }

        val newPost = Post(
            id = UUID.randomUUID().toString(),
            userId = userId,
            productId = finalProductId,
            content = content,
            mediaUrls = uploadedUrls
        )
        supabase.postgrest[TABLE_POSTS].insert(newPost)
    }

    suspend fun checkLastPost(userId: String): Post? = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest[TABLE_POSTS].select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
                limit(1)
            }.decodeSingleOrNull<Post>()
        } catch (e: Exception) {
            null
        }
    }
}
