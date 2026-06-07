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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.features.review.viewmodel.ReviewViewModel

/**
 * 1. MÀN HÌNH CHÍNH (WriteReviewScreen): Đóng vai trò là State Holder / Container.
 * Kết nối trực tiếp với ViewModel, quản lý trạng thái luồng và các Side Effect.
 */
@Composable
fun WriteReviewScreen(
    productId: String,
    onBack: () -> Unit,
    onReviewSubmitted: () -> Unit,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    // Kích hoạt nạp dữ liệu thông tin quyền đánh giá khi màn hình mở ra
    LaunchedEffect(productId) {
        viewModel.loadReviewData(productId)
    }

    WriteReviewContent(
        isSubmitting = isSubmitting,
        onBack = onBack,
        onSubmitReview = { rating, content ->
            if (content.isBlank()) {
                Toast.makeText(context, context.getString(R.string.VuiLongVietNoiDungDanhGia), Toast.LENGTH_SHORT).show()
            } else {
                viewModel.submitProductReview(
                    productId = productId,
                    rating = rating,
                    content = content,
                    onSuccess = {
                        Toast.makeText(context, context.getString(R.string.DanhGiaThanhCong), Toast.LENGTH_SHORT).show()
                        onReviewSubmitted()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    )
}

/**
 * 2. THÀNH PHẦN NỘI DUNG GIAO DIỆN (WriteReviewContent): Thành phần giao diện thuần túy (Stateless).
 * Không phụ thuộc vào ViewModel, nhận trạng thái và phát sự kiện ngược lên thông qua Lambda function.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewContent(
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onSubmitReview: (rating: Int, content: String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var content by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.DanhGiaSanPham), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isSubmitting) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Thẻ chọn số sao đánh giá (Rating Card)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                onClick = { if (!isSubmitting) rating = index + 1 },
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

            // Ô nhập nội dung text đánh giá (Review Text Field)
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("Hãy chia sẻ những điều bạn thích về sản phẩm này nhé...", color = TextMuted) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                maxLines = 5,
                enabled = !isSubmitting
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Nút bấm gửi biểu mẫu
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