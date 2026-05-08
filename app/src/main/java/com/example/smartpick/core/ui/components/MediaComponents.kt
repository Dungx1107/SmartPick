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

/**
 * Hiển thị danh sách media (ảnh/video) theo dạng lưới động.
 *
 * Layout sẽ tự động thay đổi dựa trên số lượng media:
 *
 * - 1 media:
 *   Hiển thị full chiều ngang.
 *
 * - 2 media:
 *   Chia đôi màn hình theo chiều dọc.
 *
 * - 3 media:
 *   1 media lớn bên trái,
 *   2 media nhỏ xếp chồng bên phải.
 *
 * - 4 media trở lên:
 *   Hiển thị dạng lưới 2x2.
 *   Nếu số lượng > 4 sẽ hiển thị overlay "+x".
 *
 * Hỗ trợ:
 * - Ảnh.
 * - Video thumbnail bằng Coil VideoFrameDecoder.
 *
 * @param mediaUrls Danh sách URL media từ Supabase Storage.
 * @param modifier Modifier tùy chỉnh cho layout tổng thể.
 */
@Composable
fun MediaGrid(
    mediaUrls: List<String>,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    // Tổng số media
    val totalMedia = mediaUrls.size

    // Không hiển thị nếu danh sách rỗng
    if (totalMedia == 0) return

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        when (totalMedia) {

            // =====================================================
            // 1 MEDIA
            // =====================================================
            1 -> {

                MediaItem(
                    url = mediaUrls[0],
                    modifier = Modifier.fillMaxWidth()
                        .height(300.dp)
                )
            }

            // =====================================================
            // 2 MEDIA
            // =====================================================
            2 -> {

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(200.dp)
                ) {

                    MediaItem(
                        url = mediaUrls[0],
                        modifier = Modifier.weight(1f)
                            .fillMaxHeight()
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    MediaItem(
                        url = mediaUrls[1],
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }

            // =====================================================
            // 3 MEDIA
            // =====================================================
            3 -> {

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(300.dp)
                ) {

                    // Media lớn bên trái
                    MediaItem(
                        url = mediaUrls[0],
                        modifier = Modifier.weight(1f)
                            .fillMaxHeight()
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // 2 media nhỏ bên phải
                    Column(
                        modifier = Modifier.weight(1f)
                            .fillMaxHeight()
                    ) {

                        MediaItem(
                            url = mediaUrls[1],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        MediaItem(
                            url = mediaUrls[2],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                    }
                }
            }

            // =====================================================
            // 4 MEDIA TRỞ LÊN
            // =====================================================
            else -> {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .height(400.dp)
                ) {

                    // Hàng trên
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {

                        MediaItem(
                            url = mediaUrls[0],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        MediaItem(
                            url = mediaUrls[1],
                            modifier = Modifier.weight(1f)
                                .fillMaxHeight()
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Hàng dưới
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {

                        MediaItem(
                            url = mediaUrls[2],
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // Media cuối cùng
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        ) {

                            MediaItem(
                                url = mediaUrls[3],
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay hiển thị số media còn lại
                            if (totalMedia > 4) {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.5f)),
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

/**
 * Hiển thị một media item đơn lẻ.
 *
 * Hỗ trợ:
 * - Hiển thị ảnh.
 * - Hiển thị thumbnail video tự động.
 *
 * Sử dụng:
 * - Coil AsyncImage.
 * - VideoFrameDecoder để trích frame video.
 *
 * @param url URL media từ Supabase Storage.
 * @param modifier Modifier dùng để tùy chỉnh kích thước và layout.
 */
@Composable
fun MediaItem(
    url: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(       // Cấu hình Coil ImageRequest
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)     // URL media
            .decoderFactory(VideoFrameDecoder.Factory()) // Tự động lấy thumbnail nếu là video
            .crossfade(true)  // Hiệu ứng fade khi load
            .build(),

        contentDescription = null,
        modifier = modifier.clip(RoundedCornerShape(4.dp)),
        contentScale = ContentScale.Crop   // Crop để ảnh/video phủ kín item

    )
}