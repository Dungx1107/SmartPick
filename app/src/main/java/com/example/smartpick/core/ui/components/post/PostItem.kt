package com.example.smartpick.core.ui.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.model.User
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.core.utils.ReactionUtils.updateReactionState

@Composable
fun PostItem(
    post: Post,
    user: User,
    product: Product? = null,
    onPostClick: () -> Unit = {},
    onProductClick: (Product) -> Unit = {},
    onReactionClick: (String, ReactionType) -> Unit = { _, _ -> },
    onShareClick: (String) -> Unit = {},
    isDetailView: Boolean = false,
) {
    var showReactionPopup by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }

    var localReaction by remember(post.currentUserReaction) { mutableStateOf(post.currentUserReaction) }
    var localReactionCount by remember(post.reactionCount) { mutableIntStateOf(post.reactionCount) }
    var localBreakdown by remember(post.reactionBreakdown) { mutableStateOf(post.reactionBreakdown) }

    val handleReaction = { reaction: ReactionType ->
        val (newReaction, newCount, newBreakdown) = updateReactionState(
            reaction, localReaction, localReactionCount, localBreakdown
        )
        localReaction = newReaction
        localReactionCount = newCount
        localBreakdown = newBreakdown
        onReactionClick(post.id.toString(), reaction)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onPostClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            PostHeader(
                user = user,
                createdAt = post.createdAt ?: "Vừa xong",
                isShared = post.sharedPostId != null
            )

            if (post.sharedPost != null && post.sharedPostUser != null) {
                if (!post.content.isNullOrBlank()) {
                    Text(
                        text = post.content,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 15.sp
                    )
                }
                SharedPostCard(
                    sharedPost = post.sharedPost,
                    sharedUser = post.sharedPostUser,
                    onPostClick = onPostClick,
                    onReactionClick = onReactionClick
                )
            } else {
                PostMainContent(
                    content = post.content,
                    mediaUrls = post.mediaUrls,
                    product = product,
                    onMediaClick = { _ -> onPostClick() },
                    onProductClick = onProductClick
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (localReactionCount > 0) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val topIcons = localBreakdown.entries.sortedByDescending { it.value }.take(3)
                        .joinToString(" ") { it.key.getIcon() }
                    Text(text = topIcons.ifEmpty { "👍" }, fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "$localReactionCount", fontSize = 13.sp, color = TextMuted)
                }
            }

            Box {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            ReactionButton(
                                currentReaction = localReaction,
                                onLongPress = { showReactionPopup = true },
                                onClick = { handleReaction(ReactionType.LIKE) })
                        }
                        PostActionButton(
                            icon = Icons.Outlined.ChatBubbleOutline,
                            text = stringResource(R.string.BinhLuan),
                            onClick = onPostClick,
                            modifier = Modifier.weight(1f)
                        )
                        PostActionButton(
                            icon = Icons.Outlined.Share,
                            text = stringResource(R.string.ChiaSe),
                            onClick = { showShareDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (showReactionPopup) {
                    ReactionPopup(
                        onDismiss = { showReactionPopup = false },
                        onReactionSelected = { reaction ->
                            showReactionPopup = false
                            handleReaction(reaction)
                        })
                }
            }
        }
    }

    if (showShareDialog) {
        SharePostDialog(
            onDismiss = { showShareDialog = false },
            onShare = { caption ->
                showShareDialog = false
                onShareClick(caption)
            }
        )
    }
}