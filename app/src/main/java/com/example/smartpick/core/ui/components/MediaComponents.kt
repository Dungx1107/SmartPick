package com.example.smartpick.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MediaGrid(
    mediaUrls: List<String>,
    onMediaClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val count = mediaUrls.size
    Box(modifier = modifier.fillMaxWidth().heightIn(max = 400.dp)) {
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
            else -> {
                Column(Modifier.fillMaxWidth().height(400.dp)) {
                    MediaItem(mediaUrls[0], Modifier.weight(1f).fillMaxWidth()) { onMediaClick(0) }
                    Spacer(Modifier.height(2.dp))
                    Row(Modifier.weight(1f).fillMaxWidth()) {
                        MediaItem(mediaUrls[1], Modifier.weight(1f).fillMaxHeight()) { onMediaClick(1) }
                        Spacer(Modifier.width(2.dp))
                        MediaItem(mediaUrls[2], Modifier.weight(1f).fillMaxHeight()) { onMediaClick(2) }
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
        if (url.contains(".mp4") || url.contains("video")) {
            Icon(
                imageVector = Icons.Outlined.PlayCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp).align(Alignment.Center)
            )
        }
    }
}
