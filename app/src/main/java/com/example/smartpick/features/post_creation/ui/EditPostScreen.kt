package com.example.smartpick.features.post_creation.ui

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.post_creation.viewmodel.EditPostUiState
import com.example.smartpick.features.post_creation.viewmodel.EditPostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    postId: String,
    onClose: () -> Unit,
    viewModel: EditPostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var isInitialized by remember { mutableStateOf(false) }

    // Các State chứa dữ liệu đang chỉnh sửa
    var content by remember { mutableStateOf("") }
    var existingUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var newUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var hasProduct by remember { mutableStateOf(false) }
    var productId by remember { mutableStateOf<String?>(null) }
    var productState by remember { mutableStateOf(ProductFormState()) }

    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is EditPostUiState.Success -> {
                if (!isInitialized) {
                    val data = (uiState as EditPostUiState.Success)
                    content = data.post.content ?: ""

                    // FIX: Bỏ toán tử ?: vì post.mediaUrls đã là List<String> (non-nullable)
                    existingUrls = data.post.mediaUrls

                    if (data.product != null) {
                        hasProduct = true
                        productId = data.product.id
                        productState = ProductFormState(
                            name = data.product.name,
                            price = data.product.price.toLong().toString(), // Ép giá về chuỗi
                            // FIX: Thêm ?: "" vì data.product.category có thể rỗng
                            category = data.product.category ?: "",
                            brand = data.product.brand ?: ""
                        )
                    }
                    isInitialized = true
                }
            }
            is EditPostUiState.UpdateSuccess -> {
                Toast.makeText(context, (uiState as EditPostUiState.UpdateSuccess).message, Toast.LENGTH_SHORT).show()
                onClose()
            }
            is EditPostUiState.Error -> {
                Toast.makeText(context, (uiState as EditPostUiState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chỉnh sửa bài viết", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) { Icon(Icons.Default.Close, null) }
                },
                actions = {
                    val isSubmitEnabled = content.isNotBlank() || existingUrls.isNotEmpty() || newUris.isNotEmpty()

                    Button(
                        onClick = {
                            val updatedProduct = if (hasProduct) {
                                Product(
                                    id = productId,
                                    ownerId = (uiState as? EditPostUiState.Success)?.user?.id ?: "",
                                    name = productState.name,
                                    price = productState.price.toDoubleOrNull() ?: 0.0,
                                    category = productState.category,
                                    brand = productState.brand
                                )
                            } else null

                            viewModel.savePostChanges(postId, content, existingUrls, newUris, updatedProduct, context)
                        },
                        enabled = isSubmitEnabled && uiState !is EditPostUiState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        if (uiState is EditPostUiState.Loading && isInitialized) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Lưu", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            if (uiState is EditPostUiState.Loading && !isInitialized) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (isInitialized) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    val user = (uiState as? EditPostUiState.Success)?.user
                    UserProfileHeader(user = user, isSelling = hasProduct)

                    PostContentInput(
                        content = content,
                        isSelling = hasProduct,
                        onContentChange = { content = it }
                    )

                    // Hiển thị và cho phép XÓA ảnh cũ
                    if (existingUrls.isNotEmpty()) {
                        Text("Ảnh/Video cũ:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(existingUrls) { url ->
                                Box(modifier = Modifier.size(100.dp)) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { existingUrls = existingUrls - url },
                                        modifier = Modifier.align(Alignment.TopEnd).size(24.dp).padding(2.dp)
                                    ) {
                                        Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.background(Color.Black.copy(0.5f), CircleShape))
                                    }
                                }
                            }
                        }
                    }

                    // Khu vực THÊM ảnh/video mới (Tái sử dụng từ CreatePost)
                    Text("Thêm Ảnh/Video mới:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                    MediaSelectionSection(
                        selectedUris = newUris,
                        onMediaSelected = { newUris = newUris + it },
                        onMediaRemoved = { newUris = newUris - it }
                    )

                    // Form chỉnh sửa Thông tin Sản phẩm
                    if (hasProduct) {
                        Spacer(modifier = Modifier.height(16.dp))
                        ProductDetailsForm(
                            state = productState,
                            onStateChange = { productState = it }
                        )
                    }
                }
            }
        }
    }
}