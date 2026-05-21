package com.example.smartpick.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
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
@Serializable
data class Post(
    val id: String? = null,
    val userId: String,
    val productId: String? = null,
    val content: String? = null,
    val mediaUrls: List<String> = emptyList(),
    val createdAt: String? = null,
    // Tính năng Reaction mới
    val reactionCount: Int = 0,
    val currentUserReaction: ReactionType? = null
) : Parcelable