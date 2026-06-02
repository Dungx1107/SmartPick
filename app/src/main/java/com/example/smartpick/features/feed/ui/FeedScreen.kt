package com.example.smartpick.features.feed.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.smartpick.R
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.post.PostItem
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.feed.viewmodel.FeedUiState
import com.example.smartpick.features.feed.viewmodel.FeedViewModel

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    scrollToTopTrigger: Long = 0L,
    onPostClick: (String) -> Unit = {},
    onCreatePostClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refreshFeedSilently()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    FeedContent(
        currentUser = currentUser,
        uiState = uiState,
        paddingValues = paddingValues,
        scrollToTopTrigger = scrollToTopTrigger,
        onPostClick = onPostClick,
        onCreatePostClick = onCreatePostClick,
        onReactionClick = { postId, type -> viewModel.toggleReaction(postId, type) },
        onShareClick = { postId, caption ->
            viewModel.sharePost(postId, caption) {
                Toast.makeText(context, context.getString(R.string.DaChiaSeLenTrangCaNhan), Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Composable
fun FeedContent(
    currentUser: User?,
    uiState: FeedUiState,
    paddingValues: PaddingValues,
    scrollToTopTrigger: Long,
    onPostClick: (String) -> Unit,
    onCreatePostClick: () -> Unit,
    onReactionClick: (String, ReactionType) -> Unit = { _, _ -> },
    onShareClick: (String, String) -> Unit = { _, _ -> }
) {
    val listState = rememberLazyListState()

    LaunchedEffect(scrollToTopTrigger) { // Bắt sự kiện khi click vào TopBar -> Vuốt lên phần tử đầu tiên
        if (scrollToTopTrigger > 0L) {
            listState.animateScrollToItem(0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when (uiState) {
            is FeedUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                ), color = MaterialTheme.colorScheme.primary
            )

            is FeedUiState.Success -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.DanhChoBan),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(
                        uiState.posts,
                        key = { it.first.id.toString() }) { (post, user, product) ->
                        PostItem(
                            post = post,
                            user = user,
                            product = product,
                            isDetailView = false,
                            onPostClick = { onPostClick(post.id.toString()) },
                            onReactionClick = { id, reaction -> onReactionClick(id, reaction) },
                            onShareClick = { caption ->
                                onShareClick(
                                    post.id.toString(),
                                    caption
                                )
                            }
                        )
                    }
                }

                FloatingActionButton(
                    onClick = onCreatePostClick,
                    containerColor = SmartPickColor,
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.DangBai)
                    )
                }
            }

            is FeedUiState.Error -> Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeedContentPreview() {

    val mockUser = User(
        id = "user_1",
        fullName = "Nguyễn Văn A",
        username = "vana",
        avatarUrl = ""
    )

    val mockProduct = Product(
        id = "product_1",
        ownerId = "user_1",
        name = "iPhone 15 Pro Max",
        brand = "Apple",
        category = "Điện thoại",
        price = 32990000.0,
        imageUrls = listOf(),
        stock = 10,
        soldCount = 5
    )

    val mockPost = Post(
        id = "post_1",
        userId = "user_1",
        productId = "product_1",
        content = "Đây là bài viết preview cho màn hình Feed 🚀",
        mediaUrls = emptyList(),
        createdAt = "2 phút trước",

        reactionCount = 120,
        currentUserReaction = ReactionType.LOVE,

        reactionBreakdown = mapOf(
            ReactionType.LIKE to 60,
            ReactionType.LOVE to 40,
            ReactionType.HAHA to 20
        )
    )

    val mockUiState = FeedUiState.Success(
        posts = listOf(
            Triple(mockPost, mockUser, mockProduct),
            Triple(
                mockPost.copy(
                    id = "post_2",
                    content = "Một bài viết khác không có sản phẩm.",
                    currentUserReaction = ReactionType.HAHA,
                    reactionCount = 35
                ),
                mockUser.copy(
                    id = "user_2",
                    fullName = "Trần Thị B"
                ),
                null
            )
        )
    )

    MaterialTheme {
        FeedContent(
            currentUser = mockUser,
            uiState = mockUiState,
            paddingValues = PaddingValues(),
            scrollToTopTrigger = 0L,
            onPostClick = {},
            onCreatePostClick = {},
            onReactionClick = { _, _ -> },
            onShareClick = { _, _ -> }
        )
    }
}