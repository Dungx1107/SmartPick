package com.example.smartpick.features.feed.data

import android.util.Log
import com.example.smartpick.core.data.dto.PostReactionDto
import com.example.smartpick.core.data.dto.PostReactionInsertDto
import com.example.smartpick.core.data.dto.ReactedPostResponse
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.data.mapper.toPostDomain
import com.example.smartpick.core.model.*
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
    private val TABLE_REACTIONS = "post_reactions"

    suspend fun getPostsWithUsers(currentUserId: String): List<Triple<Post, User, Product?>> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest[TABLE_POSTS]
                .select(columns = Columns.raw("*, users(*), products(*), post_reactions(*)")) {
                    order("created_at", Order.DESCENDING)
                }

            val rawData = response.decodeList<FullPostResponse>()

            rawData.map { item ->
                val reactions = item.postReactions ?: emptyList()

                val post = item.toPostDomain().copy(
                    reactionCount = reactions.size,
                    currentUserReaction = reactions.find { it.userId == currentUserId }?.let {
                        try { ReactionType.valueOf(it.reactionType) } catch (e: Exception) { null }
                    }
                )

                val user = item.users?.toDomain() ?: User(id = item.userId, fullName = "Người dùng SmartPick")
                val product = item.products?.toDomain()

                Triple(post, user, product)
            }
        } catch (e: Exception) {
            Log.e("FEED_REPOSITORY", "Lỗi tải dữ liệu feed: ${e.message}")
            emptyList()
        }
    }

    suspend fun toggleReaction(postId: String, userId: String, reactionType: ReactionType): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existingList = supabase.postgrest[TABLE_REACTIONS]
                .select {
                    filter {
                        eq("post_id", postId)
                        eq("user_id", userId)
                    }
                }.decodeList<PostReactionDto>()

            val existing = existingList.firstOrNull()

            if (existing != null) {
                if (existing.reactionType == reactionType.name) {
                    supabase.postgrest[TABLE_REACTIONS].delete {
                        filter { eq("id", existing.id!!) }
                    }
                } else {
                    supabase.postgrest[TABLE_REACTIONS].update(
                        mapOf("reaction_type" to reactionType.name)
                    ) {
                        filter { eq("id", existing.id!!) }
                    }
                }

                if (existingList.size > 1) {
                    existingList.drop(1).forEach { duplicate ->
                        supabase.postgrest[TABLE_REACTIONS].delete {
                            filter { eq("id", duplicate.id!!) }
                        }
                    }
                }
            } else {
                val newReaction = PostReactionInsertDto(
                    postId = postId,
                    userId = userId,
                    reactionType = reactionType.name
                )
                supabase.postgrest[TABLE_REACTIONS].insert(newReaction)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FEED_REPOSITORY", "Lỗi DB khi toggleReaction: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ĐÃ THÊM HÀM NÀY VÀO ĐỂ FIX LỖI "Unresolved reference 'getReactedPosts'"
    suspend fun getReactedPosts(currentUserId: String): List<Triple<Post, User, Product?>> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest[TABLE_REACTIONS]
                .select(columns = Columns.raw("post_id, user_id, posts(*, users(*), products(*), post_reactions(*))")) {
                    filter {
                        eq("user_id", currentUserId)
                    }
                    order("created_at", Order.DESCENDING)
                }

            val rawData = response.decodeList<ReactedPostResponse>()

            rawData.mapNotNull { item ->
                val postItem = item.post ?: return@mapNotNull null
                val reactions = postItem.postReactions ?: emptyList()

                val post = postItem.toPostDomain().copy(
                    reactionCount = reactions.size,
                    currentUserReaction = reactions.find { it.userId == currentUserId }?.let {
                        try { ReactionType.valueOf(it.reactionType) } catch (e: Exception) { null }
                    }
                )

                val user = postItem.users?.toDomain() ?: User(id = postItem.userId, fullName = "Người dùng SmartPick")
                val product = postItem.products?.toDomain()

                Triple(post, user, product)
            }
        } catch (e: Exception) {
            Log.e("FEED_REPOSITORY", "Lỗi tải bài viết đã thích: ${e.message}")
            emptyList()
        }
    }
}