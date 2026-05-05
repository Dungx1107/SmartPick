package com.example.smartpick.core.model

data class Post(
    val id: String = "",
    val userId: String,
    val productId: String? = null,
    val content: String? = null,
    val createdAt: String = "",
    val mediaUrls: List<String> = emptyList() // Chứa link ảnh/video đính kèm của bài đăng
)
