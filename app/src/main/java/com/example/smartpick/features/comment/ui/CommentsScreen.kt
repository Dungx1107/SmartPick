package com.example.smartpick.features.comment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartpick.features.comment.ui.components.CommentInputField
import com.example.smartpick.features.comment.ui.components.CommentItem
import com.example.smartpick.features.comment.ui.components.CommentUIState

@Composable
fun CommentsScreen(
    comments: List<CommentUIState>,
    paddingValues: PaddingValues,
    onSendComment: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onReplyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var commentText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(
                items = comments,
                key = { comment: CommentUIState -> comment.id } // Tối ưu hóa hiệu năng render
            ) { comment: CommentUIState ->
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

        CommentInputField(
            commentText = commentText,
            onCommentChange = { commentText = it },
            avatarAuthorUrl = null,
            onSend = {
                if (commentText.isNotBlank()) {
                    onSendComment(commentText)
                    commentText = "" // Xóa nội dung sau khi gửi
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CommentsScreenPreview() {
    // Khởi tạo dữ liệu mẫu chỉ dành cho Preview
    val mockComments = remember {
        listOf(
            CommentUIState(
                id = "1",
                authorName = "Nguyễn Minh Quang",
                authorAvatar = null,
                content = "Bàn phím này gõ êm không bác? Đang tính xúc một em về code đêm.",
                timeAgo = "1 giờ trước",
                likesCount = 12
            ),
            CommentUIState(
                id = "2",
                authorName = "Lê Hải An",
                authorAvatar = null,
                content = "Gõ cực êm nha bác, build nhôm đầm tay lắm. Nên mua switch red nhen! 🔥",
                timeAgo = "45 phút trước",
                likesCount = 5,
                isAuthor = true
            ),
            CommentUIState(
                id = "3",
                authorName = "Trần Thu Hà",
                authorAvatar = null,
                content = "Setup đẹp quá, xin link mua cái giá đỡ màn hình với ạ.",
                timeAgo = "30 phút trước",
                likesCount = 2
            )
        )
    }

    MaterialTheme {
        CommentsScreen(
            comments = mockComments,
            paddingValues = PaddingValues(0.dp),
            onSendComment = {},
            onLikeClick = {},
            onReplyClick = {}
        )
    }
}