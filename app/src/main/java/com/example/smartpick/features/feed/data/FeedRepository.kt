package com.example.smartpick.features.feed.data

import android.util.Log
import com.example.smartpick.core.data.dto.PostReactionDto
import com.example.smartpick.core.data.dto.PostReactionInsertDto
import com.example.smartpick.core.data.dto.ProductDto
import com.example.smartpick.core.data.dto.UserDto
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.model.*
import com.example.smartpick.core.utils.Constants.TABLE_POSTS
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    private val TABLE_REACTIONS = "post_reactions"

    @Serializable
    data class SoldItemDto(
        val id: String,
        val quantity: Int,
        @SerialName("price_at_purchase") val priceAtPurchase: Double,
        @SerialName("created_at") val createdAt: String? = null,
        val products: ProductDto? = null
    )

    @Serializable
    private data class PostUpdateDto(
        val content: String?,
        @SerialName("media_urls") val mediaUrls: List<String>
    )

    @Serializable
    private data class ProductUpdateDto(
        val name: String,
        val brand: String?,
        val category: String?,
        val price: Double
    )

    @Serializable
    private data class SafePostResponse(
        val id: String,
        @SerialName("user_id") val userId: String,
        @SerialName("product_id") val productId: String? = null,
        val content: String? = null,
        @SerialName("media_urls") val mediaUrls: List<String>? = emptyList(),
        @SerialName("created_at") val createdAt: String? = null,
        @SerialName("shared_post_id") val sharedPostId: String? = null,
        val users: UserDto? = null,
        val products: ProductDto? = null,
        @SerialName("post_reactions") val postReactions: List<PostReactionDto>? = emptyList()
    ) {
        fun toDomainTriple(currentUserId: String, sharedPostsMap: Map<String, Triple<Post, User, Product?>> = emptyMap()): Triple<Post, User, Product?> {
            val reactions = this.postReactions ?: emptyList()
            val rCount = reactions.size
            val cReaction = reactions.find { it.userId == currentUserId }?.let {
                try { ReactionType.valueOf(it.reactionType) } catch (e: Exception) { null }
            }

            val breakdown = reactions
                .mapNotNull { try { ReactionType.valueOf(it.reactionType) } catch (e: Exception) { null } }
                .groupingBy { it }
                .eachCount()

            val sharedTriple = this.sharedPostId?.let { sharedPostsMap[it] }

            val post = Post(
                id = this.id,
                userId = this.userId,
                productId = this.productId,
                content = this.content,
                mediaUrls = this.mediaUrls?.filter { it.isNotBlank() } ?: emptyList(),
                createdAt = this.createdAt,
                reactionCount = rCount,
                currentUserReaction = cReaction,
                reactionBreakdown = breakdown,
                sharedPostId = this.sharedPostId,
                sharedPost = sharedTriple?.first,
                sharedPostUser = sharedTriple?.second
            )
            val user = this.users?.toDomain() ?: User(id = this.userId, fullName = "Người dùng SmartPick")
            val product = this.products?.toDomain()
            return Triple(post, user, product)
        }
    }

    @Serializable
    private data class SafeReactedPostResponse(
        @SerialName("post_id") val postId: String,
        @SerialName("user_id") val userId: String,
        @SerialName("posts") val post: SafePostResponse? = null
    )

    suspend fun getPostsWithUsers(currentUserId: String): List<Triple<Post, User, Product?>> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest[TABLE_POSTS].select(columns = Columns.raw("*, users(*), products(*), post_reactions(*)")) {
                order("created_at", Order.DESCENDING)
            }
            val rawPosts = response.decodeList<SafePostResponse>().filter { it.sharedPostId == null }

            val sharedPostIds = rawPosts.mapNotNull { it.sharedPostId }.distinct()
            val sharedPostsMap = mutableMapOf<String, Triple<Post, User, Product?>>()

            if (sharedPostIds.isNotEmpty()) {
                val sharedResponse = supabase.postgrest[TABLE_POSTS].select(columns = Columns.raw("*, users(*), products(*), post_reactions(*)")) { filter { isIn("id", sharedPostIds) } }.decodeList<SafePostResponse>()
                sharedResponse.forEach { sharedRaw -> sharedPostsMap[sharedRaw.id] = sharedRaw.toDomainTriple(currentUserId) }
            }
            rawPosts.map { it.toDomainTriple(currentUserId, sharedPostsMap) }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getUserPosts(profileUserId: String, currentUserId: String): List<Triple<Post, User, Product?>> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest[TABLE_POSTS].select(columns = Columns.raw("*, users(*), products(*), post_reactions(*)")) {
                filter { eq("user_id", profileUserId) }
                order("created_at", Order.DESCENDING)
            }
            val rawPosts = response.decodeList<SafePostResponse>()

            val sharedPostIds = rawPosts.mapNotNull { it.sharedPostId }.distinct()
            val sharedPostsMap = mutableMapOf<String, Triple<Post, User, Product?>>()

            if (sharedPostIds.isNotEmpty()) {
                val sharedResponse = supabase.postgrest[TABLE_POSTS].select(columns = Columns.raw("*, users(*), products(*), post_reactions(*)")) { filter { isIn("id", sharedPostIds) } }.decodeList<SafePostResponse>()
                sharedResponse.forEach { sharedRaw -> sharedPostsMap[sharedRaw.id] = sharedRaw.toDomainTriple(currentUserId) }
            }
            rawPosts.map { it.toDomainTriple(currentUserId, sharedPostsMap) }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getReactedPosts(currentUserId: String): List<Triple<Post, User, Product?>> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest[TABLE_REACTIONS].select(columns = Columns.raw("post_id, user_id, posts(*, users(*), products(*), post_reactions(*))")) {
                filter { eq("user_id", currentUserId) }
                order("created_at", Order.DESCENDING)
            }
            val rawData = response.decodeList<SafeReactedPostResponse>()
            val rawPosts = rawData.mapNotNull { it.post }

            val sharedPostIds = rawPosts.mapNotNull { it.sharedPostId }.distinct()
            val sharedPostsMap = mutableMapOf<String, Triple<Post, User, Product?>>()

            if (sharedPostIds.isNotEmpty()) {
                val sharedResponse = supabase.postgrest[TABLE_POSTS].select(columns = Columns.raw("*, users(*), products(*), post_reactions(*)")) { filter { isIn("id", sharedPostIds) } }.decodeList<SafePostResponse>()
                sharedResponse.forEach { sharedRaw -> sharedPostsMap[sharedRaw.id] = sharedRaw.toDomainTriple(currentUserId) }
            }
            rawPosts.map { it.toDomainTriple(currentUserId, sharedPostsMap) }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun toggleReaction(postId: String, userId: String, reactionType: ReactionType): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existingList = supabase.postgrest[TABLE_REACTIONS].select { filter { eq("post_id", postId); eq("user_id", userId) } }.decodeList<PostReactionDto>()
            val existing = existingList.firstOrNull()

            if (existing != null) {
                if (existing.reactionType == reactionType.name) {
                    supabase.postgrest[TABLE_REACTIONS].delete { filter { eq("id", existing.id!!) } }
                } else {
                    supabase.postgrest[TABLE_REACTIONS].update(mapOf("reaction_type" to reactionType.name)) { filter { eq("id", existing.id!!) } }
                }
                if (existingList.size > 1) {
                    existingList.drop(1).forEach { dup -> supabase.postgrest[TABLE_REACTIONS].delete { filter { eq("id", dup.id!!) } } }
                }
            } else {
                supabase.postgrest[TABLE_REACTIONS].insert(PostReactionInsertDto(postId, userId, reactionType.name))
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun sharePost(originalPostId: String, currentUserId: String, caption: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val sharedPostData = mapOf("user_id" to currentUserId, "shared_post_id" to originalPostId, "content" to caption)
            supabase.postgrest[TABLE_POSTS].insert(sharedPostData)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deletePost(postId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest["post_reactions"].delete { filter { eq("post_id", postId) } }
            supabase.postgrest["comments"].delete { filter { eq("post_id", postId) } }
            supabase.postgrest[TABLE_POSTS].delete { filter { eq("id", postId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FEED_REPOSITORY", "Lỗi xóa bài viết: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getSoldItems(sellerId: String): List<SoldItemDto> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest["order_items"]
                .select(columns = Columns.raw("*, products!inner(*)")) {
                    filter {
                        eq("products.owner_id", sellerId)
                    }
                    order("created_at", Order.DESCENDING)
                }
            response.decodeList<SoldItemDto>()
        } catch (e: Exception) {
            Log.e("FEED_REPOSITORY", "Lỗi tải danh sách đã bán: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getPostById(postId: String): Result<Triple<Post, User, Product?>> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest[TABLE_POSTS]
                .select(columns = Columns.raw("*, users(*), products(*), post_reactions(*)")) {
                    filter { eq("id", postId) }
                }.decodeSingle<SafePostResponse>()

            Result.success(response.toDomainTriple(""))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadMedia(context: android.content.Context, uri: android.net.Uri): String? = withContext(Dispatchers.IO) {
        try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@withContext null

            // FIX VIDEO: Tự động phát hiện MIME Type để gắn đuôi file chuẩn xác
            val mimeType = context.contentResolver.getType(uri) ?: ""
            val extension = if (mimeType.startsWith("video/")) ".mp4" else ".jpg"

            val fileName = "${java.util.UUID.randomUUID()}$extension"
            val bucket = supabase.storage.from("post_media")
            bucket.upload(fileName, bytes)
            bucket.publicUrl(fileName)
        } catch (e: Exception) {
            Log.e("FEED_REPO", "Lỗi upload media: ${e.message}")
            null
        }
    }

    suspend fun updatePostFull(
        postId: String,
        newContent: String?,
        newMediaUrls: List<String>,
        product: Product?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val postUpdateData = PostUpdateDto(
                content = newContent,
                mediaUrls = newMediaUrls
            )
            supabase.postgrest[TABLE_POSTS].update(postUpdateData) {
                filter { eq("id", postId) }
            }

            if (product != null && product.id != null) {
                val productUpdateData = ProductUpdateDto(
                    name = product.name,
                    brand = product.brand,
                    category = product.category,
                    price = product.price
                )
                supabase.postgrest["products"].update(productUpdateData) {
                    filter { eq("id", product.id) }
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FEED_REPO", "Lỗi update post full: ${e.message}")
            Result.failure(e)
        }
    }
}