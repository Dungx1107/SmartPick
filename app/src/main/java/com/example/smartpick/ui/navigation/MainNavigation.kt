package com.example.smartpick.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.ui.screens.home.AccentBlue
import com.example.smartpick.ui.screens.home.TextMuted
import com.example.smartpick.ui.screens.home.White

@Composable
fun MainBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    data class NavItem(
        val icon: ImageVector,
        val label: String,
        val route: String
    )

    // danh sach 4 nut chon phan duoi
    val items = listOf(
        NavItem(
            Icons.Outlined.Home,
            stringResource(R.string.home),
            stringResource(R.string.home)
        ),
        NavItem(
            Icons.Outlined.AutoAwesome,
            stringResource(R.string.ai_curator),
            stringResource(R.string.ai_curator)
        ),
        NavItem(
            Icons.Outlined.BookmarkBorder,
            stringResource(R.string.saved),
            stringResource(R.string.saved)
        ),
        NavItem(
            Icons.Outlined.Person,
            stringResource(R.string.profile),
            stringResource(R.string.profile)
        ),
    )

    NavigationBar(
        containerColor = White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(item.label, fontSize = 10.sp)
                },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentBlue,
                    selectedTextColor = AccentBlue,
                    unselectedIconColor = Color.DarkGray,
                    unselectedTextColor = Color.DarkGray,
                    indicatorColor = Color(0xFFEBF3FF)
                )
            )
        }
    }
}

@Preview
@Composable
fun MainBottomBarPreview() {
    MainBottomBar("kaka", {})
}