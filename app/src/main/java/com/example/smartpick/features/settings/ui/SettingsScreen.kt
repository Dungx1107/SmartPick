package com.example.smartpick.features.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.SmartPickTheme
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
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

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
                    Text(stringResource(R.string.DangXuat), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.Huy))
                }
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.ChonNgonNgu)) },
            text = {
                Column {
                    val languages = listOf("vi" to stringResource(R.string.TiengViet), "en" to stringResource(R.string.TiengAnh))
                    languages.forEach { (code, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(code)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLanguage == code,
                                onClick = {
                                    viewModel.setLanguage(code)
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(name, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.Dong))
                }
            }
        )
    }

    SettingsContent(
        isDarkMode = isDarkMode,
        currentLanguage = currentLanguage,
        onThemeToggle = { viewModel.toggleTheme(it) },
        onLanguageClick = { showLanguageDialog = true },
        onBackClick = onBackClick,
        onLogoutClick = { showLogoutDialog = true }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    isDarkMode: Boolean,
    currentLanguage: String,
    onThemeToggle: (Boolean) -> Unit,
    onLanguageClick: () -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var isNotiEnabled by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.CaiDat),
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                onCheckedChange = onThemeToggle
            )
            
            // Lựa chọn Ngôn ngữ
            val langText = if (currentLanguage == "vi") stringResource(R.string.TiengViet) else stringResource(R.string.TiengAnh)
            SettingsClickItem(
                title = "${stringResource(R.string.NgonNgu)}: $langText",
                icon = Icons.Default.Language,
                onClick = onLanguageClick
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
                titleColor = MaterialTheme.colorScheme.error,
                onClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.smartpick_version_1_0_0_2026),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SmartPickTheme {
        SettingsContent(
            isDarkMode = false,
            currentLanguage = "vi",
            onThemeToggle = {},
            onLanguageClick = {},
            onBackClick = {},
            onLogoutClick = {}
        )
    }
}