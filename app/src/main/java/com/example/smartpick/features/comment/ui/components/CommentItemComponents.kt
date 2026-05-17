package com.example.smartpick.features.comment.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.ui.components.ProfileAvatar
import com.example.smartpick.features.comment.viewmodel.CommentUIState

@Composable
fun ReplyConnector(
    modifier: Modifier = Modifier,
    isReply: Boolean = false,
    isParent: Boolean = false,
    isLastReply: Boolean = false
) {
    Canvas(modifier = modifier) {
        val lineColor = Color(0xFFDADDE1)
        val strokeWidth = 1.5.dp.toPx()

        // Tọa độ X luôn là tâm avatar cha: 16dp padding + 20dp bán kính = 36dp
        val verticalX = 36.dp.toPx()

        if (isParent) {
            // NẾU LÀ CHA: Vẽ từ tâm avatar (4dp padding + 20dp bán kính = 24dp) xuống đáy
            val parentAvatarCenterY = 24.dp.toPx()
            drawLine(
                color = lineColor,
                start = Offset(verticalX, parentAvatarCenterY),
                end = Offset(verticalX, size.height),
                strokeWidth = strokeWidth
            )
        } else if (isReply) {
            // NẾU LÀ CON (REPLY):
            val replyAvatarCenterY = 20.dp.toPx() // 4dp padding + 16dp bán kính
            val horizontalEndX = 68.dp.toPx()   // 52dp padding + 16dp bán kính

            // Vẽ đường dọc: Luôn bắt đầu từ đỉnh (0f) để nối tiếp từ trên xuống
            drawLine(
                color = lineColor,
                start = Offset(verticalX, 0f),
                end = Offset(
                    verticalX,
                    if (isLastReply) replyAvatarCenterY else size.height
                ),
                strokeWidth = strokeWidth
            )

            // Vẽ đường ngang sang tâm avatar con
            drawLine(
                color = lineColor,
                start = Offset(verticalX, replyAvatarCenterY),
                end = Offset(horizontalEndX, replyAvatarCenterY),
                strokeWidth = strokeWidth
            )
        }
    }
}

@Composable
fun CommentAvatar(
    avatarUrl: String?,
    isReply: Boolean
) {
    ProfileAvatar(
        avatarUrl = avatarUrl,
        size = if (isReply) 32.dp else 40.dp
    )
}

@Composable
fun CommentBody(
    modifier: Modifier = Modifier,
    state: CommentUIState,
    onReplyClick: () -> Unit
) {
    Column(modifier = modifier) {
        CommentHeader(state)
        CommentContent(state)
        CommentFooter(
            timeAgo = state.timeAgo,
            onReplyClick = onReplyClick
        )
    }
}

@Composable
fun CommentHeader(state: CommentUIState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = state.authorName,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF1E3A8A),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis // Xử lý nếu tên quá dài
        )
        if (state.isAuthor) BadgeAuthor()
    }
}

@Composable
fun CommentContent(
    state: CommentUIState
) {
    Text(
        text = buildAnnotatedString {
            if (state.replyToName != null) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                ) {
                    append("${state.replyToName} ")
                }
            }
            append(state.content)
        },
        fontSize = 14.sp,
        color = Color(0xFF334155),
        lineHeight = 20.sp,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
fun CommentFooter(
    timeAgo: String,
    onReplyClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timeAgo,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(R.string.tra_loi),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            modifier = Modifier.clickable {
                onReplyClick()
            }
        )
    }
}

@Composable
fun BadgeAuthor() {
    Spacer(modifier = Modifier.width(6.dp))
    Box(
        modifier = Modifier
            .background(Color(0xFFD6E4FF), RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            stringResource(R.string.TacGia),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E3A8A)
        )
    }
}

@Composable
fun LikeSection(
    id: String,
    isLiked: Boolean,
    count: Int,
    onLikeClick: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 12.dp)
            .clickable { onLikeClick(id) } // Bấm vào cả cụm Row (trái tim + số) để tương tác dễ hơn
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint = if (isLiked) Color.Red else Color(0xFF94A3B8), // Đỏ khi đã thích, xám khi chưa thích
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(4.dp)) // Khoảng cách giữa trái tim và số lượng

        Text(
            text = count.toString(),
            fontSize = 13.sp,
            color = if (isLiked) Color.Red else Color(0xFF94A3B8), // Chuyển màu số theo trạng thái like
            fontWeight = if (isLiked) FontWeight.Bold else FontWeight.Medium
        )
    }
}
