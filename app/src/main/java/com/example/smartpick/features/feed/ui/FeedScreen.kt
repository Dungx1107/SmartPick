package com.example.smartpick.features.feed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.User
import com.example.smartpick.core.theme.AccentBlue
import com.example.smartpick.core.theme.DividerColor
import com.example.smartpick.core.theme.PageBg
import com.example.smartpick.core.theme.SurfaceCard
import com.example.smartpick.core.theme.TextMuted
import com.example.smartpick.core.theme.TextSecondary
import com.example.smartpick.core.theme.White
import com.example.smartpick.features.feed.ui.components.PostItem
import com.example.smartpick.features.feed.viewmodel.FeedUiState
import com.example.smartpick.features.feed.viewmodel.FeedViewModel
import com.example.smartpick.features.profile.ui.ProfileAvatar

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    onPostClick: (String) -> Unit = {},
    onCommentClick: (String) -> Unit = {},
    onCreatePostClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    // Stateful chỉ làm nhiệm vụ kết nối ViewModel với UI
    FeedContent(
        currentUserAvatar = null,
        uiState = uiState,
        paddingValues = paddingValues,
        onPostClick = onPostClick,
        onCommentClick = onCommentClick,
        onCreatePostClick = onCreatePostClick,
    )
}

@Composable
fun FeedContent(
    currentUserAvatar: String?,
    uiState: FeedUiState,
    paddingValues: PaddingValues,
    onPostClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onCreatePostClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .padding(paddingValues)
    ) {
        when (uiState) {
            is FeedUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is FeedUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        vertical = 8.dp,
                        horizontal = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // Entry point: Khung đăng bài
                    item {
                        CreatePostPrompt(
                            avatarUrl = currentUserAvatar,
                            onClick = onCreatePostClick
                        )
                    }

                    // Danh sách bài viết
                    items(uiState.posts, key = { it.first.id }) { (post, user) ->
                        PostItem(
                            post = post,
                            user = user,
                            onPostClick = { onPostClick(post.id) },
                            onCommentClick = { onCommentClick(post.id) }
                        )
                    }
                }
            }

            is FeedUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

// Khối UI nhập nháy để đăng bài
@Composable
private fun CreatePostPrompt(
    avatarUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileAvatar(avatarUrl = avatarUrl, size = 42.dp)

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceCard)
                        .clickable { onClick() }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.BanMuonChiaSeSanPhamGi),
                        color = TextMuted,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = DividerColor.copy(alpha = 0.4f))

            Spacer(modifier = Modifier.height(8.dp))

            // Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(onClick = onClick) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = AccentBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.AnhVideo), color = TextSecondary)
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Feed Screen Success")
@Composable
fun FeedScreenPreview() {
    // 1. Khởi tạo dữ liệu mẫu
    val mockUser = User(
        id = "u1",
        fullName = "Nguyễn Xuân Dũng",
        avatarUrl = null
    )

//    val mockPosts = listOf(
//        Pair(
//            Post(
//                id = "p1",
//                idUser = "u1",
//                content = "Trải nghiệm tính năng SmartPick AI tìm kiếm sản phẩm qua hình ảnh cực mượt! 🚀",
//                createAt = "5 phút trước",
//                images = listOf("https://via.placeholder.com/600")
//            ),
//            mockUser
//        ),
//        Pair(
//            Post(
//                id = "p2",
//                idUser = "u2",
//                content = "Hôm nay tôi đang tìm kiếm một chiếc bàn phím cơ mới cho dự án Android.",
//                createAt = "1 giờ trước",
//                images = emptyList()
//            ),
//            User(id = "u2", fullName = "Lê Hải An", avatarUrl = null)
//        )
//    )

    // 2. Render UI ở trạng thái Success
//    MaterialTheme {
//        FeedContent(
//            uiState = FeedUiState.Success(mockPosts),
//            paddingValues = PaddingValues(0.dp),
//            onPostClick = {},
//            onCommentClick = {},
//            onCreatePostClick = {},
//            currentUserAvatar = null
//
//        )
//    }
}

@Preview(showBackground = true, name = "Feed Screen Loading")
@Composable
fun FeedScreenLoadingPreview() {
    MaterialTheme {
        FeedContent(
            uiState = FeedUiState.Loading,
            paddingValues = PaddingValues(0.dp),
            onPostClick = {},
            onCommentClick = {},
            onCreatePostClick = {},
            currentUserAvatar = null
        )
    }
}