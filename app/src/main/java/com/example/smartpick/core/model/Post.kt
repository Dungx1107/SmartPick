package com.example.smartpick.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
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

// FIX 1: Bỏ @Serializable ở đây vì đây là Domain Model, chỉ cần @Parcelize
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

    // FIX 2: Thêm @RawValue để thư viện Parcelize hiểu cách truyền Object lồng nhau
    val sharedPostId: String? = null,
    val sharedPost: @RawValue Post? = null,
    val sharedPostUser: @RawValue User? = null

) : Parcelable