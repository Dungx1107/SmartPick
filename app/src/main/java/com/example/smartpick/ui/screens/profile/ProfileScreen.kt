package com.example.smartpick.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartpick.ui.navigation.Routes
import com.example.smartpick.ui.theme.PageBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        containerColor = PageBg

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Section: User Profile Card
            ProfileCard(navController)

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Bento Grid Settings
            SettingsGrid()

            Spacer(modifier = Modifier.height(32.dp))

            // Section: Logout
            Button(
                onClick = { /* TODO: Handle Logout */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF9F403D)
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF9F403D).copy(alpha = 0.1f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng xuất", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "SMARTPICK VERSION 1.0.0 • 2026",
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileCard(
    navController: NavController,
    username: String = "username"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFD6E4FF).copy(alpha = 0.3f), Color.Transparent),
                        radius = 400f
                    )
                )
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color.White, CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                // TODO: Replace with actual image using Coil
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = username,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    navController.navigate(Routes.EditProfile.route)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF1F5F9),
                    contentColor = Color(0xFF476282)
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Chỉnh sửa hồ sơ", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun SettingsGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingItemCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.History,
                iconBgColor = Color(0xFFD6E4FF),
                iconColor = Color(0xFF455F88),
                title = "Lịch sử mua hàng",
                description = "Xem lại các đơn hàng và theo dõi vận chuyển"
            )
            SettingItemCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Payments,
                iconBgColor = Color(0xFFD9D7F8),
                iconColor = Color(0xFF5D5D78),
                title = "Thanh toán",
                description = "Quản lý thẻ tín dụng và ví điện tử của bạn"
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingItemCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.NotificationsActive,
                iconBgColor = Color(0xFFD4E4FC),
                iconColor = Color(0xFF516075),
                title = "Thông báo",
                description = "Tùy chỉnh cách bạn nhận tin tức từ ứng dụng"
            )
            SettingItemCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.HelpCenter,
                iconBgColor = Color(0xFFD7E5EB),
                iconColor = Color(0xFF283439),
                title = "Hỗ trợ",
                description = "Giải đáp thắc mắc và liên hệ CSKH"
            )
        }
    }
}

@Composable
fun SettingItemCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    description: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFF4F7))
            .clickable { /* TODO */ }
            .padding(24.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconColor)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF283439)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                lineHeight = 18.sp
            )
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    ProfileScreen(navController = navController)
}