package com.example.smartpick.features.cart.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.cart.data.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val cartItems: StateFlow<List<CartItem>> = cartRepository.cartItemsFlow

    val totalCartCount: StateFlow<Int> = cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _cartError = MutableStateFlow<String?>(null)
    val cartError: StateFlow<String?> = _cartError.asStateFlow()

    fun clearError() {
        _cartError.value = null
    }

    init {
        refreshCart()
    }

    fun refreshCart() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                user?.id?.let { uid -> cartRepository.fetchCartItems(uid) }
            } catch (e: Exception) {
                Log.e("CartViewModel", "refreshCart error: ${e.message}", e)
            }
        }
    }

    fun increaseQuantity(item: CartItem) {
        val stock = item.product?.stock ?: 0
        if (item.quantity >= stock) {
            _cartError.value = "Chỉ còn $stock sản phẩm trong kho!"
            return
        }

        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                if (user != null && item.id != null) {
                    cartRepository.updateCartItemQuantity(user.id, item.id!!, item.quantity + 1)
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "increaseQuantity error: ${e.message}", e)
            }
        }
    }

    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                if (user != null && item.id != null) {
                    cartRepository.updateCartItemQuantity(user.id, item.id!!, item.quantity - 1)
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "decreaseQuantity error: ${e.message}", e)
            }
        }
    }
}