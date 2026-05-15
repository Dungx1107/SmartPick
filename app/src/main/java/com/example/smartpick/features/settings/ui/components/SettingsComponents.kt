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
import androidx.compose.material3.MaterialTheme
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
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.TextMuted


@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground, // TextPrimary mapping
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
        headlineContent = { 
            Text(
                title, 
                fontWeight = FontWeight.SemiBold, 
                color = MaterialTheme.colorScheme.onSurface // TextSecondary mapping
            ) 
        },
        supportingContent = { Text(description, color = TextMuted) },
        leadingContent = icon?.let { { Icon(it, null, tint = MaterialTheme.colorScheme.primary) } },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Composable
fun SettingsClickItem(
    title: String,
    icon: ImageVector,
    titleColor: Color = Color.Unspecified, // Default handling below
    onClick: () -> Unit
) {
    val finalTitleColor = if (titleColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else titleColor
    
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold, color = finalTitleColor) },
        leadingContent = { Icon(icon, null, tint = finalTitleColor) },
        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextMuted) },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsSectionTitlePreview() {
    SmartPickTheme {
        SettingsSectionTitle(title = "Giao diện")
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsSwitchItemPreview() {
    SmartPickTheme {
        SettingsSwitchItem(
            title = "Chế độ tối",
            description = "Bật giao diện tối cho ứng dụng",
            icon = Icons.Default.Settings,
            checked = true,
            onCheckedChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsClickItemPreview() {
    SmartPickTheme {
        SettingsClickItem(
            title = "Đăng xuất",
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            onClick = {}
        )
    }
}
