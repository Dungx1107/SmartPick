package com.example.smartpick.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.DynamicFeed
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.AccentBlue

@Composable
fun MainBottomBar(
    navController: NavController,
    onNavigate: (String) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    MainBottomBarContent(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    )
}

@Composable
fun MainBottomBarContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    data class NavItem(
        val icon: ImageVector,
        val label: String,
        val route: String
    )

    val items = listOf(
        NavItem(
            Icons.Outlined.Home,
            stringResource(R.string.home),
            Routes.Home.route
        ),
        NavItem(
            Icons.Outlined.DynamicFeed,
            stringResource(R.string.feeds),
            Routes.Feed.route
        ),
        NavItem(
            Icons.Outlined.AutoAwesome,
            stringResource(R.string.reviews),
            Routes.ReviewHub.route
        ),
        NavItem(
            Icons.Outlined.BookmarkBorder,
            stringResource(R.string.saved),
            Routes.Saved.route
        ),
        NavItem(
            Icons.Outlined.Person,
            stringResource(R.string.profile),
            Routes.Profile.route
        ),
    )

    NavigationBar(
        containerColor = colorResource(R.color.white),
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item.label, fontSize = 10.sp) },
                selected = selected,
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

@Preview(showBackground = true)
@Composable
fun MainBottomBarPreview() {
    MainBottomBarContent(
        currentRoute = Routes.Home.route,
        onNavigate = {}
    )
}