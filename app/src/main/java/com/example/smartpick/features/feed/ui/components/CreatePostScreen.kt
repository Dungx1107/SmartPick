package com.example.smartpick.features.feed.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.profile.ui.ProfileAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    currentUserAvatar: String?,
    currentUserName: String,
    onClose: () -> Unit,
    onSubmit: (content: String, product: Product?) -> Unit // Trả về nội dung và đối tượng Product
) {
    // Post State
    var content by rememberSaveable { mutableStateOf("") }

    // Product State (Optional section)
    var isAddingProduct by rememberSaveable { mutableStateOf(false) }
    var productName by rememberSaveable { mutableStateOf("") }
    var productBranch by rememberSaveable { mutableStateOf("") }
    var productCategory by rememberSaveable { mutableStateOf("") }
    var productPrice by rememberSaveable { mutableStateOf("") }

    val isSubmitEnabled = content.isNotBlank() || (isAddingProduct && productName.isNotBlank())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo bài viết", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val product = if (isAddingProduct) {
                                Product(
                                    id = "", // Cấp ID ở tầng ViewModel/Repository
                                    name = productName,
                                    branch = productBranch,
                                    category = productCategory,
                                    price = productPrice.toDoubleOrNull() ?: 0.0,
                                    imageUrl = null
                                )
                            } else null

                            onSubmit(content, product)
                        },
                        enabled = isSubmitEnabled
                    ) {
                        Text(
                            text = "ĐĂNG",
                            fontWeight = FontWeight.Bold,
                            color = if (isSubmitEnabled) Color(0xFF1877F2) else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: User Info
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileAvatar(avatarUrl = currentUserAvatar, size = 48.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = currentUserName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            // Body: Post Content Input
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Bạn muốn chia sẻ điều gì về sản phẩm này?", fontSize = 20.sp, color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
            )

            // Nút thêm ảnh (Mô phỏng)
            Row(modifier = Modifier.padding(16.dp)) {
                OutlinedButton(onClick = { /* Mở Image Picker */ }) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF45BD62))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Thêm Ảnh/Video", color = Color.Black)
                }
            }

            HorizontalDivider(thickness = 8.dp, color = Color(0xFFF0F2F5))

            // Body: Product Details Section
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Đính kèm thông tin sản phẩm",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Switch(
                        checked = isAddingProduct,
                        onCheckedChange = { isAddingProduct = it }
                    )
                }

                if (isAddingProduct) {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("Tên sản phẩm *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = productBranch,
                            onValueChange = { productBranch = it },
                            label = { Text("Thương hiệu") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        OutlinedTextField(
                            value = productCategory,
                            onValueChange = { productCategory = it },
                            label = { Text("Danh mục") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = productPrice,
                        onValueChange = { productPrice = it },
                        label = { Text("Giá (VNĐ)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreatePostScreenPreview() {
    MaterialTheme {
        CreatePostScreen(
            currentUserAvatar = null,
            currentUserName = "Nguyễn Văn A",
            onClose = {},
            onSubmit = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreatePostScreen_WithProductPreview() {
    MaterialTheme {
        var fakeToggle by remember { mutableStateOf(true) }

        CreatePostScreen(
            currentUserAvatar = null,
            currentUserName = "Nguyễn Văn A",
            onClose = {},
            onSubmit = { _, _ -> }
        )
    }
}