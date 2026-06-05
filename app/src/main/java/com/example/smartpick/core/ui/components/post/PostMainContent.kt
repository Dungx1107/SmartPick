package com.example.smartpick.core.ui.components.post

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.ui.theme.AccentBlue
// FIX: Nhúng VideoPlayer vào Feed
import com.example.smartpick.core.ui.components.VideoPlayer

// Helper kiểm tra URL có phải video không
private fun isVideoUrl(url: String): Boolean {
    val lower = url.lowercase()
    return lower.endsWith(".mp4") || lower.contains(".mp4?") ||
            lower.endsWith(".mov") || lower.contains(".mov?") ||
            lower.endsWith(".webm") || lower.contains(".webm?")
}

@Composable
fun PostMainContent(
    modifier: Modifier = Modifier,
    content: String?,
    mediaUrls: List<String> = emptyList(),
    product: Product? = null,
    onMediaClick: (Int) -> Unit = {},
    onProductClick: (Product) -> Unit = {}
) {
    Column(modifier = modifier) {
        if (!content.isNullOrBlank()) Text(
            text = content,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 15.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (mediaUrls.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            MediaGrid(
                mediaUrls = mediaUrls,
                onMediaClick = onMediaClick,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
        product?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clickable { onProductClick(it) },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = it.imageUrls.firstOrNull(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = it.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        val priceFormatted = String.format("%,.0f đ", it.price).replace(",", ".")
                        Text(
                            text = priceFormatted,
                            color = AccentBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

// Widget dùng chung để hiển thị Ảnh hoặc Video
@Composable
private fun MediaCell(url: String, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier = modifier.clickable { onClick() }) {
        if (isVideoUrl(url)) {
            VideoPlayer(videoUrl = url, modifier = Modifier.fillMaxSize())
        } else {
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun MediaGrid(mediaUrls: List<String>, modifier: Modifier = Modifier, onMediaClick: (Int) -> Unit) {
    val imageHeight = 220.dp
    val imageShape = RoundedCornerShape(8.dp)
    Box(modifier = modifier.fillMaxWidth()) {
        when (mediaUrls.size) {
            1 -> MediaCell(
                url = mediaUrls[0],
                modifier = Modifier.fillMaxWidth().height(imageHeight).clip(imageShape),
                onClick = { onMediaClick(0) }
            )

            2 -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(2) { index ->
                    MediaCell(
                        url = mediaUrls[index],
                        modifier = Modifier.weight(1f).height(imageHeight).clip(imageShape),
                        onClick = { onMediaClick(index) }
                    )
                }
            }

            else -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MediaCell(
                    url = mediaUrls[0],
                    modifier = Modifier.weight(1f).height(imageHeight).clip(imageShape),
                    onClick = { onMediaClick(0) }
                )
                Box(modifier = Modifier.weight(1f).height(imageHeight).clip(imageShape).clickable { onMediaClick(1) }) {

                    MediaCell(
                        url = mediaUrls[1],
                        modifier = Modifier.fillMaxSize(),
                        onClick = { onMediaClick(1) }
                    )

                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${mediaUrls.size - 2}",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}