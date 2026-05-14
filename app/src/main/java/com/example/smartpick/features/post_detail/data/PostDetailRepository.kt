package com.example.smartpick.features.post_detail.data

import com.example.smartpick.features.post_detail.data.dto.PostDetailResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostDetailRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    // Lấy chi tiết bài viết (Join User và Product)
    suspend fun getPostDetail(postId: String): PostDetailResponse?
    = withContext(Dispatchers.IO) {
        return@withContext supabase.postgrest["posts"]
            .select(columns = Columns.raw("*, users(*), products(*)")) {
                filter { eq("id", postId) }
            }.decodeSingleOrNull<PostDetailResponse>()
    }

}