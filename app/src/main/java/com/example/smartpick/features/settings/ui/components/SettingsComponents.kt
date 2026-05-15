package com.example.smartpick.features.settings.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.core.ui.theme.TextPrimary
import com.example.smartpick.core.ui.theme.TextSecondary
import com.example.smartpick.core.ui.theme.White


@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary, // Sử dụng màu text chính Blue
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsSwitchItem(
    title: String,
    description: String,
    icon: ImageVector? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold, color = TextSecondary) },
        supportingContent = { Text(description, color = TextMuted) },
        leadingContent = icon?.let { { Icon(it, null, tint = SmartPickColor) } },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = SmartPickColor // Màu switch khi bật
                )
            )
        },
        colors = ListItemDefaults.colors(containerColor = White)
    )
}

@Composable
fun SettingsClickItem(
    title: String,
    icon: ImageVector,
    titleColor: Color = TextSecondary,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold, color = titleColor) },
        leadingContent = { Icon(icon, null, tint = titleColor) },
        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextMuted) },
        colors = ListItemDefaults.colors(containerColor = White)
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsSectionTitlePreview() {
    SettingsSectionTitle(title = "Giao diện")
}

@Preview(showBackground = true)
@Composable
fun SettingsSwitchItemPreview() {
    SettingsSwitchItem(
        title = "Chế độ tối",
        description = "Bật giao diện tối cho ứng dụng",
        icon = Icons.Default.Settings,
        checked = true,
        onCheckedChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsClickItemPreview() {
    SettingsClickItem(
        title = "Đăng xuất",
        icon = Icons.AutoMirrored.Filled.ExitToApp,
        onClick = {}
    )
}
