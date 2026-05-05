package com.example.smartpick.features.feed.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

// ======================= CONTENT OPTIMIZED =======================

@Composable
fun PostContent(
    content: String?,
    mediaUrls: List<String>, // Đổi từ images sang mediaUrls
    maxLines: Int = Int.MAX_VALUE,
    onMediaClick: (Int) -> Unit = {}, // Click vào để xem chi tiết ảnh/video
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

        if (mediaUrls.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            MediaGrid(
                mediaUrls = mediaUrls,
                onMediaClick = onMediaClick
            )
        }
    }
}

@Composable
fun MediaGrid(
    mediaUrls: List<String>,
    onMediaClick: (Int) -> Unit
) {
    val count = mediaUrls.size

    // Box này sẽ chứa layout dựa trên số lượng ảnh
    Box(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
        when (count) {
            1 -> MediaItem(mediaUrls[0], Modifier.fillMaxWidth().aspectRatio(16f/9f)) { onMediaClick(0) }
            2 -> Row(Modifier.fillMaxWidth().height(250.dp)) {
                MediaItem(mediaUrls[0], Modifier.weight(1f).fillMaxHeight()) { onMediaClick(0) }
                Spacer(Modifier.width(2.dp))
                MediaItem(mediaUrls[1], Modifier.weight(1f).fillMaxHeight()) { onMediaClick(1) }
            }
            3 -> Row(Modifier.fillMaxWidth().height(300.dp)) {
                MediaItem(mediaUrls[0], Modifier.weight(1f).fillMaxHeight()) { onMediaClick(0) }
                Spacer(Modifier.width(2.dp))
                Column(Modifier.weight(1f).fillMaxHeight()) {
                    MediaItem(mediaUrls[1], Modifier.weight(1f).fillMaxWidth()) { onMediaClick(1) }
                    Spacer(Modifier.height(2.dp))
                    MediaItem(mediaUrls[2], Modifier.weight(1f).fillMaxWidth()) { onMediaClick(2) }
                }
            }
            else -> { // Trường hợp 4 ảnh hoặc nhiều hơn
                Column(Modifier.fillMaxWidth().height(400.dp)) {
                    MediaItem(mediaUrls[0], Modifier.weight(1f).fillMaxWidth()) { onMediaClick(0) }
                    Spacer(Modifier.height(2.dp))
                    Row(Modifier.weight(1f).fillMaxWidth()) {
                        MediaItem(mediaUrls[1], Modifier.weight(1f).fillMaxHeight()) { onMediaClick(1) }
                        Spacer(Modifier.width(2.dp))
                        MediaItem(mediaUrls[2], Modifier.weight(1f).fillMaxHeight()) { onMediaClick(2) }
                        Spacer(Modifier.width(2.dp))

                        // Ảnh cuối cùng kèm lớp phủ nếu > 4
                        Box(Modifier.weight(1f).fillMaxHeight()) {
                            MediaItem(mediaUrls[3], Modifier.fillMaxSize()) { onMediaClick(3) }
                            if (count > 4) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+${count - 3}",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MediaItem(
    url: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(modifier = modifier.clickable { onClick() }) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5)),
            contentScale = ContentScale.Crop
        )

        // Hiện icon Play nếu là Video (Logic giả định đuôi file)
        if (url.contains(".mp4") || url.contains("video")) {
            Icon(
                imageVector = Icons.Outlined.PlayCircle,
                contentDescription = "Play Video",
                tint = Color.White,
                modifier = Modifier.size(48.dp).align(Alignment.Center)
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

@Preview(showBackground = true, showSystemUi = true, name = "Full Post - 3 Media")
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
            content = "Đây là bài viết test UI feed SmartPick. Layout đã tối ưu media grid 🔥",
            mediaUrls = listOf(
                "https://via.placeholder.com/600",
                "https://via.placeholder.com/600",
                "https://via.placeholder.com/600"
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(color = Color(0xFFE4E6EB))

        PostFooterActions(
            onCommentClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Media Grid - 5 items")
@Composable
fun MediaGridPreview() {
    PostContent(
        content = "Test nhiều ảnh để check overlay + layout",
        mediaUrls = listOf(
            "https://via.placeholder.com/600",
            "https://via.placeholder.com/600",
            "https://via.placeholder.com/600",
            "https://via.placeholder.com/600",
            "https://via.placeholder.com/600"
        )
    )
}