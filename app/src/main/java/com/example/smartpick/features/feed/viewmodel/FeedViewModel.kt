package com.example.smartpick.features.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.features.feed.data.FeedRepository
import com.example.smartpick.features.feed.viewmodel.FeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FeedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

//    private val _comments = MutableStateFlow<List<CommentUIState>>(emptyList())
//    val comments = _comments.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            try {
                val posts = repository.getPosts()
                _uiState.value = FeedUiState.Success(posts)
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

//    fun loadComments(postId: String) {
//        viewModelScope.launch {
//            val rawComments = repository.getComments(postId) // Trả về List<Comment>
//            _comments.value = rawComments.map { comment ->
//                comment.toUIState(
//                    authorName = "User ${comment.userId}", // Logic join user
//                    isLiked = checkIfLiked(comment.id)
//                )
//            }
//        }
//    }
}