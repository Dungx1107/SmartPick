package com.example.smartpick.features.checkout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.checkout.data.OrderRepository
import com.example.smartpick.features.home.data.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val homeRepository: HomeRepository, // Sẽ thay bằng CartRepository ở bước sau
    private val authRepository: AuthRepository
) : ViewModel() {

    var phone = MutableStateFlow("")
    var address = MutableStateFlow("")
    var paymentMethod = MutableStateFlow("COD")

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    init {
        loadCurrentCart()
        loadUserDefaultInfo() // FIX: Tự động gọi hàm gợi ý thông tin khi mở màn hình
    }

    /**
     * Tự động lấy thông tin từ Profile và Đơn hàng cũ để điền sẵn cho khách
     */
    private fun loadUserDefaultInfo() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                // 1. Cố gắng lấy SĐT từ Profile người dùng trước
                if (!user.phoneNumber.isNullOrBlank()) {
                    phone.value = user.phoneNumber
                }

                // 2. Tìm đơn hàng gần nhất. Nếu có, ưu tiên lấy thông tin từ đơn hàng này (vì nó là địa chỉ giao thực tế)
                val lastOrder = orderRepository.getLastOrderInfo(user.id)
                if (lastOrder != null) {
                    if (!lastOrder.phoneNumber.isNullOrBlank()) {
                        phone.value = lastOrder.phoneNumber
                    }
                    if (!lastOrder.shippingAddress.isNullOrBlank()) {
                        address.value = lastOrder.shippingAddress
                    }
                }
            }
        }
    }

    private fun loadCurrentCart() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                user?.id?.let { uid ->
                    _cartItems.value = homeRepository.getCartItems(uid)
                }
            } catch (e: Exception) {
                _cartItems.value = emptyList()
            }
        }
    }

    fun updatePhone(value: String) { phone.value = value }
    fun updateAddress(value: String) { address.value = value }
    fun updatePaymentMethod(value: String) { paymentMethod.value = value }

    fun placeOrder(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentPhone = phone.value
        val currentAddress = address.value
        val currentMethod = paymentMethod.value
        val currentItems = _cartItems.value

        if (currentPhone.isBlank() || currentAddress.isBlank()) {
            onError("Vui lòng nhập đầy đủ thông tin nhận hàng")
            return
        }

        if (currentItems.isEmpty()) {
            onError("Giỏ hàng của bạn đang trống")
            return
        }

        // Chốt chặn tồn kho
        val outOfStockItem = currentItems.find { item ->
            val stock = item.product?.stock ?: 0
            item.quantity > stock
        }

        if (outOfStockItem != null) {
            val stockInfo = outOfStockItem.product?.stock ?: 0
            if (stockInfo <= 0) {
                onError("Sản phẩm '${outOfStockItem.product?.name}' hiện đã hết hàng!")
            } else {
                onError("Sản phẩm '${outOfStockItem.product?.name}' chỉ còn $stockInfo chiếc trong kho!")
            }
            return
        }

        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user == null) {
                onError("Vui lòng đăng nhập lại để thanh toán")
                return@launch
            }

            _isProcessing.value = true
            val result = orderRepository.checkout(
                userId = user.id,
                cartItems = currentItems,
                address = currentAddress,
                phone = currentPhone,
                paymentMethod = currentMethod
            )
            _isProcessing.value = false

            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Lỗi hệ thống khi chốt đơn")
            }
        }
    }
}