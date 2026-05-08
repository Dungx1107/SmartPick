package com.example.smartpick.features.feed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.ui.components.PostItem
import com.example.smartpick.core.ui.theme.PageBg
import com.example.smartpick.core.ui.theme.White
import com.example.smartpick.features.feed.ui.components.CreatePostPrompt
import com.example.smartpick.features.feed.viewmodel.FeedUiState
import com.example.smartpick.features.feed.viewmodel.FeedViewModel

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    onPostClick: (String) -> Unit = {},
    onCommentClick: (String) -> Unit = {},
    onCreatePostClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .padding(paddingValues)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = White,
            shadowElevation = 2.dp
        ) {
            CreatePostPrompt(
                avatarUrl = currentUserAvatar,
                onClick = onCreatePostClick,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.DanhChoBan),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Box(modifier = Modifier.weight(1f)) {
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
                        items(
                            uiState.posts,
                            key = { it.first.id.toString() }) { (post, user, product) ->
                            PostItem(
                                post = post,
                                user = user,
                                product = product,
                                onPostClick = { onPostClick(post.id.toString()) },
                                onCommentClick = { onCommentClick(post.id.toString()) },
                                onMediaClick = { /* Để sau */ },
                                onProductClick = { /* Để sau */ }
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
}
