package com.example.smartpick.features.comment.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.ui.components.ProfileAvatar
import com.example.smartpick.features.comment.viewmodel.CommentUIState

@Composable
fun FacebookReplyConnector(
    isLastReply: Boolean
) {

    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val lineColor = Color(0xFFE2E8F0)

        val strokeWidth = 1.5.dp.toPx()

        // vị trí line dọc
        val verticalX = 28.dp.toPx()

        // tâm avatar reply
        val avatarCenterY = 20.dp.toPx()

        // line dọc
        drawLine(
            color = lineColor,
            start = Offset(verticalX, 0f),
            end = Offset(
                verticalX,
                if (isLastReply)
                    avatarCenterY
                else
                    size.height
            ),
            strokeWidth = strokeWidth
        )

        // line ngang
        drawLine(
            color = lineColor,
            start = Offset(verticalX, avatarCenterY),
            end = Offset(52.dp.toPx(), avatarCenterY),
            strokeWidth = strokeWidth
        )
    }
}
@Composable
fun ReplyConnector(
    isLastReply: Boolean
) {
    Box(
        modifier = Modifier
            .width(24.dp)
            .fillMaxHeight()
            .drawBehind {

                val lineColor = Color(0xFFE2E8F0)
                val stroke = 1.5.dp.toPx()

                val centerX = size.width / 2

                // avatar reply = 32dp
                val avatarRadius = 16.dp.toPx()

                // tâm avatar
                val centerY = avatarRadius

                // line dọc
                drawLine(
                    color = lineColor,
                    start = Offset(centerX, 0f),
                    end = Offset(
                        centerX,
                        if (isLastReply) centerY else size.height
                    ),
                    strokeWidth = stroke
                )

                // line ngang
                drawLine(
                    color = lineColor,
                    start = Offset(centerX, centerY),
                    end = Offset(size.width, centerY),
                    strokeWidth = stroke
                )
            }
    )
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 12.dp)
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint = if (isLiked) Color.Red else Color(0xFF94A3B8),
            modifier = Modifier
                .size(18.dp)
                .clickable { onLikeClick(id) }
        )
        if (count > 0) {
            Text(
                text = count.toString(),
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}