package com.example.smartpick.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.ui.theme.PageBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit = {},
    onSaveProfile: () -> Unit = {}
) {
    // Khởi tạo các state để quản lý dữ liệu form
    var name by remember { mutableStateOf("Nguyễn Bá Trọng Tín") }
    var role by remember { mutableStateOf("Cloud & DevOps Intern") }
    var email by remember { mutableStateOf("trongtin@smartpick.com") }
    var phone by remember { mutableStateOf("+84 123 456 789") }
    var dob by remember { mutableStateOf("01/01/2005") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chỉnh sửa hồ sơ",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A),
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E3A8A)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onSaveProfile) {
                        Text(
                            "Lưu",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A),
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8FAFC))
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

            // Editable Avatar
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
                        .border(4.dp, Color.White, CircleShape)
                        .background(Color(0xFFE2E8F0))
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color.Gray,
                        modifier = Modifier.size(56.dp)
                    )
                }

                // Camera Badge Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E3A8A))
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { /* TODO: Open Image Picker */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Edit Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Form Fields
            ProfileTextField(
                label = "Họ và Tên",
                value = name,
                onValueChange = { name = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = "Công việc / Vai trò",
                value = role,
                onValueChange = { role = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = "Số điện thoại",
                value = phone,
                onValueChange = { phone = it },
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = "Năm sinh",
                value = dob,
                onValueChange = { dob = it },
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Primary Save Button
            Button(
                onClick = onSaveProfile,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "Cập nhật thông tin",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF64748B)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1E3A8A),
            unfocusedBorderColor = Color(0xFFCBD5E1),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        singleLine = true
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LuminaChatScreenPreview() {
    EditProfileScreen();
}