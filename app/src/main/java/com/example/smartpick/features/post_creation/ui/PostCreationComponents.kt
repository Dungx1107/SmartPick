package com.example.smartpick.features.post_creation.ui

import android.net.Uri
import android.os.Parcelable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import com.example.smartpick.R
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.ProfileAvatar
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.features.post_creation.viewmodel.CreatePostUiState
import kotlinx.parcelize.Parcelize

/**
 * Trạng thái form nhập thông tin sản phẩm.
 */
@Parcelize
data class ProductFormState(
    val name: String = "",
    val price: String = "",
    val category: String = "",
    val brand: String = ""
): Parcelable {
    fun isValid() = name.isNotBlank()
            && price.isNotBlank()
            && category.isNotBlank()
}

/**
 * TopBar cho màn hình tạo bài viết.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostTopBar(
    isSubmitEnabled: Boolean,
    isLoading: Boolean,
    onClose: () -> Unit,
    onSubmit: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { 
            Text(
                stringResource(R.string.TaoBaiViet), 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ) 
        },
        navigationIcon = {
            IconButton(onClick = onClose) { 
                Icon(
                    Icons.Default.Close, 
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                ) 
            }
        },
        actions = {
            Button(
                onClick = onSubmit,
                enabled = isSubmitEnabled && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = TextMuted
                ),
                modifier = Modifier.padding(end = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp), 
                        color = MaterialTheme.colorScheme.onPrimary, 
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.Dang), fontWeight = FontWeight.Bold)
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Thanh chuyển đổi loại bài viết.
 */
@Composable
fun PostTypeTabs(selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf(stringResource(R.string.ThaoLuan), stringResource(R.string.DangBan))
    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = { 
            HorizontalDivider(
                thickness = 0.5.dp, 
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ) 
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                text = { 
                    Text(
                        title, 
                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                    ) 
                }
            )
        }
    }
}

/**
 * Header hiển thị thông tin người dùng.
 */
@Composable
fun UserProfileHeader(user: User?, isSelling: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
        ProfileAvatar(avatarUrl = user?.avatarUrl, size = 48.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = user?.fullName ?: stringResource(R.string.NguoiDungSmartPick),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant, 
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = if (isSelling) stringResource(R.string.DangBanHang) else stringResource(R.string.CongKhai),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Ô nhập nội dung bài viết.
 */
@Composable
fun PostContentInput(content: String, isSelling: Boolean, onContentChange: (String) -> Unit) {
    TextField(
        value = content,
        onValueChange = onContentChange,
        placeholder = { 
            Text(
                text = if (isSelling) "Mô tả sản phẩm của bạn..." else "Bạn đang nghĩ gì?", 
                fontSize = 18.sp,
                color = TextMuted
            ) 
        },
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 120.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent, 
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent, 
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
    )
}

/**
 * Khu vực chọn ảnh/video cho bài viết.
 */
@Composable
fun MediaSelectionSection(
    selectedUris: List<Uri>,
    onMediaSelected: (List<Uri>) -> Unit,
    onMediaRemoved: (Uri) -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { onMediaSelected(it) }
    )
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { it?.let { uri -> onMediaSelected(listOf(uri)) } }
    )
    val context = LocalContext.current

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), 
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { 
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    ) 
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(8.dp), 
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Icon(
                    Icons.Default.Image, 
                    contentDescription = null, 
                    tint = Color(0xFF45BD62) // Keeping semantic green
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.Anhr), color = MaterialTheme.colorScheme.onSurface)
            }
            OutlinedButton(
                onClick = { 
                    videoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                    ) 
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(8.dp), 
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Icon(
                    Icons.Default.Videocam, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Video", color = MaterialTheme.colorScheme.onSurface)
            }
        }

        if (selectedUris.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), 
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedUris) { uri ->
                    Box(modifier = Modifier.size(100.dp)) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(uri)
                                .decoderFactory(VideoFrameDecoder.Factory())
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        if (context.contentResolver.getType(uri)?.contains("video") == true) {
                            Icon(
                                Icons.Default.PlayCircle, 
                                contentDescription = null, 
                                tint = Color.White.copy(alpha = 0.8f), 
                                modifier = Modifier.align(Alignment.Center).size(32.dp)
                            )
                        }
                        IconButton(
                            onClick = { onMediaRemoved(uri) }, 
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close, 
                                contentDescription = null, 
                                tint = Color.White, 
                                modifier = Modifier.background(Color.Black.copy(0.5f), CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Form nhập thông tin chi tiết sản phẩm.
 */
@Composable
fun ProductDetailsForm(state: ProductFormState, onStateChange: (ProductFormState) -> Unit) {
    Column {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp), 
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
        Text(
            "Thông tin chi tiết sản phẩm", 
            fontWeight = FontWeight.Bold, 
            modifier = Modifier.padding(bottom = 12.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        OutlinedTextField(
            value = state.name, 
            onValueChange = { onStateChange(state.copy(name = it)) },
            label = { Text("Tên sản phẩm *") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            OutlinedTextField(
                value = state.price, 
                onValueChange = { onStateChange(state.copy(price = it)) },
                label = { Text("Giá bán (VNĐ) *") }, 
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1.1f), 
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = state.category, 
                onValueChange = { onStateChange(state.copy(category = it)) },
                label = { Text("Danh mục *") }, 
                modifier = Modifier.weight(0.9f), 
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = state.brand, 
            onValueChange = { onStateChange(state.copy(brand = it)) },
            label = { Text("Thương hiệu") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}

/**
 * Overlay hiển thị trạng thái màn hình.
 */
@Composable
fun StateOverlay(uiState: CreatePostUiState) {
    if (uiState is CreatePostUiState.Error) {
        val displayMsg = if (uiState.message.contains("Authorization")) 
            "Lỗi quyền truy cập RLS Policy trên Supabase!" 
        else 
            "Đã xảy ra lỗi khi đăng bài. Vui lòng thử lại."
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Text(
                text = displayMsg, 
                color = MaterialTheme.colorScheme.error, 
                fontWeight = FontWeight.Bold, 
                modifier = Modifier.padding(bottom = 80.dp)
            )
        }
    }
    if (uiState is CreatePostUiState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)), 
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

// ======================================================
// PREVIEW
// ======================================================

@Preview(showBackground = true)
@Composable
private fun CreatePostTopBarPreview() {
    SmartPickTheme {
        CreatePostTopBar(
            isSubmitEnabled = true,
            isLoading = false,
            onClose = {},
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostTypeTabsPreview() {
    SmartPickTheme {
        PostTypeTabs(
            selectedIndex = 0,
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserProfileHeaderPreview() {
    SmartPickTheme {
        UserProfileHeader(
            user = User(
                id = "1",
                fullName = "Nguyễn Văn A",
                avatarUrl = null
            ),
            isSelling = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostContentInputPreview() {
    SmartPickTheme {
        PostContentInput(
            content = "",
            isSelling = false,
            onContentChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailsFormPreview() {
    SmartPickTheme {
        ProductDetailsForm(
            state = ProductFormState(
                name = "iPhone 15 Pro Max",
                price = "32000000",
                category = "Điện thoại",
                brand = "Apple"
            ),
            onStateChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StateOverlayLoadingPreview() {
    SmartPickTheme {
        StateOverlay(
            uiState = CreatePostUiState.Loading
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StateOverlayErrorPreview() {
    SmartPickTheme {
        StateOverlay(
            uiState = CreatePostUiState.Error(
                "Authorization failed"
            )
        )
    }
}