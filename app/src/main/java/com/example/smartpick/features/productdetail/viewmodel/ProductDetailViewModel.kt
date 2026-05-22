package com.example.smartpick.features.productdetail.viewmodel

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

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: HomeRepository // Sau này sẽ đổi thành ProductRepository
) : ViewModel() {

    private val _postId = MutableStateFlow<String?>(null)
    val postId: StateFlow<String?> = _postId.asStateFlow()

    /**
     * Lấy ID bài đăng thảo luận liên quan đến sản phẩm
     */
    fun fetchPostId(productId: String) {
        viewModelScope.launch {
            _postId.value = repository.getPostIdByProductId(productId)
        }
    }

    /**
     * Logic kiểm tra xem sản phẩm còn hàng hay không trước khi cho phép mua
     */
    fun isProductAvailable(product: Product): Boolean {
        return product.stock > 0
    }
}