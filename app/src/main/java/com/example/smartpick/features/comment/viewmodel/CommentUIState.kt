package com.example.smartpick.features.comment.viewmodel

import androidx.compose.runtime.Immutable

/**
 * UI State đại diện cho dữ liệu hiển thị của một dòng bình luận.
 * Sử dụng @Immutable để tối ưu hóa việc recomposition trong Compose.
 */
@Immutable
data class CommentUIState(
    val id: String,
    val authorName: String,
    val authorAvatar: String?,
    val content: String,
    val timeAgo: String,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val isAuthor: Boolean = false,

    val parentId: String? = null,
    val replyToName: String? = null,  // Tên người được phản hồi
    val replies: List<CommentUIState> = emptyList() // Chứa danh sách reply cấp 2
)