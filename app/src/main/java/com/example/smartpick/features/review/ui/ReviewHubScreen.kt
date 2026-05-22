package com.example.smartpick.features.review.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReviewResponse
import com.example.smartpick.core.ui.theme.*
import com.example.smartpick.features.review.viewmodel.ReviewHubViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewHubScreen(
    onNavigateToWriteReview: (String) -> Unit,
    viewModel: ReviewHubViewModel = hiltViewModel()
) {
    val productsToReview by viewModel.productsToReview.collectAsState()
    val reviewedProducts by viewModel.reviewedProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chưa đánh giá", "Đã đánh giá")

    LaunchedEffect(Unit) { viewModel.fetchReviewData() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // FIX: Dùng màu nền chuẩn
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Đánh giá sản phẩm", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = SmartPickColor,
                indicator = { tabPositions ->
                    SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), color = SmartPickColor)
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) SmartPickColor else TextMuted,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SmartPickColor, strokeWidth = 3.dp)
                }
            } else {
                when (selectedTabIndex) {
                    0 -> PendingReviewList(productsToReview, onNavigateToWriteReview)
                    1 -> CompletedReviewList(reviewedProducts)
                }
            }
        }
    }
}

@Composable
fun PendingReviewList(products: List<Product>, onReviewClick: (String) -> Unit) {
    if (products.isEmpty()) {
        EmptyState("Bạn không có sản phẩm nào chờ đánh giá")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp), // FIX: Chuẩn hóa góc 12dp
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = product.imageUrls.firstOrNull(),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 14.sp)

                            // FIX: Chuẩn hóa format tiền tệ
                            val priceFormatted = String.format("%,.0f đ", product.price).replace(",", ".")
                            Text(priceFormatted, color = ErrorRed, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = { onReviewClick(product.id ?: "") },
                            colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Đánh giá", fontSize = 12.sp, color = White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedReviewList(reviews: List<ReviewResponse>) {
    if (reviews.isEmpty()) {
        EmptyState("Bạn chưa có đánh giá nào")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reviews) { review -> CompletedReviewCard(review) }
        }
    }
}

@Composable
fun CompletedReviewCard(review: ReviewResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            review.products?.let { product ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = product.imageUrls.firstOrNull(),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(product.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Row {
                repeat(review.rating) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.content, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(review.createdAt.split("T").firstOrNull() ?: "", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.History, null, modifier = Modifier.size(64.dp), tint = TextMuted)
            Spacer(Modifier.height(16.dp))
            Text(message, color = TextMuted)
        }
    }
}