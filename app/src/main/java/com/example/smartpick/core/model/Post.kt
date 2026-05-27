package com.example.smartpick.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

enum class ReactionType {
    LIKE, LOVE, HAHA, WOW, SAD, ANGRY;

    fun getIcon(): String = when(this) {
        LIKE -> "👍"
        LOVE -> "❤️"
        HAHA -> "😂"
        WOW -> "😮"
        SAD -> "😢"
        ANGRY -> "😡"
    }
}

@Parcelize
data class Post(
    val id: String? = null,
    val userId: String,
    val productId: String? = null,
    val content: String? = null,
    val mediaUrls: List<String> = emptyList(),
    val createdAt: String? = null,

    val reactionCount: Int = 0,
    val currentUserReaction: ReactionType? = null,

    // TÚI ĐẾM CẢM XÚC (Chỉ chứa những cảm xúc có người thả)
    val reactionBreakdown: @RawValue Map<ReactionType, Int> = emptyMap(),

    val sharedPostId: String? = null,
    val sharedPost: @RawValue Post? = null,
    val sharedPostUser: @RawValue User? = null

) : Parcelable