// File: app/src/main/java/com/example/smartpick/features/home/ui/HomeScreen.kt
package com.example.smartpick.features.home.ui

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartpick.R
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.home.ui.components.CartBottomSheet
import com.example.smartpick.features.home.ui.components.ProductDetailContent
import com.example.smartpick.features.home.ui.components.ProductGridCard
import com.example.smartpick.features.home.ui.components.SearchBar
import com.example.smartpick.features.home.viewmodel.HomeUiState
import com.example.smartpick.features.home.viewmodel.HomeViewModel
import com.example.smartpick.navigation.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    var showCart by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = data?.get(0) ?: ""
            searchQuery = recognizedText
            viewModel.searchProducts(recognizedText)
        }
    }

    fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.NoiTenSanPhamBanMuonTim))
        }
        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Thiết bị không hỗ trợ nhận diện giọng nói", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        floatingActionButton = {
            FloatingActionButton(onClick = { showCart = true }) {
                val totalQuantity = cartItems.sumOf { it.quantity }
                if (totalQuantity > 0) {
                    BadgedBox(badge = { Badge { Text(totalQuantity.toString()) } }) {
                        Icon(Icons.Default.ShoppingCart, stringResource(R.string.cart))
                    }
                } else {
                    Icon(Icons.Default.ShoppingCart, stringResource(R.string.cart))
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    viewModel.searchProducts(it)
                },
                onMicClick = { startSpeechToText() }
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is HomeUiState.Error -> Text("Lỗi: ${state.message}", modifier = Modifier.align(Alignment.Center))
                    is HomeUiState.Success -> {
                        if (state.products.isEmpty()) {
                            Text(stringResource(R.string.KoTimThaySanPhamNao), modifier = Modifier.align(Alignment.Center))
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.products) { product ->
                                    ProductGridCard(
                                        product = product,
                                        onProductClick = { selectedProduct = product },
                                        onAddToCart = {
                                            viewModel.addToCart(
                                                product = it,
                                                onSuccess = { Toast.makeText(context, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show() },
                                                onError = { errorMsg -> Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show() }
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
            modifier = Modifier.fillMaxHeight(0.9f),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            ProductDetailContent(
                product = selectedProduct!!,
                onViewFeed = {
                    scope.launch {
                        val postId = viewModel.getPostId(selectedProduct!!.id ?: "")
                        if (postId != null) {
                            selectedProduct = null
                            navController.navigate(Routes.PostDetail.createRoute(postId))
                        } else {
                            Toast.makeText(context, "Sản phẩm này chưa có bài đăng!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onAddToCart = {
                    viewModel.addToCart(
                        product = selectedProduct!!,
                        onSuccess = { Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show() },
                        onError = { errorMsg -> Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show() }
                    )
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
            onDismiss = { showCart = false },
            onIncrease = { viewModel.increaseQuantity(it) },
            onDecrease = { viewModel.decreaseQuantity(it) },
            onCheckout = {
                showCart = false
                navController.navigate(Routes.Checkout.route)
            }
        )
    } // FIX: Đã thêm dấu ngoặc nhọn đóng của hàm HomeScreen
}