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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Nạp luồng dữ liệu từ biến Flow chuẩn trong Repository của bạn
    val cartItems: StateFlow<List<CartItem>> = cartRepository.cartItemsFlow

    // Trạng thái các ID mục giỏ hàng đang được tích chọn (Checkbox) để chuẩn bị thanh toán
    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds.asStateFlow()

    // Tổng số lượng item trong giỏ hàng hiển thị lên UI
    val totalCartCount: StateFlow<Int> = cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Trạng thái thông báo lỗi hệ thống giỏ hàng
    private val _cartError = MutableStateFlow<String?>(null)
    val cartError: StateFlow<String?> = _cartError.asStateFlow()

    fun clearError() {
        _cartError.value = null
    }

    init {
        refreshCart()
    }

    /**
     * Đồng bộ nạp lại danh sách giỏ hàng mới nhất từ database
     */
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

    /**
     * Bật hoặc tắt trạng thái tích chọn Checkbox của một item
     */
    fun toggleSelection(cartItemId: String) {
        _selectedIds.update { currentSet ->
            if (currentSet.contains(cartItemId)) {
                currentSet - cartItemId
            } else {
                currentSet + cartItemId
            }
        }
    }

    /**
     * Chọn tất cả hoặc Hủy chọn tất cả các mặt hàng đang hiển thị trong giỏ
     */
    fun selectAll(isSelectAll: Boolean) {
        if (isSelectAll) {
            val allIds = cartItems.value.mapNotNull { it.id }.toSet()
            _selectedIds.value = allIds
        } else {
            _selectedIds.value = emptySet()
        }
    }

    /**
     * Tăng số lượng mặt hàng trong giỏ, có kiểm tra giới hạn tồn kho (stock)
     */
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
                    // Gọi hàm với đúng 3 tham số như cấu trúc file CartRepository hiện tại của bạn
                    cartRepository.updateCartItemQuantity(user.id, item.id!!, item.quantity + 1)
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "increaseQuantity error: ${e.message}", e)
            }
        }
    }

    /**
     * Giảm số lượng mặt hàng trong giỏ, tự động xóa nếu số lượng về 0
     */
    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                if (user != null && item.id != null) {
                    val newQuantity = item.quantity - 1
                    if (newQuantity <= 0) {
                        // Nếu số lượng về 0, xóa ID này khỏi danh sách đang chọn trước khi thực hiện xóa vật lý
                        _selectedIds.update { it - item.id!! }
                    }
                    // Hàm updateCartItemQuantity của bạn đã tự bọc logic delete nếu số lượng <= 0
                    cartRepository.updateCartItemQuantity(user.id, item.id!!, newQuantity)
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "decreaseQuantity error: ${e.message}", e)
            }
        }
    }

    /**
     * Hàm xóa trực tiếp các mặt hàng khi người dùng nhấn nút Xóa ở chế độ "Chỉnh sửa"
     */
    fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    _selectedIds.update { it - cartItemId }
                    // Ép số lượng về 0 để kích hoạt nhánh xóa vật lý trong Repository
                    cartRepository.updateCartItemQuantity(user.id, cartItemId, 0)
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "removeItem error: ${e.message}", e)
            }
        }
    }
}