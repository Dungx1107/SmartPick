package com.example.smartpick.core.model

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val content: String,
    val createdAt: String,
    val user: User, // Đưa thông tin User vào đây
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val parentId: String? = null // Thêm trường này để phục vụ tính năng trả lời
)