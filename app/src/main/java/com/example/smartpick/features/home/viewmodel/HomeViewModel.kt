// File: app/src/main/java/com/example/smartpick/features/home/viewmodel/HomeViewModel.kt
package com.example.smartpick.features.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.auth.data.AuthRepository
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
    private val repository: HomeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private var allProductsList: List<Product> = emptyList()

    init {
        fetchProducts()
        fetchCartItems()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val products = repository.getAllProducts()
                allProductsList = products
                _uiState.value = HomeUiState.Success(products)
            } catch (e: Exception) {
                Log.e("SupabaseCart", "Lỗi fetchProducts: ${e.message}", e)
                _uiState.value = HomeUiState.Error(e.message ?: "Lỗi tải dữ liệu")
            }
        }
    }

    fun searchProducts(query: String) {
        val filtered = if (query.isBlank()) {
            allProductsList
        } else {
            allProductsList.filter { it.name.contains(query, ignoreCase = true) }
        }
        _uiState.value = HomeUiState.Success(filtered)
    }

    fun fetchCartItems() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                user?.id?.let { uid ->
                    _cartItems.value = repository.getCartItems(uid)
                }
            } catch (e: Exception) {
                Log.e("SupabaseCart", "Lỗi fetchCartItems: ${e.message}", e)
            }
        }
    }

    /**
     * Thêm sản phẩm vào giỏ hàng với callback thông báo kết quả thực tế từ API
     */
    fun addToCart(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    onError("Bạn cần đăng nhập để thực hiện tính năng này")
                    return@launch
                }
                
                val uid = user.id
                if (product.id != null) {
                    val result = repository.addToCart(uid, product.id)
                    if (result.isSuccess) {
                        fetchCartItems() // Cập nhật lại list sau khi thêm thành công
                        onSuccess()
                    } else {
                        val errorMsg = result.exceptionOrNull()?.message ?: "Lỗi không xác định"
                        Log.e("SupabaseCart", "Lỗi addToCart API: $errorMsg")
                        onError(errorMsg)
                    }
                } else {
                    onError("ID sản phẩm không hợp lệ")
                }
            } catch (e: Exception) {
                Log.e("SupabaseCart", "Crash addToCart: ${e.message}", e)
                onError("Đã xảy ra lỗi hệ thống: ${e.message}")
            }
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                if (cartItem.id != null) {
                    val result = repository.removeFromCart(cartItem.id)
                    if (result.isSuccess) fetchCartItems()
                }
            } catch (e: Exception) {
                Log.e("SupabaseCart", "Lỗi removeFromCart: ${e.message}", e)
            }
        }
    }

    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            try {
                val result = repository.updateCartItemQuantity(item.id!!, item.quantity + 1)
                if (result.isSuccess) fetchCartItems()
            } catch (e: Exception) {
                Log.e("SupabaseCart", "Lỗi increaseQuantity: ${e.message}", e)
            }
        }
    }

    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            try {
                if (item.quantity > 1) {
                    val result = repository.updateCartItemQuantity(item.id!!, item.quantity - 1)
                    if (result.isSuccess) fetchCartItems()
                } else {
                    removeFromCart(item)
                }
            } catch (e: Exception) {
                Log.e("SupabaseCart", "Lỗi decreaseQuantity: ${e.message}", e)
            }
        }
    }

    suspend fun getPostId(productId: String): String? {
        return repository.getPostIdByProductId(productId)
    }
}
