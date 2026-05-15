package com.example.smartpick.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.SmartPickColor

@Composable
fun MainTopBar(
    onMenuClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    tagText: String? = null,
    showNotificationBadge: Boolean = false
) {
    MainTopBarContent(
        onMenuClick = onMenuClick,
        onNotificationClick = onNotificationClick,
        tagText = tagText,
        showNotificationBadge = showNotificationBadge
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBarContent(
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    tagText: String? = null,
    showNotificationBadge: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                fontWeight = FontWeight.Black,
                color = SmartPickColor
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = SmartPickColor
                )
            }
        },
        actions = {
            if (tagText != null) {
                Surface(
                    color = Color(0xFFD6E3FF),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = tagText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38527B),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Box {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF1E3A8A)
                    )
                }

                if (showNotificationBadge) {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp, top = 12.dp) // Căn chỉnh lại vị trí dấu chấm đỏ cho khớp với chuông
                            .size(8.dp)
                            .background(Color.Red, CircleShape)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    )
}

@Preview(showBackground = true, name = "Default TopBar")
@Composable
fun MainTopBarPreview() {
    MainTopBarContent(
        onMenuClick = {},
        onNotificationClick = {},
        tagText = "AI Assist",
        showNotificationBadge = false
    )
}

@Preview(showBackground = true, name = "TopBar with Cart Badge")
@Composable
fun MainTopBarWithBadgePreview() {
    MainTopBarContent(
        onMenuClick = {},
        onNotificationClick = {},
        showNotificationBadge = true
    )
}

@Preview(showBackground = true, name = "Custom Title")
@Composable
fun MainTopBarCustomTitlePreview() {
    MainTopBarContent(
        onMenuClick = {},
        onNotificationClick = {},
        showNotificationBadge = false
    )
}