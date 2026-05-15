package com.example.smartpick.features.home.ui

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartpick.R
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.features.home.ui.components.CartBottomSheet
import com.example.smartpick.features.home.ui.components.ProductDetailContent
import com.example.smartpick.features.home.ui.components.ProductGridCard
import com.example.smartpick.features.home.ui.components.SearchBar
import com.example.smartpick.features.home.viewmodel.HomeUiState
import com.example.smartpick.features.home.viewmodel.HomeViewModel
import com.example.smartpick.navigation.Routes
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val scope = rememberCoroutineScope()

    HomeContent(
        uiState = uiState,
        cartItems = cartItems,
        paddingValues = paddingValues,
        onSearch = { viewModel.searchProducts(it) },
        onAddToCart = { product, onSuccess, onError ->
            viewModel.addToCart(product, onSuccess, onError)
        },
        onViewFeed = { productId, onPostFound ->
            scope.launch {
                val postId = viewModel.getPostId(productId)
                onPostFound(postId)
            }
        },
        onIncrease = { viewModel.increaseQuantity(it) },
        onDecrease = { viewModel.decreaseQuantity(it) },
        onNavigateToPost = { postId ->
            navController.navigate(Routes.PostDetail.createRoute(postId))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    cartItems: List<CartItem>,
    paddingValues: PaddingValues,
    onSearch: (String) -> Unit,
    onAddToCart: (Product, () -> Unit, (String) -> Unit) -> Unit,
    onViewFeed: (String, (String?) -> Unit) -> Unit,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onNavigateToPost: (String) -> Unit
) {
    var showCart by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val recognizedText =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            searchQuery = recognizedText
            onSearch(recognizedText)
        }
    }

    fun startSpeech() {
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
            e.printStackTrace()
        }
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCart = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                val total = cartItems.sumOf { it.quantity }
                if (total > 0) {
                    BadgedBox(
                        badge = { 
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) { Text(total.toString()) } 
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, stringResource(R.string.GioHang))
                    }
                } else {
                    Icon(Icons.Default.ShoppingCart, stringResource(R.string.GioHang))
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    onSearch(it)
                },
                onMicClick = { startSpeech() }
            )

            Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()) {
                when (uiState) {
                    is HomeUiState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )

                    is HomeUiState.Error -> Text(
                        "Lỗi: ${uiState.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    is HomeUiState.Success -> {
                        if (uiState.products.isEmpty()) {
                            Text(
                                stringResource(R.string.KoCoSanPham), 
                                color = TextMuted,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(uiState.products) { product ->
                                    ProductGridCard(
                                        product = product,
                                        onProductClick = { selectedProduct = product },
                                        onAddToCart = {
                                            onAddToCart(
                                                it,
                                                {
                                                    Toast.makeText(
                                                        context,
                                                        "Đã thêm!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                { msg ->
                                                    Toast.makeText(
                                                        context,
                                                        msg,
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedProduct != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedProduct = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            ProductDetailContent(
                product = selectedProduct!!,
                onViewFeed = {
                    onViewFeed(selectedProduct!!.id ?: "") { postId ->
                        if (postId != null) {
                            selectedProduct = null
                            onNavigateToPost(postId)
                        } else {
                            Toast.makeText(context, "Chưa có bài đăng", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onAddToCart = {
                    onAddToCart(selectedProduct!!, {}, {})
                },
                onBuyNow = {
                    selectedProduct = null
                    showCart = true
                }
            )
        }
    }

    if (showCart) {
        CartBottomSheet(
            cartItems = cartItems,
            onIncrease = onIncrease,
            onDecrease = onDecrease,
            onDismiss = { showCart = false },
            onCheckout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    SmartPickTheme {
        val mockProducts = listOf(
            Product(id = "1", name = "Sản phẩm mẫu 1", price = 150000.0, ownerId = "u1"),
            Product(id = "2", name = "Sản phẩm mẫu 2", price = 250000.0, ownerId = "u1")
        )

        val mockCart = listOf(
            CartItem(id = "c1", userId = "u1", productId = "1", quantity = 2)
        )

        HomeContent(
            uiState = HomeUiState.Success(mockProducts),
            cartItems = mockCart,
            paddingValues = PaddingValues(0.dp),
            onSearch = {},
            onAddToCart = { _, _, _ -> },
            onViewFeed = { _, _ -> },
            onIncrease = {},
            onDecrease = {},
            onNavigateToPost = {}
        )
    }
}