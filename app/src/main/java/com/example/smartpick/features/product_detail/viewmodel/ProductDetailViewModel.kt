// FILE: com/example/smartpick/features/product_detail/viewmodel/ProductDetailViewModel.kt
package com.example.smartpick.features.product_detail.viewmodel

import android.util.Log
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

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

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
                    fetchPostId(productId)
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
            Log.d("SMARTPICK_DEBUG", "--- VIEWMODEL: Bắt đầu gọi fetchPostId cho sản phẩm: $productId ---")
            val fetchedId = repository.getPostIdByProductId(productId)

            _postId.value = fetchedId
            Log.d("SMARTPICK_DEBUG", "--- VIEWMODEL: Trạng thái lưu trữ của _postId.value hiện tại đã đổi thành: ${_postId.value} ---")
        }
    }

    fun isProductAvailable(product: Product): Boolean {
        return product.stock > 0
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

                if (product.id == null) {
                    onError("Không tìm thấy ID sản phẩm")
                    return@launch
                }

                val currentPostId = _postId.value

                Log.d("SMARTPICK_DEBUG", "--- VIEWMODEL: Chuẩn bị kích hoạt toán tử addToCart từ nút bấm UI ---")
                Log.d("SMARTPICK_DEBUG", "--- VIEWMODEL: Tham số gửi đi -> ProductId: ${product.id} | PostId lấy từ StateFlow: $currentPostId")

                val result = cartRepository.addToCart(
                    userId = user.id,
                    productId = product.id,
                    postId = currentPostId
                )

                if (result.isFailure) {
                    val exception = result.exceptionOrNull()
                    onError(exception?.message ?: "Lỗi hệ thống database")
                } else {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("SMARTPICK_DEBUG", "Ngoại lệ xảy ra khi kết nối mạng/DB: ${e.message}", e)
                e.printStackTrace()
                onError(e.localizedMessage ?: "Ngoại lệ hệ thống")
            }
        }
    }
}