package com.example.smartpick.features.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.OrderResponse
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.home.data.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Đã thêm: State lưu trữ Lịch sử mua hàng
    private val _orders = MutableStateFlow<List<OrderResponse>>(emptyList())
    val orders: StateFlow<List<OrderResponse>> = _orders.asStateFlow()

    private var allProductsList: List<Product> = emptyList()

    init {
        fetchProducts()
        fetchCartItems()
        fetchOrderHistory() // Khởi tạo lấy lịch sử
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val products = repository.getAllProducts()
                allProductsList = products
                _uiState.value = HomeUiState.Success(products)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Lỗi tải dữ liệu")
            }
        }
    }

    fun searchProducts(query: String) {
        val filtered = if (query.isBlank()) allProductsList else allProductsList.filter { it.name.contains(query, ignoreCase = true) }
        _uiState.value = HomeUiState.Success(filtered)
    }

    fun fetchCartItems() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                user?.id?.let { uid -> _cartItems.value = repository.getCartItems(uid) }
            } catch (e: Exception) {
                Log.e("SupabaseCart", "Lỗi fetchCartItems: ${e.message}", e)
            }
        }
    }

    // Đã thêm: Hàm lấy Lịch sử đơn hàng
    fun fetchOrderHistory() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                user?.id?.let { uid ->
                    _orders.value = repository.getOrders(uid)
                }
            } catch (e: Exception) {
                Log.e("SupabaseOrders", "Lỗi fetchOrderHistory: ${e.message}", e)
            }
        }
    }

    fun addToCart(product: Product, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    onError("Bạn cần đăng nhập để thực hiện tính năng này")
                    return@launch
                }
                if (product.id != null) {
                    val result = repository.addToCart(user.id, product.id)
                    if (result.isSuccess) {
                        fetchCartItems()
                        onSuccess()
                    } else {
                        onError(result.exceptionOrNull()?.message ?: "Lỗi không xác định")
                    }
                }
            } catch (e: Exception) {
                onError("Đã xảy ra lỗi hệ thống: ${e.message}")
            }
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            if (cartItem.id != null) {
                val result = repository.removeFromCart(cartItem.id)
                if (result.isSuccess) fetchCartItems()
            }
        }
    }

    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            val result = repository.updateCartItemQuantity(item.id!!, item.quantity + 1)
            if (result.isSuccess) fetchCartItems()
        }
    }

    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            if (item.quantity > 1) {
                val result = repository.updateCartItemQuantity(item.id!!, item.quantity - 1)
                if (result.isSuccess) fetchCartItems()
            } else {
                removeFromCart(item)
            }
        }
    }

    fun processCheckout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    onError("Vui lòng đăng nhập để thực hiện thanh toán")
                    return@launch
                }

                val currentCart = _cartItems.value
                val result = repository.checkout(user.id, currentCart)

                if (result.isSuccess) {
                    fetchCartItems() // Reset giỏ hàng về rỗng
                    fetchOrderHistory() // Ngay lập tức cập nhật lại tab Lịch sử
                    onSuccess()
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Lỗi thanh toán từ Database"
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                onError("Đã xảy ra lỗi hệ thống khi chốt đơn")
            }
        }
    }

    suspend fun getPostId(productId: String): String? {
        return repository.getPostIdByProductId(productId)
    }
}