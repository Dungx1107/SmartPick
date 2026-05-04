package com.example.smartpick.features.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartpick.R
import com.example.smartpick.core.model.User
import com.example.smartpick.core.theme.ErrorRed
import com.example.smartpick.core.theme.ErrorRedBg
import com.example.smartpick.core.theme.PageBg
import com.example.smartpick.core.theme.TextMuted
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.navigation.Routes

// 1. Stateful Composable: Quản lý logic và dữ liệu từ Hilt/Navigation
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val user by authViewModel.currentUser.collectAsState()

    ProfileContent(
        user = user,
        onLogout = {
            authViewModel.logout()
            navController.navigate(Routes.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        },
        onEditProfile = {
            navController.navigate(Routes.EditProfile.route)
        }
    )
}

// 2. Stateless Composable: Chỉ đảm nhận hiển thị UI (Cho phép Preview)
@Composable
fun ProfileContent(
    user: User?,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(PageBg) // Đồng bộ màu nền ứng dụng[cite: 1]
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sử dụng Header Card mới với Gradient xanh
        ProfileHeaderCard(user = user, onEditProfile = onEditProfile)

        Spacer(modifier = Modifier.height(28.dp))

        // Bento Grid hiển thị các tiện ích
        SettingsBentoGrid()

        Spacer(modifier = Modifier.height(32.dp))

        // Nút Đăng xuất sử dụng màu trạng thái Error[cite: 1]
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorRedBg,
                contentColor = ErrorRed
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                stringResource(R.string.DangXuat),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Thông tin phiên bản ứng dụng
        Text(
            text = stringResource(R.string.smartpick_version_1_0_0_2026),
            fontSize = 10.sp,
            color = TextMuted, // màu xám nhạt
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}


// 3. Preview: Sử dụng Stateless Composable với dữ liệu mẫu
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    // Tạo dữ liệu User giả lập cho Preview
    val mockUser = User(
        id = "1",
        email = "dung.nx@example.com",
        fullName = "Nguyễn Xuân Dũng",
        username = "dungnx",
        avatarUrl = null
    )

    ProfileContent(
        user = mockUser,
        onLogout = {},
        onEditProfile = {}
    )
}