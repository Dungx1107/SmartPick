package com.example.smartpick.core.model

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val content: String,
    val createAt: String
)
