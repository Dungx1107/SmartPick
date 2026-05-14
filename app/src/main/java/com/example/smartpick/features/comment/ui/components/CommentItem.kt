package com.example.smartpick.features.comment.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartpick.features.comment.viewmodel.CommentUIState
//
//@Composable
//fun CommentItem(
//    modifier: Modifier = Modifier,
//    state: CommentUIState,
//    onLikeClick: (String) -> Unit,
//    onReplyClick: (CommentUIState) -> Unit,
//    isReply: Boolean = false,
//    isLastReply: Boolean = false
//) {
//
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(IntrinsicSize.Min)
//            .padding(
//                start = 16.dp,
//                end = 16.dp
//            ),
//        verticalAlignment = Alignment.Top
//    ) {
//
//        if (isReply) {
//            ReplyConnector(isLastReply = isLastReply)
//        }
//        CommentAvatar(
//            avatarUrl = state.authorAvatar,
//            isReply = isReply
//        )
//        Spacer(modifier = Modifier.width(12.dp))
//        CommentBody(
//            state = state,
//            modifier = Modifier.weight(1f),
//            onReplyClick = { onReplyClick(state) }
//        )
//        LikeSection(
//            id = state.id,
//            isLiked = state.isLiked,
//            count = state.likesCount,
//            onLikeClick = onLikeClick
//        )
//    }
//}

@Composable
fun CommentItem(
    modifier: Modifier = Modifier,
    state: CommentUIState,
    onLikeClick: (String) -> Unit,
    onReplyClick: (CommentUIState) -> Unit,
    isReply: Boolean = false,
    isLastReply: Boolean = false
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {

        // DRAW CONNECTOR BEHIND
        if (isReply) {
            FacebookReplyConnector(
                isLastReply = isLastReply
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (isReply) 52.dp else 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 4.dp
                ),
            verticalAlignment = Alignment.Top
        ) {

            CommentAvatar(
                avatarUrl = state.authorAvatar,
                isReply = isReply
            )

            Spacer(modifier = Modifier.width(12.dp))

            CommentBody(
                modifier = Modifier.weight(1f),
                state = state,
                onReplyClick = {
                    onReplyClick(state)
                }
            )

            LikeSection(
                id = state.id,
                isLiked = state.isLiked,
                count = state.likesCount,
                onLikeClick = onLikeClick
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CommentItemPreview() {
    MaterialTheme {
        CommentItem(
            state = CommentUIState(
                id = "1",
                authorName = "Nguyễn Văn A",
                authorAvatar = null,
                content = "Sản phẩm này dùng rất ổn, pin khá trâu và thiết kế đẹp.",
                timeAgo = "2 giờ trước",
                likesCount = 12,
                isLiked = true,
                isAuthor = true
            ),
            onLikeClick = {},
            onReplyClick = {}
        )
    }
}