package com.example.smartpick.features.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.cart.data.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Lắng nghe dữ liệu giỏ hàng biến động từ Repository phát ra
    val cartItems: StateFlow<List<CartItem>> = cartRepository.cartItemsFlow

    // Tự động tính tổng số lượng sản phẩm có trong giỏ hàng để hiển thị lên Badge biểu tượng
    val totalCartCount: StateFlow<Int> = cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        refreshCart()
    }

    fun refreshCart() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            user?.id?.let { uid -> cartRepository.fetchCartItems(uid) }
        }
    }

    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null && item.id != null) {
                cartRepository.updateCartItemQuantity(user.id, item.id!!, item.quantity + 1)
            }
        }
    }

    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null && item.id != null) {
                cartRepository.updateCartItemQuantity(user.id, item.id!!, item.quantity - 1)
            }
        }
    }
}