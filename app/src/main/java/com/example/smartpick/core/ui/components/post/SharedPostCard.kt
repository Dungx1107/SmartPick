package com.example.smartpick.core.ui.components.post

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
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
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.core.utils.ReactionUtils.updateReactionState

@Composable
fun SharedPostCard(
    sharedPost: Post,
    sharedUser: User,
    onPostClick: () -> Unit,
    onReactionClick: (String, ReactionType) -> Unit = { _, _ -> }
) {
    var showReactionPopup by remember { mutableStateOf(false) }
    var localReaction by remember(sharedPost.currentUserReaction) { mutableStateOf(sharedPost.currentUserReaction) }
    var localReactionCount by remember(sharedPost.reactionCount) { mutableIntStateOf(sharedPost.reactionCount) }
    var localBreakdown by remember(sharedPost.reactionBreakdown) { mutableStateOf(sharedPost.reactionBreakdown) }

    val handleReaction = { reaction: ReactionType ->
        val (newReaction, newCount, newBreakdown) = updateReactionState(
            reaction, localReaction, localReactionCount, localBreakdown
        )
        localReaction = newReaction
        localReactionCount = newCount
        localBreakdown = newBreakdown
        onReactionClick(sharedPost.id.toString(), reaction)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onPostClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            // FIX: Đã xóa đuôi .kt và backtick, gọi trực tiếp hàm PostHeader
            PostHeader(user = sharedUser, createdAt = sharedPost.createdAt ?: "Vừa xong")

            PostMainContent(
                content = sharedPost.content,
                mediaUrls = sharedPost.mediaUrls,
                product = null,
                onMediaClick = { _ -> onPostClick() }
            )

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
                                onClick = { handleReaction(ReactionType.LIKE) }
                            )
                        }
                        PostActionButton(
                            icon = Icons.Outlined.ChatBubbleOutline,
                            text = stringResource(R.string.BinhLuan),
                            onClick = { onPostClick() },
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
                        }
                    )
                }
            }
        }
    }
}