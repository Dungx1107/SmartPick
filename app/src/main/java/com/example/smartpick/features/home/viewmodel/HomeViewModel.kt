package com.example.smartpick.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.cart.data.CartRepository
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
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allProductsList: List<Product> = emptyList()

    init {
        fetchProducts()
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
        val filtered = if (query.isBlank()) {
            allProductsList
        } else {
            allProductsList.filter { it.name.contains(query, ignoreCase = true) }
        }
        _uiState.value = HomeUiState.Success(filtered)
    }

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
                if (product.id != null) {
                    val result = cartRepository.addToCart(
                        userId = user.id,
                        productId = product.id,
                        postId = product.postId
                    )
                    if (result.isSuccess) {
                        onSuccess()
                    } else {
                        onError(result.exceptionOrNull()?.message ?: "Lỗi thêm vào giỏ hàng")
                    }
                }
            } catch (e: Exception) {
                onError("Đã xảy ra lỗi hệ thống: ${e.message}")
            }
        }
    }
}