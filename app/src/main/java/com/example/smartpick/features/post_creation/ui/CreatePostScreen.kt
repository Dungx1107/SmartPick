package com.example.smartpick.features.post_creation.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.ProfileAvatar
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import com.example.smartpick.R
import com.example.smartpick.features.post_creation.viewmodel.CreatePostUiState
import com.example.smartpick.features.post_creation.viewmodel.CreatePostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    currentUser: User?, // người dùng hiện tại
    onClose: () -> Unit,
    viewModel: CreatePostViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(stringResource(R.string.ThaoLuan), stringResource(R.string.DangBan))

    var content by rememberSaveable { mutableStateOf("") }

    // State Sản phẩm
    var productName by rememberSaveable { mutableStateOf("") }
    var productBrand by rememberSaveable { mutableStateOf("") }
    var productCategory by rememberSaveable { mutableStateOf("") }
    var productPrice by rememberSaveable { mutableStateOf("") }

    // State lưu danh sách URI của các ảnh/video đã chọn
    var selectedMediaUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val isSubmitEnabled = if (selectedTabIndex == 0) {
        content.isNotBlank() || selectedMediaUris.isNotEmpty()
    } else {
        productName.isNotBlank() && productPrice.isNotBlank() && productCategory.isNotBlank()
    }

    // Launcher chọn ảnh (Cho phép chọn nhiều ảnh)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> selectedMediaUris = selectedMediaUris + uris }
    )

    // Launcher chọn video (Chỉ chọn 1 video hoặc nhiều tùy cấu hình)
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { selectedMediaUris = selectedMediaUris + it } }
    )

    // Xử lý khi đăng thành công
    LaunchedEffect(uiState) {
        if (uiState is CreatePostUiState.Success) {
            onClose() // Tự đóng màn hình khi đăng xong
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.TaoBaiViet),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.Dong)
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            val product = if (selectedTabIndex == 1) {
                                Product(
                                    id = null, // Để DB tự tạo UUID
                                    ownerId = currentUser?.id ?: "",
                                    name = productName,
                                    brand = productBrand,
                                    category = productCategory,
                                    price = productPrice.toDoubleOrNull() ?: 0.0,
//                                    imageUrls = emptyList()
                                )
                            } else null

                            viewModel.createPost(
                                content = content,
                                mediaUris = selectedMediaUris, // Truyền List<Uri> thật
                                product = product,
                                context = context
                            )
                        },
                        enabled = isSubmitEnabled && uiState !is CreatePostUiState.Loading,                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1877F2),
                            disabledContainerColor = Color(0xFFE4E6EB)
                        ),
                        modifier = Modifier.padding(end = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        if (uiState is CreatePostUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.Dang), fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                // Thanh chuyển Tab
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = Color(0xFF1877F2),
                    divider = { HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE4E6EB)) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // ─── THÔNG TIN NGƯỜI ĐĂNG ───
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        ProfileAvatar(
                            avatarUrl = currentUser?.avatarUrl,
                            size = 48.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = currentUser?.fullName
                                    ?: stringResource(R.string.NguoiDungSmartPick),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            // Hiển thị chế độ công khai/nhóm
                            Surface(
                                color = Color(0xFFF0F2F5),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (selectedTabIndex == 1) stringResource(R.string.DangBanHang) else stringResource(
                                        R.string.CongKhai
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // ─── PHẦN NHẬP NỘI DUNG CHÍNH ───
                    TextField(
                        value = content,
                        onValueChange = { content = it },
                        placeholder = {
                            Text(
                                text = if (selectedTabIndex == 0) "Bạn đang nghĩ gì?" else "Mô tả sản phẩm của bạn...",
                                fontSize = 18.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 120.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
                    )

                    // ─── NÚT THÊM MEDIA ───
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                Color(0xFFE4E6EB)
                            )
                        ) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                tint = Color(0xFF45BD62)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.Anhr), color = Color.Black)
                        }

                        OutlinedButton(
                            onClick = {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                Color(0xFFE4E6EB)
                            )
                        ) {
                            Icon(
                                Icons.Default.Videocam,
                                contentDescription = null,
                                tint = Color(0xFFE53935)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Video", color = Color.Black)
                        }
                    }

                    if (selectedMediaUris.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedMediaUris) { uri ->
                                Box(modifier = Modifier.size(100.dp)) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(uri)
                                            .decoderFactory(VideoFrameDecoder.Factory())// Giúp hiện hình video
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    // Nếu là video, hãy thêm một icon "Play" đè lên cho người dùng biết
                                    if (context.contentResolver.getType(uri)
                                            ?.contains("video") == true
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.8f),
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .size(32.dp)
                                        )
                                    }
                                    // Nút xóa ảnh đã chọn
                                    IconButton(
                                        onClick = { selectedMediaUris = selectedMediaUris - uri },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.background(
                                                Color.Black.copy(0.5f),
                                                CircleShape
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ─── FORM SẢN PHẨM (CHỈ HIỆN KHI CHỌN TAB ĐĂNG BÁN) ───
                    if (selectedTabIndex == 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 0.5.dp
                        )
                        Text(
                            "Thông tin chi tiết sản phẩm",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            label = { Text("Tên sản phẩm *") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row {
                            OutlinedTextField(
                                value = productPrice,
                                onValueChange = { productPrice = it },
                                label = { Text("Giá bán (VNĐ) *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            OutlinedTextField(
                                value = productCategory,
                                onValueChange = { productCategory = it },
                                label = { Text("Danh mục *") },
                                modifier = Modifier.weight(0.9f),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = productBrand,
                            onValueChange = { productBrand = it },
                            label = { Text("Thương hiệu") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Hiển thị thông báo lỗi nếu có
            if (uiState is CreatePostUiState.Error) {
                val errorMsg = (uiState as CreatePostUiState.Error).message
                val displayMsg = if (errorMsg.contains("Authorization")) {
                    "Lỗi quyền truy cập: Hãy kiểm tra RLS Policy trên Supabase!"
                } else {
                    "Đã xảy ra lỗi khi đăng bài. Vui lòng thử lại."
                }

                Text(
                    text = displayMsg,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
                )
            }

            // Lớp phủ khi đang Loading (Chặn người dùng tương tác)
            if (uiState is CreatePostUiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1877F2))
                }
            }
        }
    }

}


@Preview(showBackground = true, showSystemUi = true, name = "Create Post - Discussion")
@Composable
fun CreatePostScreenPreview_Discussion() {

    val mockUser = User(
        id = "1",
        email = "test@gmail.com",
        fullName = "Nguyễn Văn A",
        username = "nguyenvana",
        avatarUrl = null,
        phoneNumber = "0123456789"
    )

    MaterialTheme {
        CreatePostScreen(
            currentUser = mockUser,
            onClose = {},
        )
    }
}