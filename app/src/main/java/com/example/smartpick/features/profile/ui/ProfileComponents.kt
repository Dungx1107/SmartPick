package com.example.smartpick.features.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.ProfileAvatar
import com.example.smartpick.core.ui.theme.BadgeOrange
import com.example.smartpick.core.ui.theme.CardLight
import com.example.smartpick.core.ui.theme.PrimaryGradient
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.TextSecondary
import com.example.smartpick.core.ui.theme.White

@Composable
fun CameraBadgeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(SmartPickColor)
            .border(2.dp, White, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun ProfileHeaderCard(
    user: User?,
    onEditProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(PrimaryGradient)
            )
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Sử dụng ProfileAvatar từ core
            ProfileAvatar(avatarUrl = user?.avatarUrl, size = 100.dp)

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text(
                text = user?.fullName
                    ?: user?.username
                    ?: stringResource(R.string.user),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White,
            )

            // Email
            if (!user?.email.isNullOrEmpty()) {
                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = White.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Button
            Button(
                onClick = onEditProfile,
                colors = ButtonDefaults.buttonColors(
                    containerColor = White.copy(alpha = 0.2f),
                    contentColor = White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.ChinhSuaHoSo),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}


@Composable
fun SettingsBentoGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingItemCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.History,
                iconBgColor = CardLight,
                iconColor = SmartPickColor,
                title = stringResource(R.string.LichSuMuaHang),
                description = stringResource(R.string.XemDonHang)
            )
            SettingItemCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Payments,
                iconBgColor = Color(0xFFF3E5F5),
                iconColor = Color(0xFF7B1FA2),
                title = stringResource(R.string.ThanhToan),
                description = stringResource(R.string.QuanLiTaiChinh)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingItemCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.NotificationsActive,
                iconBgColor = Color(0xFFFFF3E0),
                iconColor = BadgeOrange,
                title = stringResource(R.string.ThongBao),
                description = stringResource(R.string.TuyChinhCachNhanTinTuc)
            )
            SettingItemCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.HelpCenter,
                iconBgColor = Color(0xFFE0F2F1),
                iconColor = Color(0xFF00796B),
                title = stringResource(R.string.HoTro),
                description = stringResource(R.string.GiaiDapThacMac)
            )
        }
    }
}

@Composable
fun SettingItemCard(
    modifier: Modifier,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    description: String
) {
    Card(
        modifier = modifier.clickable { },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = SmartPickColor
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
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


// ----------------------------------- PREVIEW --------------------------------------------------

@Preview(showBackground = true)
@Composable
fun PreviewProfileHeaderCard() {
    val mockUser = User(
        id = "1",
        email = "user@gmail.com",
        fullName = "Nguyễn Văn A",
        username = "nguyenvana",
        avatarUrl = null
    )

    ProfileHeaderCard(
        user = mockUser,
        onEditProfile = {}
    )
}
