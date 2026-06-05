package com.example.smartpick.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DynamicFeed
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartpick.R

@Composable
fun MainBottomBar(
    navController: NavController,
    unreadCount: Int,
    onNavigate: (String) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    MainBottomBarContent(
        currentRoute = currentRoute,
        unreadCount = unreadCount,
        onNavigate = onNavigate
    )
}

@Composable
fun MainBottomBarContent(
    currentRoute: String?,
    unreadCount: Int,
    onNavigate: (String) -> Unit
) {
    data class NavItem(
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
        val label: String,
        val route: String,
        val isNotification: Boolean = false
    )

    val items = listOf(
        NavItem(
            Icons.Filled.Home, Icons.Outlined.Home,
            stringResource(R.string.home), Routes.Home.route
        ),
        NavItem(
            Icons.Filled.DynamicFeed, Icons.Outlined.DynamicFeed,
            stringResource(R.string.feeds), Routes.Feed.route
        ),
        NavItem(
            Icons.Filled.Star, Icons.Outlined.StarOutline,
            stringResource(R.string.reviews), Routes.ReviewHub.route
        ),
        NavItem(
            Icons.Filled.Notifications, Icons.Outlined.Notifications,
            stringResource(R.string.alert), Routes.Notifications.route,
            isNotification = true
        ),
        NavItem(
            Icons.Filled.Person, Icons.Outlined.Person,
            stringResource(R.string.profile), Routes.Profile.route
        ),
    )

    Column {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->
                val selected = currentRoute?.substringBefore("?")?.substringBefore("/") ==
                        item.route.substringBefore("?")?.substringBefore("/")

                NavigationBarItem(
                    icon = {
                        if (item.isNotification) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        val badgeText = if (unreadCount > 9) stringResource(R.string._9cong) else unreadCount.toString()
                                        Badge(
                                            modifier = Modifier.offset(x = 4.dp, y = (-4).dp),
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ) {
                                            Text(text = badgeText, fontSize = 9.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = { Text(item.label, fontSize = 10.sp) },
                    selected = selected,
                    onClick = { onNavigate(item.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainBottomBarPreview() {
    MainBottomBarContent(
        currentRoute = Routes.Home.route,
        unreadCount = 5,
        onNavigate = {}
    )
}