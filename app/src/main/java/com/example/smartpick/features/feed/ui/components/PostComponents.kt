package com.example.smartpick.features.feed.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.R
import com.example.smartpick.core.model.User
import com.example.smartpick.features.profile.ui.ProfileAvatar

// ======================= HEADER =======================

@Composable
fun PostHeader(
    user: User,
    createdAt: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileAvatar(
            avatarUrl = user.avatarUrl,
            size = 40.dp
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.fullName ?: stringResource(R.string.smartpick_user),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1C1E21)
            )
            Text(
                text = createdAt,
                fontSize = 12.sp,
                color = Color(0xFF65676B)
            )
        }

        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Outlined.MoreHoriz,
                contentDescription = null,
                tint = Color(0xFF65676B)
            )
        }
    }
}

// ======================= CONTENT =======================

@Composable
fun PostContent(
    content: String?,
    images: List<String>,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        content?.let {
            Text(
                text = it,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF050505)
            )
        }

        if (images.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = images.first(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color(0xFFF0F2F5)),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

// ======================= FOOTER =======================

@Composable
fun PostFooterActions(
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {

        PostActionButton(
            icon = Icons.Outlined.FavoriteBorder,
            text = stringResource(R.string.thich),
            onClick = { /* TODO */ },
            modifier = Modifier.weight(1f)
        )

        PostActionButton(
            icon = Icons.Outlined.ChatBubbleOutline,
            text = stringResource(R.string.BinhLuan),
            onClick = onCommentClick,
            modifier = Modifier.weight(1f)
        )

        PostActionButton(
            icon = Icons.Outlined.Share,
            text = stringResource(R.string.ChiaSe),
            onClick = { /* TODO */ },
            modifier = Modifier.weight(1f)
        )
    }
}

// ======================= BUTTON =======================

@Composable
private fun PostActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF65676B)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = text,
                fontSize = 13.sp,
                color = Color(0xFF65676B),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostHeaderPreview() {
    val mockUser = com.example.smartpick.core.model.User(
        id = "1",
        email = "test@gmail.com",
        fullName = "Nguyễn Văn A",
        username = "nguyenvana",
        avatarUrl = null,
        phoneNumber = "0123456789"
    )

    PostHeader(
        user = mockUser,
        createdAt = "2 giờ trước"
    )
}

@Preview(showBackground = true)
@Composable
fun PostContentPreview() {
    PostContent(
        content = "Đây là một bài post demo rất dài để test UI hiển thị nội dung trong feed. Nhìn cho giống Facebook 😄",
        images = listOf(
            "https://via.placeholder.com/600x400"
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PostFooterPreview() {
    PostFooterActions(
        onCommentClick = {}
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FullPostPreview() {
    val mockUser = User(
        id = "1",
        email = "test@gmail.com",
        fullName = "Nguyễn Văn A",
        username = "nguyenvana",
        avatarUrl = null,
        phoneNumber = "0123456789"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {

        PostHeader(
            user = mockUser,
            createdAt = "2 giờ trước"
        )

        PostContent(
            content = "Đây là bài viết test full layout. UI cần nhìn giống Facebook để user quen thuộc.",
            images = listOf("https://via.placeholder.com/600x400")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Divider(color = Color(0xFFE4E6EB))

        PostFooterActions(
            onCommentClick = {}
        )
    }
}