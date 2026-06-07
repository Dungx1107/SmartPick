package com.example.smartpick.features.profile.ui.main

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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront // Bổ sung Icon Cửa hàng
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.smartpick.core.ui.theme.PrimaryGradient
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.TextMuted

@Composable
fun CameraBadgeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
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
            ProfileAvatar(avatarUrl = user?.avatarUrl, size = 100.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user?.fullName
                    ?: user?.username
                    ?: stringResource(R.string.user),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
            )

            if (!user?.email.isNullOrEmpty()) {
                Text(
                    text = user!!.email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onEditProfile,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    contentColor = MaterialTheme.colorScheme.onPrimary
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
fun SettingsBentoGrid(
    onHistoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSellerDashboardClick: () -> Unit = {} // FIX: Thêm action chuyển hướng gian hàng
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingItemCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.History,
                iconBgColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                iconColor = MaterialTheme.colorScheme.primary,
                title = stringResource(R.string.LichSuMuaHang),
                description = stringResource(R.string.XemDonHang),
                onClick = onHistoryClick,
            )
            SettingItemCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Payments,
                iconBgColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                iconColor = MaterialTheme.colorScheme.secondary,
                title = stringResource(R.string.ThanhToan),
                description = stringResource(R.string.QuanLiTaiChinh)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingItemCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Settings,
                iconBgColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                iconColor = MaterialTheme.colorScheme.tertiary,
                title = stringResource(R.string.CaiDat),
                description = "Tùy chỉnh tài khoản & ứng dụng",
                onClick = onSettingsClick
            )
            SettingItemCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.HelpCenter,
                iconBgColor = MaterialTheme.colorScheme.surfaceVariant,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                title = stringResource(R.string.HoTro),
                description = stringResource(R.string.GiaiDapThacMac)
            )
        }

        // FIX: Thêm Tile Gian hàng nằm Full-width bên dưới
        SettingItemCard(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Storefront,
            iconBgColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
            iconColor = MaterialTheme.colorScheme.error,
            title = "Gian hàng của tôi",
            description = "Trưng bày sản phẩm và quản lý doanh thu",
            onClick = onSellerDashboardClick
        )
    }
}

@Composable
fun SettingItemCard(
    modifier: Modifier,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    description: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
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
        label = { Text(label, color = TextMuted) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
        singleLine = true
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewProfileHeaderCard() {
    SmartPickTheme {
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
}