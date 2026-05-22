package com.example.smartpick.features.home.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReviewResponse
import com.example.smartpick.features.home.ui.components.*
import com.example.smartpick.features.home.viewmodel.HomeUiState
import com.example.smartpick.features.home.viewmodel.HomeViewModel
import com.example.smartpick.navigation.Routes
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val productReviews by viewModel.productReviews.collectAsState()
    val canReview by viewModel.canReview.collectAsState()

    var showCart by rememberSaveable { mutableStateOf(false) }
    var selectedProduct by rememberSaveable { mutableStateOf<Product?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val freshSelectedProduct = remember(selectedProduct, uiState) {
        if (uiState is HomeUiState.Success && selectedProduct != null) {
            (uiState as HomeUiState.Success).products.find { it.id == selectedProduct?.id }
                ?: selectedProduct
        } else selectedProduct
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val speechLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val text =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                        ?: ""
                searchQuery = text
                viewModel.searchProducts(text)
            }
        }

    LaunchedEffect(selectedProduct) {
        selectedProduct?.id?.let { viewModel.fetchReviewsAndCheckEligibility(it) }
    }

    HomeContent(
        paddingValues = paddingValues,
        uiState = uiState,
        cartItems = cartItems,
        searchQuery = searchQuery,
        selectedProduct = freshSelectedProduct,
        productReviews = productReviews,
        canReview = canReview,
        showCart = showCart,
        onSearchQueryChange = { searchQuery = it; viewModel.searchProducts(it) },
        onMicClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            }
            try {
                speechLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Thiết bị không hỗ trợ", Toast.LENGTH_SHORT).show()
            }
        },
        onCartClick = { showCart = true },
        onProductClick = { selectedProduct = it },
        onAddToCart = {
            viewModel.addToCart(
                it,
                { Toast.makeText(context, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show() },
                { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() })
        },
        onDismissSheet = { selectedProduct = null },
        onViewFeed = {
            scope.launch {
                val postId = viewModel.getPostId(freshSelectedProduct?.id ?: "")
                if (postId != null) {
                    selectedProduct = null; navController.navigate(
                        Routes.PostDetail.createRoute(postId)
                    )
                } else Toast.makeText(context, "Chưa có bài đăng thảo luận!", Toast.LENGTH_SHORT).show()
            }
        },
        onBuyNow = {
            viewModel.addToCart(
                freshSelectedProduct!!,
                { selectedProduct = null; navController.navigate(Routes.Checkout.route) },
                { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })
        },
        onSubmitReview = { r, c ->
            viewModel.submitProductReview(
                freshSelectedProduct!!.id!!,
                r,
                c,
                { Toast.makeText(context, "Đã đánh giá!", Toast.LENGTH_SHORT).show() },
                { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })
        },
        onDismissCart = { showCart = false },
        onIncrease = { item -> viewModel.increaseQuantity(item) },
        onDecrease = { item -> viewModel.decreaseQuantity(item) },
        onCheckout = { showCart = false; navController.navigate(Routes.Checkout.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    uiState: HomeUiState,
    cartItems: List<CartItem>,
    searchQuery: String,
    selectedProduct: Product?,
    productReviews: List<ReviewResponse>,
    canReview: Boolean,
    showCart: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onMicClick: () -> Unit, onCartClick: () -> Unit, onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit, onDismissSheet: () -> Unit, onViewFeed: () -> Unit,
    onBuyNow: () -> Unit, onSubmitReview: (Int, String) -> Unit, onDismissCart: () -> Unit,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onCheckout: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // Giữ lệnh này để khử khoảng trắng đáy
        containerColor = MaterialTheme.colorScheme.background, // Khôi phục màu nền gốc
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCartClick,
                containerColor = SmartPickColor, // Khôi phục nút bấm thành màu SmartPick
                contentColor = White
            ) {
                val total = cartItems.sumOf { it.quantity }
                if (total > 0) BadgedBox(badge = { Badge { Text(total.toString()) } }) {
                    Icon(Icons.Default.ShoppingCart, null)
                }
                else Icon(Icons.Default.ShoppingCart, null)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onMicClick = onMicClick,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )

                    is HomeUiState.Success -> LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.products) { product ->
                            ProductGridCard(
                                product = product,
                                onProductClick = { onProductClick(product) },
                                onAddToCart = { onAddToCart(product) }
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    if (selectedProduct != null) {
        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            ProductDetailContent(
                selectedProduct,
                productReviews,
                canReview,
                onViewFeed,
                { onAddToCart(selectedProduct) },
                onBuyNow,
                onSubmitReview
            )
        }
    }

    if (showCart) {
        CartBottomSheet(
            cartItems = cartItems,
            onDismiss = onDismissCart,
            onIncrease = { cartId ->
                val item = cartItems.find { it.id == cartId }
                if (item != null) onIncrease(item)
            },
            onDecrease = { cartId ->
                val item = cartItems.find { it.id == cartId }
                if (item != null) onDecrease(item)
            },
            onCheckout = onCheckout
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    HomeContent(
        paddingValues = PaddingValues(0.dp),
        uiState = HomeUiState.Loading,
        cartItems = emptyList(),
        searchQuery = "",
        selectedProduct = null,
        productReviews = emptyList(),
        canReview = false,
        showCart = false,
        onSearchQueryChange = {},
        onMicClick = {},
        onCartClick = {},
        onProductClick = {},
        onAddToCart = {},
        onDismissSheet = {},
        onViewFeed = {},
        onBuyNow = {},
        onSubmitReview = { _, _ -> },
        onDismissCart = {},
        onIncrease = {},
        onDecrease = {},
        onCheckout = {}
    )
}