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

    // DTO nội bộ siêu an toàn, không dùng Join đệ quy để tránh lỗi Supabase
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
        fun toDomainTriple(
            currentUserId: String,
            sharedPostsMap: Map<String, Triple<Post, User, Product?>> = emptyMap()
        ): Triple<Post, User, Product?> {
            val reactions = this.postReactions ?: emptyList()
            val rCount = reactions.size
            val cReaction = reactions.find { it.userId == currentUserId }?.let {
                try { ReactionType.valueOf(it.reactionType) } catch (e: Exception) { null }
            }

            // Lấy bài gốc từ Map (nếu có)
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
            // Bước 1: Lấy tất cả bài viết bình thường
            val response = supabase.postgrest[TABLE_POSTS]
                .select(columns = Columns.raw("*, users(*), products(*), post_reactions(*)")) {
                    order("created_at", Order.DESCENDING)
                }

            val rawPosts = response.decodeList<SafePostResponse>()

            // Bước 2: Lọc ra các ID của bài gốc (bài được chia sẻ)
            val sharedPostIds = rawPosts.mapNotNull { it.sharedPostId }.distinct()
            val sharedPostsMap = mutableMapOf<String, Triple<Post, User, Product?>>()

            // Bước 3: Query riêng các bài gốc này và nhét vào Map
            if (sharedPostIds.isNotEmpty()) {
                val sharedResponse = supabase.postgrest[TABLE_POSTS]
                    .select(columns = Columns.raw("*, users(*), products(*), post_reactions(*)")) {
                        filter { isIn("id", sharedPostIds) }
                    }.decodeList<SafePostResponse>()

                sharedResponse.forEach { sharedRaw ->
                    sharedPostsMap[sharedRaw.id] = sharedRaw.toDomainTriple(currentUserId)
                }
            }

            // Bước 4: Ghép chung lại với nhau
            rawPosts.map { it.toDomainTriple(currentUserId, sharedPostsMap) }
        } catch (e: Exception) {
            Log.e("FEED_REPOSITORY", "Lỗi tải dữ liệu feed: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getReactedPosts(currentUserId: String): List<Triple<Post, User, Product?>> = withContext(Dispatchers.IO) {
        try {
            // Bước 1: Lấy các bài viết đã thích
            val response = supabase.postgrest[TABLE_REACTIONS]
                .select(columns = Columns.raw("post_id, user_id, posts(*, users(*), products(*), post_reactions(*))")) {
                    filter { eq("user_id", currentUserId) }
                    order("created_at", Order.DESCENDING)
                }

            val rawData = response.decodeList<SafeReactedPostResponse>()
            val rawPosts = rawData.mapNotNull { it.post }

            // Bước 2, 3, 4 y hệt như trên
            val sharedPostIds = rawPosts.mapNotNull { it.sharedPostId }.distinct()
            val sharedPostsMap = mutableMapOf<String, Triple<Post, User, Product?>>()

            if (sharedPostIds.isNotEmpty()) {
                val sharedResponse = supabase.postgrest[TABLE_POSTS]
                    .select(columns = Columns.raw("*, users(*), products(*), post_reactions(*)")) {
                        filter { isIn("id", sharedPostIds) }
                    }.decodeList<SafePostResponse>()

                sharedResponse.forEach { sharedRaw ->
                    sharedPostsMap[sharedRaw.id] = sharedRaw.toDomainTriple(currentUserId)
                }
            }

            rawPosts.map { it.toDomainTriple(currentUserId, sharedPostsMap) }
        } catch (e: Exception) {
            Log.e("FEED_REPOSITORY", "Lỗi tải bài viết đã thích: ${e.message}", e)
            emptyList()
        }
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
        } catch (e: Exception) {
            Log.e("FEED_REPOSITORY", "Lỗi DB khi toggleReaction", e)
            Result.failure(e)
        }
    }

    suspend fun sharePost(originalPostId: String, currentUserId: String, caption: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val sharedPostData = mapOf(
                "user_id" to currentUserId,
                "shared_post_id" to originalPostId,
                "content" to caption
            )
            supabase.postgrest[TABLE_POSTS].insert(sharedPostData)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FEED_REPOSITORY", "Lỗi chia sẻ: ${e.message}")
            Result.failure(e)
        }
    }

    // THÊM VÀO FeedRepository.kt
    suspend fun getUserPosts(profileUserId: String, currentUserId: String): List<Triple<Post, User, Product?>> = withContext(Dispatchers.IO) {
        try {
            // Bước 1: Chỉ lấy các bài viết thuộc về riêng User này (dành cho màn hình Profile)
            val response = supabase.postgrest[TABLE_POSTS]
                .select(columns = Columns.raw("*, users(*), products(*), post_reactions(*)")) {
                    filter {
                        eq("user_id", profileUserId)
                    }
                    order("created_at", Order.DESCENDING)
                }

            val rawPosts = response.decodeList<SafePostResponse>()

            // Bước 2: Lọc lấy danh sách ID của các bài viết gốc (bài được chia sẻ)
            val sharedPostIds = rawPosts.mapNotNull { it.sharedPostId }.distinct()
            val sharedPostsMap = mutableMapOf<String, Triple<Post, User, Product?>>()

            // Bước 3: Truy vấn riêng các bài gốc này và đưa vào bản đồ bộ nhớ tạm
            if (sharedPostIds.isNotEmpty()) {
                val sharedResponse = supabase.postgrest[TABLE_POSTS]
                    .select(columns = Columns.raw("*, users(*), products(*), post_reactions(*)")) {
                        filter { isIn("id", sharedPostIds) }
                    }.decodeList<SafePostResponse>()

                sharedResponse.forEach { sharedRaw ->
                    sharedPostsMap[sharedRaw.id] = sharedRaw.toDomainTriple(currentUserId)
                }
            }

            // Bước 4: Ghép nối đệ quy tạo cấu trúc lồng nhau hoàn hảo
            rawPosts.map { it.toDomainTriple(currentUserId, sharedPostsMap) }
        } catch (e: Exception) {
            Log.e("FEED_REPOSITORY", "Lỗi tải bài viết cá nhân: ${e.message}", e)
            emptyList()
        }
    }
}