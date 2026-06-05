package com.example.smartpick.features.product_detail.viewmodel

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
class ProductDetailViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val cartRepository: CartRepository, // Bổ sung
    private val authRepository: AuthRepository  // Bổ sung
) : ViewModel() {

    private val _postId = MutableStateFlow<String?>(null)
    val postId: StateFlow<String?> = _postId.asStateFlow()

    fun fetchPostId(productId: String) {
        viewModelScope.launch {
            _postId.value = repository.getPostIdByProductId(productId)
        }
    }

    fun isProductAvailable(product: Product): Boolean {
        return product.stock > 0
    }

    // ==========================================
    // FIX: HÀM THÊM VÀO GIỎ HÀNG (CÓ CHECK KHO)
    // ==========================================
    fun addToCart(product: Product, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!isProductAvailable(product)) {
            onError("Sản phẩm này hiện đã hết hàng!")
            return
        }

        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user == null) {
                onError("Vui lòng đăng nhập để thêm vào giỏ hàng.")
                return@launch
            }

            // Gọi CartRepository để đẩy lên database
            val result = cartRepository.addToCart(user.id, product.id!!)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Lỗi hệ thống khi thêm vào giỏ hàng.")
            }
        }
    }
}