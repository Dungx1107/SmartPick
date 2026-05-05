package com.example.smartpick.features.comment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.features.profile.ui.ProfileAvatar


/**
 * UI State đại diện cho dữ liệu hiển thị của một dòng bình luận.
 * Sử dụng @Immutable để tối ưu hóa việc recomposition trong Compose.
 */
@Immutable
data class CommentUIState(
    val id: String,
    val authorName: String,
    val authorAvatar: String?,
    val content: String,
    val timeAgo: String,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val isAuthor: Boolean = false
)

@Composable
fun CommentItem(
    state: CommentUIState,
    onLikeClick: (String) -> Unit,
    onReplyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        ProfileAvatar(
            avatarUrl = state.authorAvatar,
            size = 48.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // 1. Header: Name + Badge + Time
            CommentHeader(state)

            // 2. Content
            Text(
                text = state.content,
                fontSize = 14.sp,
                color = Color(0xFF334155),
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // 3. Actions: Reply Button
            Text(
                text = stringResource(R.string.tra_loi),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { onReplyClick(state.id) }
            )
        }

        // 4. Separate Like Section
        LikeSection(
            id = state.id,
            isLiked = state.isLiked,
            count = state.likesCount,
            onLikeClick = onLikeClick
        )
    }
}

@Composable
private fun CommentHeader(state: CommentUIState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = state.authorName,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF1E3A8A)
        )
        if (state.isAuthor) BadgeAuthor()
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = state.timeAgo,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )
    }
}

@Composable
private fun LikeSection(
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