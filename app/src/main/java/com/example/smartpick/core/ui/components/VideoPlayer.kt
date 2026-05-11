package com.example.smartpick.core.ui.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // 1. Khởi tạo ExoPlayer. Dùng remember để trình phát không bị tạo lại mỗi khi UI vẽ lại.
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false // Để người dùng tự bấm Play, tránh tốn dữ liệu di động
            repeatMode = Player.REPEAT_MODE_ONE // Tự động phát lại khi hết video
        }
    }

    // 2. Quản lý vòng đời (Lifecycle).
    // Khi thoát khỏi màn hình chi tiết, phải giải phóng (release) bộ nhớ ngay lập tức.
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // 3. Nhúng PlayerView truyền thống vào Compose
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true // Hiện các nút điều khiển: Play, Pause, Thanh trượt thời gian
                setBackgroundColor(android.graphics.Color.BLACK)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp) // Chiều cao mặc định phù hợp cho điện thoại
    )
}