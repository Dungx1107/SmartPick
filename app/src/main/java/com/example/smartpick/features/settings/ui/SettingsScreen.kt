package com.example.smartpick.features.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.ErrorRed
import com.example.smartpick.core.ui.theme.PageBg
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.White
import com.example.smartpick.features.settings.ui.components.SettingsClickItem
import com.example.smartpick.features.settings.ui.components.SettingsSectionTitle
import com.example.smartpick.features.settings.ui.components.SettingsSwitchItem
import com.example.smartpick.features.settings.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.XacNhanDangXuat)) },
            text = { Text(stringResource(R.string.CoChacMuonDangXuat)) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout(onLogoutSuccess)
                }) {
                    Text(stringResource(R.string.DangXuat), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.Huy))
                }
            }
        )
    }

    SettingsContent(
        onBackClick = onBackClick,
        onLogoutClick = { showLogoutDialog = true }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var isDarkMode by rememberSaveable { mutableStateOf(false) }
    var isNotiEnabled by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        containerColor = PageBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.CaiDat),
                        fontWeight = FontWeight.Bold,
//                        fontWeight = FontWeight.Black,
                        color = SmartPickColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = SmartPickColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White.copy(alpha = 0.9f)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Mục Giao diện
            SettingsSectionTitle(title = stringResource(R.string.GiaoDien))
            SettingsSwitchItem(
                title = stringResource(R.string.CheDoToi),
                description = if (isDarkMode) stringResource(R.string.Dangbat) else stringResource(R.string.Dangtat),
                checked = isDarkMode,
                onCheckedChange = { isDarkMode = it }
            )

            // Mục Thông báo
            SettingsSectionTitle(title = stringResource(R.string.ThongBao))
            SettingsSwitchItem(
                title = stringResource(R.string.ThongBaoUngDung),
                description = stringResource(R.string.NhanTinNhanVeDonhang),
                icon = Icons.Default.Notifications,
                checked = isNotiEnabled,
                onCheckedChange = { isNotiEnabled = it }
            )

            // Mục Tài khoản
            SettingsSectionTitle(title = stringResource(R.string.TaiKhoan))
            SettingsClickItem(
                title = stringResource(R.string.DangXuat),
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                titleColor = ErrorRed,
                onClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.smartpick_version_1_0_0_2026),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = SmartPickColor.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    MaterialTheme {
        SettingsContent(
            onBackClick = {},
            onLogoutClick = {}
        )
    }
}