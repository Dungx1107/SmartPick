package com.example.smartpick.features.comment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.features.comment.ui.components.CommentInputField
import com.example.smartpick.features.comment.ui.components.CommentItem
import com.example.smartpick.features.comment.ui.components.CommentUIState
import com.example.smartpick.features.comment.viewmodel.CommentViewModel

@Composable
fun CommentsScreen(
    postId: String,
    postOwnerId: String?,
    currentUserId: String, // Lấy từ AuthViewModel
    viewModel: CommentViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val comments by viewModel.comments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSending by viewModel.isSending.collectAsState()

    // Tự động load dữ liệu khi vào màn hình
    LaunchedEffect(postId) {
        viewModel.loadComments(postId, postOwnerId)
    }

    CommentsContent(
        comments = comments,
        isLoading = isLoading,
        isSending = isSending,
        onSendComment = { text ->
            viewModel.sendComment(postId, currentUserId, text, postOwnerId)
        },
        onLikeClick = { /* Gọi viewModel toggleLike */ },
        onReplyClick = { /* Xử lý reply */ }
    )
}

@Composable
fun CommentsContent(
    comments: List<CommentUIState>,
    isLoading: Boolean,
    isSending: Boolean,
    onSendComment: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onReplyClick: (String) -> Unit
) {
    var commentText by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {

            val bottomPadding = WindowInsets.navigationBars.union(WindowInsets.ime)
            
            Box(modifier = Modifier.windowInsetsPadding(bottomPadding)) {
                CommentInputField(
                    commentText = commentText,
                    onCommentChange = { commentText = it },
                    avatarAuthorUrl = null,
                    onSend = {
                        onSendComment(commentText)
                        commentText = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        // FIX 3: Vì contentWindowInsets = 0 nên innerPadding sẽ là 0. 
        // Chúng ta áp dụng statusBarsPadding để nội dung không bị dính lên top.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding() 
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                // FIX 4: LazyColumn sẽ tự động co lại khi Window resize do adjustResize
                LazyColumn(
                    state = listState, 
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp) // Thêm chút khoảng trống cuối danh sách
                ) {
                    items(items = comments, key = { it.id }) { comment ->
                        CommentItem(
                            state = comment,
                            onLikeClick = onLikeClick,
                            onReplyClick = onReplyClick
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CommentsContentPreview() {
    val mockComments = listOf(
        CommentUIState(
            id = "1",
            authorName = "Nguyễn Minh Quang",
            authorAvatar = "https://i.pravatar.cc/300?img=11",
            content = "Bàn phím này gõ êm không bác? Đang tính xúc một em về code đêm.",
            timeAgo = "1 giờ trước",
            likesCount = 12
        ),
        CommentUIState(
            id = "2",
            authorName = "Lê Hải An",
            authorAvatar = "https://i.pravatar.cc/300?img=12",
            content = "Gõ cực êm nha bác, build nhôm đầm tay lắm 🔥",
            timeAgo = "45 phút trước",
            likesCount = 5,
            isAuthor = true
        )
    )

    MaterialTheme {
        CommentsContent(
            comments = mockComments,
            isLoading = false,
            isSending = false,
            onSendComment = {},
            onLikeClick = {},
            onReplyClick = {}
        )
    }
}
