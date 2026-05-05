package com.example.smartpick.features.feed.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.User
import com.example.smartpick.core.theme.DividerColor
import com.example.smartpick.core.theme.White

@Composable
fun PostItem(
    post: Post,
    user: User,
    onPostClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    showFullContent: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
            .clickable(onClick = onPostClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .background(White)
                .padding(vertical = 4.dp)
        ) {
            PostHeader(user = user, createdAt = post.createAt)

            PostContent(
                content = post.content,
                images = post.images,
                maxLines = if (showFullContent) Int.MAX_VALUE else 3
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = DividerColor.copy(alpha = 0.3f)
            )

            PostFooterActions(onCommentClick = onCommentClick)
        }
    }
}

@Preview(showBackground = true, name = "Post Item Light Mode")
@Composable
fun PostItemPreview() {
    // 1. Tạo dữ liệu User mẫu
    val mockUser = User(
        id = "user_001",
        fullName = "Nguyễn Xuân Dũng", //
        avatarUrl = null // Sẽ hiển thị ảnh mặc định từ ProfileAvatar của bạn
    )

    // 2. Tạo dữ liệu Post mẫu
    val mockPost = Post(
        id = "post_001",
        idUser = "user_001",
        content = "Hôm nay bắt đầu tối ưu hóa UI cho dự án SmartPick. Cảm giác chia nhỏ component xong code sạch hơn hẳn! 🚀",
        createAt = "2 giờ trước",
        images = listOf("https://via.placeholder.com/600x400") // Link ảnh mẫu
    )

    // 3. Render Component
    MaterialTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            PostItem(
                post = mockPost,
                user = mockUser,
                onPostClick = {},
                onCommentClick = {},
                showFullContent = false
            )
        }
    }
}