package com.example.smartpick.features.profile.data

import com.example.smartpick.features.post_detail.data.dto.PostDetailResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject

class UserPostRepository  @Inject constructor(
    private val supabase: SupabaseClient    // Inject Supabase client để gọi API (database + storage)
) {

    suspend fun getUserPosts(userId: String): List<PostDetailResponse> {
        return supabase.postgrest["posts"]
            .select(columns = Columns.raw("*, users(*), products(*)")) {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<PostDetailResponse>()
    }
}