package com.example.smartpick.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.SmartPickTheme

@Composable
fun MainTopBar(
    onMenuClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onTitleClick: () -> Unit = {},
    tagText: String? = null,
    showNotificationBadge: Int = 0
) {
    MainTopBarContent(
        onMenuClick = onMenuClick,
        onNotificationClick = onNotificationClick,
        onTitleClick = onTitleClick,
        tagText = tagText,
        showNotificationBadge = showNotificationBadge
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBarContent(
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onTitleClick: () -> Unit = {},
    tagText: String? = null,
    showNotificationBadge: Int
) {
    val interactionSource = remember { MutableInteractionSource() }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    // FIX UX: Tăng vùng chạm tối thiểu lên 48dp chuẩn Google
                    .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null // Tắt hiệu ứng ripple để trông như iOS
                    ) { onTitleClick() }
                    .wrapContentHeight(Alignment.CenterVertically)
                    .padding(horizontal = 8.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menu),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            if (tagText != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = tagText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            BadgedBox(
                badge = {
                    var text = showNotificationBadge.toString()
                    if (showNotificationBadge > 0) {
                        if (showNotificationBadge > 9) {
                            text = stringResource(R.string._9cong)
                        }
                        Badge(
                            modifier = Modifier.offset(x = (-6).dp, y = 2.dp),
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(text = text, fontSize = 10.sp)
                        }
                    }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.notifications),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Preview(showBackground = true, name = "Default TopBar")
@Composable
fun MainTopBarPreview() {
    SmartPickTheme {
        MainTopBarContent(
            onMenuClick = {}, onNotificationClick = {}, tagText = "AI Assist", showNotificationBadge = 0
        )
    }
}