package com.example.smartpick.features.checkout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.checkout.data.OrderRepository
import com.example.smartpick.features.home.data.HomeRepository // Tạm thời dùng để lấy thông tin giỏ hàng hiện tại
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

    // Quản lý trạng thái Form nhập liệu
    var phone = MutableStateFlow("")
    var address = MutableStateFlow("")
    var paymentMethod = MutableStateFlow("COD")

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    init {
        loadCurrentCart()
    }

    /**
     * Tải danh sách item cần thanh toán trong giỏ hàng hiện tại
     */
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

    /**
     * Xử lý gửi đơn đặt hàng
     */
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