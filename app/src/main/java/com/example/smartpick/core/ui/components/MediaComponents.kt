package com.example.smartpick.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest

private fun isVideoUrl(url: String): Boolean {
    val lower = url.lowercase()
    return lower.endsWith(".mp4") || lower.contains(".mp4?") ||
            lower.endsWith(".mov") || lower.contains(".mov?") ||
            lower.endsWith(".webm") || lower.contains(".webm?")
}

@Composable
fun MediaGrid(
    mediaUrls: List<String>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalMedia = mediaUrls.size

    if (totalMedia == 0) return

    Column(modifier = modifier.fillMaxWidth()) {
        when (totalMedia) {
            1 -> {
                MediaItem(url = mediaUrls[0], modifier = Modifier.fillMaxWidth().height(300.dp))
            }
            2 -> {
                Row(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    MediaItem(url = mediaUrls[0], modifier = Modifier.weight(1f).fillMaxHeight())
                    Spacer(modifier = Modifier.width(4.dp))
                    MediaItem(url = mediaUrls[1], modifier = Modifier.weight(1f).fillMaxHeight())
                }
            }
            3 -> {
                Row(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    MediaItem(url = mediaUrls[0], modifier = Modifier.weight(1f).fillMaxHeight())
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        MediaItem(url = mediaUrls[1], modifier = Modifier.weight(1f).fillMaxWidth())
                        Spacer(modifier = Modifier.height(4.dp))
                        MediaItem(url = mediaUrls[2], modifier = Modifier.weight(1f).fillMaxWidth())
                    }
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                    Row(modifier = Modifier.weight(1f)) {
                        MediaItem(url = mediaUrls[0], modifier = Modifier.weight(1f).fillMaxHeight())
                        Spacer(modifier = Modifier.width(4.dp))
                        MediaItem(url = mediaUrls[1], modifier = Modifier.weight(1f).fillMaxHeight())
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.weight(1f)) {
                        MediaItem(url = mediaUrls[2], modifier = Modifier.weight(1f).fillMaxHeight())
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            MediaItem(url = mediaUrls[3], modifier = Modifier.fillMaxSize())
                            if (totalMedia > 4) {
                                Box(
                                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+${totalMedia - 3}",
                                        color = Color.White,
                                        fontSize = 24.sp,
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
    modifier: Modifier = Modifier
) {
    if (isVideoUrl(url)) {
        // Nếu là video, sử dụng VideoPlayer
        VideoPlayer(videoUrl = url, modifier = modifier.clip(RoundedCornerShape(4.dp)))
    } else {
        // Nếu là ảnh, sử dụng AsyncImage
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .decoderFactory(VideoFrameDecoder.Factory())
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = modifier.clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
    }
}