package com.example.smartpick.features.comment.ui

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.comment.ui.components.CommentInputField
import com.example.smartpick.features.comment.ui.components.CommentItem
import com.example.smartpick.features.comment.viewmodel.CommentUIState
import com.example.smartpick.features.comment.viewmodel.CommentViewModel

@Composable
fun CommentsScreen(
    postId: String,
    postOwnerId: String?,
    currentUserId: String,
    viewModel: CommentViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val comments by viewModel.comments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val replyingTo by viewModel.replyingTo.collectAsState()

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
        onReplyClick = { comment ->
            Log.d("CommentDebug", "UI: Bấm trả lời comment ID=${comment.id} của ${comment.authorName}")
            Toast.makeText(context, "Đang trả lời: ${comment.authorName}", Toast.LENGTH_SHORT).show()
            viewModel.setReplyingTo(comment)
        },
        onCancelReply = { viewModel.setReplyingTo(null) },
        replyingTo = replyingTo,
    )
}
@Composable
fun CommentsContent(
    comments: List<CommentUIState>,
    isLoading: Boolean,
    isSending: Boolean,
    onSendComment: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onReplyClick: (CommentUIState) -> Unit,
    replyingTo: CommentUIState?,
    onCancelReply: () -> Unit,
) {
    var commentText by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = comments, key = { it.id }) { parentComment ->
                        CommentItem(
                            state = parentComment,
                            onLikeClick = onLikeClick,
                            onReplyClick = onReplyClick
                        )

                        if (parentComment.replies.isNotEmpty()) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                parentComment.replies.forEachIndexed { index, reply ->
                                    CommentItem(
                                        state = reply,
                                        onLikeClick = onLikeClick,
                                        onReplyClick = onReplyClick,
                                        isReply = true,
                                        isLastReply = index == parentComment.replies.size - 1
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
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
    SmartPickTheme {
        val mockComments = listOf(
            CommentUIState(
                id = "1",
                authorId = "1",
                authorName = "Nguyễn Minh Quang",
                authorAvatar = "https://i.pravatar.cc/300?img=11",
                content = "Bàn phím này gõ êm không bác? Đang tính xúc một em về code đêm.",
                timeAgo = "1 giờ trước",
                likesCount = 12
            ),
            CommentUIState(
                id = "2",
                authorId = "2",
                authorName = "Lê Hải An",
                authorAvatar = "https://i.pravatar.cc/300?img=12",
                content = "Gõ cực êm nha bác, build nhôm đầm tay lắm 🔥",
                timeAgo = "45 phút trước",
                likesCount = 5,
                isAuthor = true
            )
        )

        CommentsContent(
            comments = mockComments,
            isLoading = false,
            isSending = false,
            onSendComment = {},
            onLikeClick = {},
            onReplyClick = {},
            replyingTo = null,
            onCancelReply = {}
        )
    }
}
