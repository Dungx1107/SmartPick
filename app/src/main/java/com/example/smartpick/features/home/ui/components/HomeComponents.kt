package com.example.smartpick.features.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.theme.AccentBlue
import com.example.smartpick.core.theme.TextMuted
import com.example.smartpick.core.theme.TextSecondary
import com.example.smartpick.core.theme.White


@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(White)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(28.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            stringResource(R.string.TimKiemSanPham),
            color = TextMuted,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Default.CameraAlt, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
    }
}

// ─── Hero Banner ──────────────────────────────────────────────────────────────
@Composable
fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0D2137), Color(0xFF1E5F99), Color(0xFF0A3D6B))
                )
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(x = 200.dp, y = (-30).dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x40009FFF), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(x = 240.dp, y = 60.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x3000C8FF), Color.Transparent)),
                    CircleShape
                )
        )

        // Text content
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp, end = 140.dp)
        ) {
            Text(
                stringResource(R.string.KyNguyenAmThanhAI),
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 28.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.TraiNghiemTinhTeAI),
                color = Color(0xCCFFFFFF),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(White)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    stringResource(R.string.KhamPhaNgay), color = AccentBlue,
                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Right side placeholder for product image
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Headphones, contentDescription = null,
                tint = Color(0x99FFFFFF), modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun AICuratorBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFEBF3FF))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Tìm kiếm bằng AI\nCurator?",
                fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                color = AccentBlue, lineHeight = 26.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Hãy để trí tuệ nhân tạo của Lumina giúp bạn tìm thấy sản phẩm hoàn hảo chỉ qua một cuộc trò chuyện ngắn.",
                fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp
            )
            Spacer(Modifier.height(16.dp))

            // Nút bấm bắt đầu ngay
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1A4FA0), AccentBlue)))
                    .clickable { /* Xử lý mở Chat AI */ }
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, tint = White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Bắt đầu ngay", color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    SearchBar()
}

@Preview(showBackground = true)
@Composable
fun HeroBannerPreview() {
    HeroBanner()
}

@Preview(showBackground = true)
@Composable
fun AICuratorBannerPreview() {
    AICuratorBanner()
}
