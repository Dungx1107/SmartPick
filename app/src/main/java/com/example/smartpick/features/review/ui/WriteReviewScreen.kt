package com.example.smartpick.features.review.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.features.review.viewmodel.ReviewViewModel

/**
 * 1. MÀN HÌNH CHÍNH (WriteReviewScreen): Kết nối trực tiếp với logic mạng Hệ thống thông qua ViewModel.
 * Đã cập nhật tiếp nhận thêm tham số orderItemId truyền từ Navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(
    productId: String,
    orderItemId: String, // BỔ SUNG: Nhận mã dòng đơn hàng chi tiết
    viewModel: ReviewViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Viết Đánh Giá",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        WriteReviewContent(
            isSubmitting = isSubmitting,
            onBack = onBack,
            onSubmitReview = { rating, content ->
                // Truyền đầy đủ productId và orderItemId xuống ViewModel xử lý khóa lượt mua
                viewModel.submitProductReview(
                    productId = productId,
                    orderItemId = orderItemId, // ĐỒNG BỘ DỮ LIỆU SANG VIEWMODEL
                    rating = rating,
                    content = content,
                    onSuccess = {
                        Toast.makeText(context, "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show()
                        onBack()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * 2. GIAO DIỆN NỘI DUNG (WriteReviewContent): Phụ trách hiển thị UI thuần túy, tách biệt logic luồng.
 */
@Composable
fun WriteReviewContent(
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onSubmitReview: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var rating by rememberSaveable { mutableIntStateOf(5) }
    var content by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Chất lượng sản phẩm",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row {
                    repeat(5) { index ->
                        val starIndex = index + 1
                        IconButton(
                            onClick = { if (!isSubmitting) rating = starIndex },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$starIndex Sao",
                                modifier = Modifier.size(36.dp),
                                tint = if (starIndex <= rating) Color(0xFFFFB300) else Color.LightGray
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Chia sẻ cảm nhận về sản phẩm",
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            placeholder = { Text("Hãy chia sẻ những điều bạn thích hoặc chưa hài lòng về sản phẩm này nhé...") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            minLines = 5,
            enabled = !isSubmitting,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSubmitReview(rating, content) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp),
            enabled = !isSubmitting && content.isNotBlank()
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Gửi Đánh Giá",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * 3. BẢN XEM TRƯỚC GIAO DIỆN (WriteReviewPreview): Phục vụ hiển thị tĩnh trên Android Studio Layout Editor.
 */
@Preview(showBackground = true, name = "Trạng thái mặc định")
@Composable
fun WriteReviewContentNormalPreview() {
    MaterialTheme {
        WriteReviewContent(
            isSubmitting = false,
            onBack = {},
            onSubmitReview = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Trạng thái đang gửi dữ liệu")
@Composable
fun WriteReviewContentSubmittingPreview() {
    MaterialTheme {
        WriteReviewContent(
            isSubmitting = true,
            onBack = {},
            onSubmitReview = { _, _ -> }
        )
    }
}