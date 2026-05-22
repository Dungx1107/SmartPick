package com.example.smartpick.features.review.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartpick.core.ui.theme.*
import com.example.smartpick.features.home.viewmodel.HomeUiState
import com.example.smartpick.features.home.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(
    productId: String,
    onBack: () -> Unit,
    onReviewSubmitted: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var rating by remember { mutableIntStateOf(5) }
    var content by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Lấy dữ liệu sản phẩm từ ViewModel để hiển thị UI
    val uiState by viewModel.uiState.collectAsState()
    val product = remember(uiState, productId) {
        if (uiState is HomeUiState.Success) {
            (uiState as HomeUiState.Success).products.find { it.id == productId }
        } else null
    }

    Scaffold(
        containerColor = PageBg,
        topBar = {
            TopAppBar(
                title = { Text("Viết đánh giá", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // FIX: THẺ TÓM TẮT SẢN PHẨM TRƯỚC KHI ĐÁNH GIÁ (CHUẨN SHOPEE)
            if (product != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SurfaceCard),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = product.imageUrls.firstOrNull(),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(product.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(4.dp))
                            val priceFormatted = String.format("%,.0f đ", product.price).replace(",", ".")
                            Text(priceFormatted, color = ErrorRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Chất lượng sản phẩm", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        repeat(5) { index ->
                            IconButton(
                                onClick = { rating = index + 1 },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < rating) Color(0xFFFFC107) else TextMuted,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("Hãy chia sẻ những điều bạn thích về sản phẩm này nhé...", color = TextMuted) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SmartPickColor,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (content.isBlank()) {
                        Toast.makeText(context, "Vui lòng viết nội dung đánh giá!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.submitProductReview(
                        productId = productId,
                        rating = rating,
                        content = content,
                        onSuccess = {
                            Toast.makeText(context, "Đánh giá thành công!", Toast.LENGTH_SHORT).show()
                            onReviewSubmitted()
                        },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Gửi Đánh Giá", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}