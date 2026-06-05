package com.example.smartpick.features.notification.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R

// =======================================================================
// COMPONENT 1: MÔ PHỎNG THANH TRẠNG THÁI (STATUS BAR) TRÊN CÙNG MÀN HÌNH
// =======================================================================
@Composable
fun StatusBarMock() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(Color(0xFFFFFFFF)) // ĐÃ SỬA: Chuyển nền thanh trạng thái sang màu TRẮNG tinh
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Góc bên trái: Nơi hiển thị các Small Icon đơn sắc của các app gửi thông báo
        Row(verticalAlignment = Alignment.CenterVertically) {
            // ĐÃ SỬA: Chuyển màu chữ đồng hồ sang ĐEN để nổi bật trên nền trắng
            Text(text = "09:41", color = Color(0xFF1A1A1A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))

            // ĐÂY CHÍNH LÀ SMALL ICON TRÊN THANH TRẠNG THÁI
            Image(
                painter = painterResource(id = R.drawable.smartpick_icon),
                contentDescription = "Small Icon On Status Bar",
                modifier = Modifier.size(16.dp),
                // ĐÃ SỬA: Nền trắng thì Icon hệ thống tự động nhuộm đen (hoặc xám tối) để dễ nhìn
                colorFilter = ColorFilter.tint(Color(0xFF1A1A1A))
            )
        }
        // Góc bên phải: Pin, Sóng wifi
        // ĐÃ SỬA: Chuyển màu các biểu tượng hệ thống sang ĐEN
        Text(text = "📶 🔋 100%", color = Color(0xFF1A1A1A), fontSize = 11.sp)
    }
}

// =======================================================================
// COMPONENT 2: MÔ PHỎNG KHUNG THÔNG BÁO ĐẦY ĐỦ KHI VUỐT XUỐNG
// =======================================================================
@Composable
fun AndroidNotificationShadeMock(
    title: String,
    body: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF2F2F6), shape = RoundedCornerShape(28.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // [Phần 1] KHỐI AVATAR / LARGE ICON PHÍA BÊN TRÁI
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Ảnh màu hình lớn của App
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "Large Icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White, shape = CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // [Phần 2] NỘI DUNG VĂN BẢN THÔNG BÁO
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "SmartPick", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B3C68))
                    Text(text = " • 2 phút trước", fontSize = 11.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = body, fontSize = 13.sp, color = Color.DarkGray, maxLines = 1)
            }
        }
    }
}

// =======================================================================
// GIAO DIỆN PREVIEW TRÊN ANDROID STUDIO
// =======================================================================
@Preview(showBackground = true, backgroundColor = 0xFF222222)
@Composable
fun FullNotificationSystemPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "1. Lúc chưa vuốt (Nhìn trên thanh Status Bar trên cùng):",
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.padding(16.dp)
        )
        StatusBarMock()

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "2. Lúc vuốt xuống (Khung thông báo đầy đủ):",
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        AndroidNotificationShadeMock(
            title = "Bình luận mới",
            body = "nguyenxuandung457 đã phản hồi: hello"
        )
    }
}