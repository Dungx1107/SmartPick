package com.example.smartpick.features.feed.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.User
import com.example.smartpick.core.theme.DividerColor
import com.example.smartpick.core.theme.PageBg
import com.example.smartpick.core.theme.White
import com.example.smartpick.features.comment.ui.CommentsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun PostDetailScreen(
    post: Post,
    user: User,
    onBackClick: () -> Unit,
    onCommentClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.BaiViet),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        containerColor = PageBg
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {

            PostItem(
                post = post,
                user = user,
                onPostClick = {},
                onCommentClick = onCommentClick,
                showFullContent = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = DividerColor.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // CommentsScreen(...)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Post Detail")
@Composable
fun PostDetailScreenPreview() {

    // Mock data chỉ dùng cho Preview
    val mockUser = User(
        id = "user1",
        fullName = "Lê Hải An",
        avatarUrl = null
    )

//    val mockPost = Post(
//        id = "post1",
//        idUser = "user1",
//        content = "Góc setup làm việc tối giản với chiếc bàn nâng hạ và màn hình 4K. Trải nghiệm cực kỳ đã khi code Android 🚀",
//        createAt = "2 giờ trước",
//        images = listOf("https://via.placeholder.com/600")
//    )

//    MaterialTheme {
//        PostDetailScreen(
//            post = mockPost,
//            user = mockUser,
//            onBackClick = {},
//            onCommentClick = {}
//        )
//    }
}
