package com.example.smartpick.features.review.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.Review
import com.example.smartpick.core.model.ReviewUser
import com.example.smartpick.core.ui.theme.*
import com.example.smartpick.features.review.ui.components.CompletedReviewList
import com.example.smartpick.features.review.ui.components.PendingReviewList
import com.example.smartpick.features.review.viewmodel.ReviewHubViewModel

@Composable
fun ReviewHubScreen(
    paddingValues: PaddingValues,
    onNavigateToWriteReview: (String) -> Unit,
    viewModel: ReviewHubViewModel = hiltViewModel()
) {
    val productsToReview by viewModel.productsToReview.collectAsState()
    val reviewedProducts by viewModel.reviewedProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchReviewData() }

    ReviewHubContent(
        productsToReview = productsToReview,
        reviewedProducts = reviewedProducts,
        isLoading = isLoading,
        paddingValues = paddingValues,
        onNavigateToWriteReview = onNavigateToWriteReview
    )
}

@Composable
fun ReviewHubContent(
    productsToReview: List<Product>,
    reviewedProducts: List<Review>,
    isLoading: Boolean,
    paddingValues: PaddingValues,
    onNavigateToWriteReview: (String) -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(stringResource(R.string.ChuaDanhGia), stringResource(R.string.DaDanhGia))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                bottom = paddingValues.calculateBottomPadding()
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Text(
                    text = stringResource(R.string.DanhGiaSanPham),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                )
            }

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else TextMuted,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp)
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

@Preview(showBackground = true, showSystemUi = true, name = "Tab Chưa Đánh Giá")
@Composable
fun ReviewHubContentPendingPreview() {
    val mockProducts = listOf(
        Product(
            id = "prod_01",
            ownerId = "user_01",
            name = "Tai nghe Bluetooth Không Dây Chống Ồn Chủ Động Sony WH-1000XM5",
            brand = "Sony",
            category = "Phụ kiện",
            price = 6490000.0,
            imageUrls = listOf("https://via.placeholder.com/150"),
            stock = 5,
            soldCount = 2
        )
    )

    SmartPickTheme {
        ReviewHubContent(
            productsToReview = mockProducts,
            reviewedProducts = emptyList(),
            isLoading = false,
            paddingValues = PaddingValues(),
            onNavigateToWriteReview = {}
        )
    }
}