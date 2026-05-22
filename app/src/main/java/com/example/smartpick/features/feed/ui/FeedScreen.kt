package com.example.smartpick.features.feed.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.smartpick.R
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.PostItem
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.feed.viewmodel.FeedUiState
import com.example.smartpick.features.feed.viewmodel.FeedViewModel

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    scrollToTopTrigger: Long = 0L, // FIX: Nhận tín hiệu cuộn từ TopBar
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
                Toast.makeText(context, "Đã chia sẻ lên Trang cá nhân!", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Composable
fun FeedContent(
    currentUser: User?,
    uiState: FeedUiState,
    paddingValues: PaddingValues,
    scrollToTopTrigger: Long, // FIX: Nhận tín hiệu cuộn
    onPostClick: (String) -> Unit,
    onCreatePostClick: () -> Unit,
    onReactionClick: (String, ReactionType) -> Unit = { _, _ -> },
    onShareClick: (String, String) -> Unit = { _, _ -> }
) {
    // FIX: Lưu trạng thái cuộn của danh sách
    val listState = rememberLazyListState()

    // Bắt sự kiện khi click vào TopBar -> Vuốt lên phần tử đầu tiên
    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger > 0L) {
            listState.animateScrollToItem(0)
        }
    }

    // FIX: Bọc màn hình bằng Scaffold để thêm Nút Nổi (FAB)
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            // Nút đăng bài nổi trôi nổi, luôn bám theo người dùng
            FloatingActionButton(
                onClick = onCreatePostClick,
                containerColor = SmartPickColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Đăng bài")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (uiState) {
                is FeedUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
                is FeedUiState.Success -> {
                    LazyColumn(
                        state = listState, // Gắn state để tự động cuộn
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp), // Chừa đáy 80dp để không bị nút FAB đè lên bài cuối
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Bỏ CreatePostPrompt ở trên cùng đi vì giờ đã có Nút FAB xịn xò
                        item {
                            Text(text = stringResource(R.string.DanhChoBan), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.primary)
                        }

                        items(uiState.posts, key = { it.first.id.toString() }) { (post, user, product) ->
                            PostItem(
                                post = post,
                                user = user,
                                product = product,
                                isDetailView = false,
                                onPostClick = { onPostClick(post.id.toString()) },
                                onReactionClick = { id, reaction -> onReactionClick(id, reaction) },
                                onShareClick = { caption -> onShareClick(post.id.toString(), caption) }
                            )
                        }
                    }
                }
                is FeedUiState.Error -> Text(text = uiState.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}