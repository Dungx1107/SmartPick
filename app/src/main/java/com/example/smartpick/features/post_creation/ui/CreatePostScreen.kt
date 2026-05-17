package com.example.smartpick.features.post_creation.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.post_creation.viewmodel.CreatePostUiState
import com.example.smartpick.features.post_creation.viewmodel.CreatePostViewModel
import java.util.UUID

@Composable
fun CreatePostScreen(
    currentUser: User?,
    onClose: () -> Unit,
    viewModel: CreatePostViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Lắng nghe trạng thái thành công để tự động đóng màn hình
    LaunchedEffect(uiState) {
        if (uiState is CreatePostUiState.Success) {
            onClose()
        }
    }

    // Truyền state và callback xuống Stateless Composable
    CreatePostContent(
        currentUser = currentUser,
        uiState = uiState,
        onClose = onClose,
        onSubmit = { content, mediaUris, product ->
            viewModel.createPost(content, mediaUris, product, context)
        }
    )
}

// ─── STATELESS COMPOSABLE (Chỉ lo hiển thị UI, không chứa ViewModel) ───
@Composable
fun CreatePostContent(
    currentUser: User?,
    uiState: CreatePostUiState,
    onClose: () -> Unit,
    onSubmit: (content: String, mediaUris: List<Uri>, product: Product?) -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var content by rememberSaveable { mutableStateOf("") }
    var selectedMediaUris by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var productState by rememberSaveable { mutableStateOf(ProductFormState()) }

    // Điều kiện bật/tắt nút Submit
    val isSubmitEnabled = if (selectedTabIndex == 0) {
        content.isNotBlank() || selectedMediaUris.isNotEmpty()
    } else {
        productState.isValid()
    }

    Scaffold(
        topBar = {
            CreatePostTopBar(
                isSubmitEnabled = isSubmitEnabled,
                isLoading = uiState is CreatePostUiState.Loading,
                onClose = onClose,
                onSubmit = {
                    val currentUserId = currentUser?.id ?: return@CreatePostTopBar

                    val product = if (selectedTabIndex == 1) {
                        Product(
                            id = null,
                            ownerId = currentUserId,
                            name = productState.name,
                            brand = productState.brand,
                            category = productState.category,
                            price = productState.price.toDoubleOrNull() ?: 0.0,
                        )
                    } else null

                    onSubmit(content, selectedMediaUris, product)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {

            Column {
                PostTypeTabs(selectedTabIndex) { selectedTabIndex = it }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    UserProfileHeader(currentUser, isSelling = selectedTabIndex == 1)

                    PostContentInput(
                        content = content,
                        isSelling = selectedTabIndex == 1,
                        onContentChange = { content = it }
                    )

                    MediaSelectionSection(
                        selectedUris = selectedMediaUris,
                        onMediaSelected = { selectedMediaUris = selectedMediaUris + it },
                        onMediaRemoved = { selectedMediaUris = selectedMediaUris - it }
                    )

                    if (selectedTabIndex == 1) {
                        ProductDetailsForm(
                            state = productState,
                            onStateChange = { productState = it }
                        )
                    }
                }
            }
            StateOverlay(uiState)
        }
    }
}

// ─── PREVIEW SECTION ───
@Preview(showBackground = true, showSystemUi = true, name = "Create Post - Tab Thảo luận")
@Composable
fun CreatePostContentPreview_Discussion() {
    val mockUser = User(
        id = UUID.randomUUID().toString(),
        email = "dungx1107@gmail.com",
        fullName = "Nguyễn Xuân Dũng",
        username = "dungx1107",
        avatarUrl = null,
        phoneNumber = "0123456789"
    )

    SmartPickTheme {
        CreatePostContent(
            currentUser = mockUser,
            uiState = CreatePostUiState.Idle,
            onClose = {},
            onSubmit = { _, _, _ -> }
        )
    }
}