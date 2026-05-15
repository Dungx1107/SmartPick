package com.example.smartpick.features.profile.ui.edit

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.ProfileAvatar
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.profile.ui.main.CameraBadgeButton
import com.example.smartpick.features.profile.ui.main.ProfileTextField
import com.example.smartpick.features.profile.viewmodel.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    editProfileViewModel: EditProfileViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val isUploading by editProfileViewModel.isUploading.collectAsState()
    val user by authViewModel.currentUser.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val selectedImage by editProfileViewModel.selectedImage.collectAsState()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showBottomSheet = true
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.BanCanCapQuyenCamera), Toast.LENGTH_SHORT
            ).show()
        }
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                editProfileViewModel.updateSelectedImage(uri)
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            try {
                if (bitmap != null) {
                    editProfileViewModel.updateSelectedImage(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            stringResource(R.string.ChupAnhMoi), 
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.clickable {
                        cameraLauncher.launch(null)
                        showBottomSheet = false
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
                )

                ListItem(
                    headlineContent = {
                        Text(
                            stringResource(R.string.ChonTuThuVien), 
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.clickable {
                        galleryLauncher.launch("image/*")
                        showBottomSheet = false
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        }
    }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            name = it.fullName ?: ""
            email = it.email ?: ""
            username = it.username ?: ""
            phone = it.phoneNumber ?: ""
        }
    }

    EditProfileContent(
        name = name,
        email = email,
        username = username,
        phone = phone,
        avatarUrl = user?.avatarUrl,
        isUploading = isUploading,
        selectedImage = selectedImage,
        onNameChange = { name = it },
        onEmailChange = { email = it },
        onUsernameChange = { username = it },
        onPhoneChange = { phone = it },
        onNavigateBack = onNavigateBack,
        onCameraClick = {
            val permissionCheckResult =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                showBottomSheet = true
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onSaveProfile = {
            user?.id?.let { userId ->
                editProfileViewModel.saveProfile(
                    userId = userId,
                    name = name,
                    username = username,
                    phone = phone,
                    email = email,
                    context = context,
                    currentAvatarUrl = user?.avatarUrl,
                    onSuccess = { newAvatarUrl ->
                        val updatedUser = user?.copy(
                            fullName = name,
                            username = username,
                            phoneNumber = phone,
                            email = email,
                            avatarUrl = newAvatarUrl
                        )
                        updatedUser?.let { authViewModel.updateCurrentUser(it) }

                        Toast.makeText(
                            context,
                            context.getString(R.string.CapNhatThanhCong), Toast.LENGTH_SHORT
                        ).show()
                        onNavigateBack()
                    },
                    onError = { errorMsg ->
                        Toast.makeText(
                            context,
                            context.getString(R.string.LoiCapNhat, errorMsg), Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    name: String,
    email: String,
    username: String,
    phone: String,
    avatarUrl: String?,
    isUploading: Boolean,
    selectedImage: Any?,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onCameraClick: () -> Unit,
    onSaveProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.ChinhSuaHoSo),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                ProfileAvatar(
                    avatarUrl = avatarUrl,
                    selectedImage = selectedImage,
                    size = 100.dp
                )

                if (isUploading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                CameraBadgeButton(
                    onClick = onCameraClick,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }

            ProfileTextField(
                label = stringResource(R.string.HoVaTen),
                value = name,
                onValueChange = onNameChange
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileTextField(
                label = stringResource(R.string.username),
                value = username,
                onValueChange = onUsernameChange
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = stringResource(R.string.email),
                value = email,
                onValueChange = onEmailChange,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = stringResource(R.string.phone_number),
                value = phone,
                onValueChange = onPhoneChange,
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onSaveProfile,
                enabled = !isUploading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    stringResource(R.string.CapNhatThongTin),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfilePreview() {
    SmartPickTheme {
        val mockUser = User(
            id = "123",
            email = "dung.nx@smartpick.com",
            fullName = "Nguyễn Xuân Dũng",
            username = "dungnx_2005",
            avatarUrl = null,
            phoneNumber = "0868364133"
        )

        var name by remember { mutableStateOf(mockUser.fullName ?: "") }
        var email by remember { mutableStateOf(mockUser.email ?: "") }
        var username by remember { mutableStateOf(mockUser.username ?: "") }
        var phone by remember { mutableStateOf(mockUser.phoneNumber ?: "") }

        EditProfileContent(
            name = name,
            email = email,
            phone = phone,
            username = username,
            avatarUrl = mockUser.avatarUrl,
            isUploading = false,
            onNameChange = { name = it },
            onEmailChange = { email = it },
            onUsernameChange = { username = it },
            onPhoneChange = { phone = it },
            onNavigateBack = {},
            selectedImage = null,
            onCameraClick = {},
            onSaveProfile = {}
        )
    }
}