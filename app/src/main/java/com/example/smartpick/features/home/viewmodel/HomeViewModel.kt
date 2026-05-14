// File: app/src/main/java/com/example/smartpick/features/home/viewmodel/HomeViewModel.kt
package com.example.smartpick.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.home.data.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val products: List<Product>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _cartItems = MutableStateFlow<List<Product>>(emptyList())
    val cartItems: StateFlow<List<Product>> = _cartItems.asStateFlow()

    // Lưu cache để tìm kiếm offline
    private var allProductsList: List<Product> = emptyList()

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val products = repository.getAllProducts()
                allProductsList = products // Gán vào cache
                _uiState.value = HomeUiState.Success(products)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Lỗi tải dữ liệu")
            }
        }
    }

    // Logic tìm kiếm sản phẩm
    fun searchProducts(query: String) {
        val filtered = if (query.isBlank()) {
            allProductsList
        } else {
            allProductsList.filter { it.name.contains(query, ignoreCase = true) }
        }
        _uiState.value = HomeUiState.Success(filtered)
    }

    fun addToCart(product: Product) {
        _cartItems.value = _cartItems.value + product
    }

    fun removeFromCart(product: Product) {
        val currentList = _cartItems.value.toMutableList()
        currentList.remove(product)
        _cartItems.value = currentList
    }

    suspend fun getPostId(productId: String): String? {
        return repository.getPostIdByProductId(productId)
    }
}