package com.example.smartpick.features.feed.viewmodel

import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.User

sealed interface FeedUiState {
    object Loading : FeedUiState
    data class Success(val posts: List<Pair<Post, User>>) : FeedUiState
    data class Error(val message: String) : FeedUiState
}