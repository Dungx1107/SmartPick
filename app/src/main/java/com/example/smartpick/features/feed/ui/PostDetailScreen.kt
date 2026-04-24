package com.example.smartpick.features.feed.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onBackClick: () -> Unit
) {
    // Mock data matching the new models
    val mockUser = User(
        id = "user1",
        fullName = "Lê Hải An",
        avatarUrl = "https://via.placeholder.com/150"
    )
    val mockPost = Post(
        id = postId,
        idUser = "user1",
        content = "Góc setup làm việc tối giản với chiếc bàn nâng hạ...",
        createAt = "2 giờ trước",
        images = listOf("https://via.placeholder.com/600")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bài viết", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tái sử dụng PostItem (đã refactor ở HomeFeedScreen)
            PostItem(
                post = mockPost,
                user = mockUser,
                onPostClick = {},
                onCommentClick = {}
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            // Tái sử dụng màn hình Comments
            CommentsScreen(
                paddingValues = PaddingValues(0.dp)
            )
        }
    }
}
