package com.example.smartpick.features.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.core.theme.PageBg
import com.example.smartpick.core.theme.SmartPickColor
import com.example.smartpick.core.theme.SurfaceCard
import com.example.smartpick.core.theme.TextMuted
import com.example.smartpick.core.theme.White
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.profile.ui.components.ProfileTextField
import com.example.smartpick.R

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // 1. Lấy dữ liệu user hiện tại từ nguồn tin cậy duy nhất (SSOT)
    val user by authViewModel.currentUser.collectAsState()

    // 2. Khởi tạo các state cục bộ cho Form
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // 3. Tự động điền dữ liệu vào Form khi user được nạp xong
    LaunchedEffect(user) {
        user?.let {
            name = it.fullName ?: ""
            email = it.email ?: ""
            username = it.username ?: ""
        }
    }

    EditProfileContent(
        name = name,
        email = email,
        username = username,
        avatarUrl = user?.avatarUrl,
        onNameChange = { name = it },
        onEmailChange = { email = it },
        onUsernameChange = { username = it },
        onNavigateBack = onNavigateBack,
        onSaveProfile = {
            // TODO: Gọi hàm update trong ViewModel
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    name: String,
    email: String,
    username: String,
    avatarUrl: String?,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onSaveProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.ChinhSuaHoSo),
                        fontWeight = FontWeight.Bold,
                        color = SmartPickColor, // Sử dụng màu từ Color.kt
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = SmartPickColor
                        )
                    }
                },
                actions = {}
            )
        },
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
            // Editable Avatar Section
            Box(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .border(4.dp, White, CircleShape)
                        .background(SurfaceCard)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUrl != null) {
                        coil.compose.AsyncImage(
                            model = avatarUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            null,
                            tint = TextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                // Nút đổi ảnh
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SmartPickColor)
                        .border(2.dp, White, CircleShape)
                        .clickable { /* TODO: Open Image Picker */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        null,
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            ProfileTextField(label = stringResource(R.string.HoVaTen), value = name, onValueChange = onNameChange)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileTextField(label = stringResource(R.string.username), value = username, onValueChange = onUsernameChange)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileTextField(
                label = stringResource(R.string.email),
                value = email,
                onValueChange = onEmailChange,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onSaveProfile,
                colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    stringResource(R.string.CapNhatThongTin),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = White
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfilePreview() {
    // 1. Tạo dữ liệu giả lập dựa trên Model User của bạn
    val mockUser = com.example.smartpick.core.model.User(
        id = "123",
        email = "dung.nx@smartpick.com",
        fullName = "Nguyễn Xuân Dũng",
        username = "dungnx_2005",
        avatarUrl = null // Để null để test trường hợp hiện icon mặc định
    )

    // 2. Sử dụng các state giả lập để điều khiển UI trong Preview
    var name by remember { mutableStateOf(mockUser.fullName ?: "") }
    var email by remember { mutableStateOf(mockUser.email ?: "") }
    var username by remember { mutableStateOf(mockUser.username ?: "") }

    // 3. Gọi Content Composable (Stateless)
    // Cách này giúp Preview chạy được ngay lập tức mà không cần máy ảo hay database
    EditProfileContent(
        name = name,
        email = email,
        username = username,
        avatarUrl = mockUser.avatarUrl,
        onNameChange = { name = it },
        onEmailChange = { email = it },
        onUsernameChange = { username = it },
        onNavigateBack = { /* Preview: Không làm gì */ },
        onSaveProfile = { /* Preview: Không làm gì */ }
    )
}