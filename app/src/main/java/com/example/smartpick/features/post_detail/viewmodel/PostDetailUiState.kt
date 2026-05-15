package com.example.smartpick.features.post_detail.viewmodel

import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.features.comment.viewmodel.CommentUIState

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val user: User? = null,
    val product: Product? = null,
    val comments: List<CommentUIState> = emptyList(),
    val error: String? = null
)