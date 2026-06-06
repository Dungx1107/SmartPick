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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Định nghĩa trạng thái UI cho màn hình Chi tiết sản phẩm
 */
data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val error: String? = null
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Khởi tạo StateFlow để ProductDetailScreen có thể collect dữ liệu ổn định
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val _postId = MutableStateFlow<String?>(null)
    val postId: StateFlow<String?> = _postId.asStateFlow()

    /**
     * Hàm nạp thông tin chi tiết sản phẩm từ Supabase
     */
    fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val productData = repository.getProductById(productId)
                if (productData != null) {
                    _uiState.update { it.copy(isLoading = false, product = productData) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Không tìm thấy sản phẩm") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Lỗi tải dữ liệu") }
            }
        }
    }

    fun fetchPostId(productId: String) {
        viewModelScope.launch {
            _postId.value = repository.getPostIdByProductId(productId)
        }
    }

    fun isProductAvailable(product: Product): Boolean {
        return product.stock > 0
    }

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

            // Gọi CartRepository hoặc HomeRepository tùy thuộc vào việc đồng bộ token
            val result = cartRepository.addToCart(user.id, product.id!!)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Lỗi hệ thống khi thêm vào giỏ hàng.")
            }
        }
    }
}