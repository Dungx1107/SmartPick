package com.example.smartpick.features.checkout.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Order
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.cart.data.CartRepository
import com.example.smartpick.features.checkout.data.OrderRepository
import com.example.smartpick.features.home.data.HomeRepository
import com.example.smartpick.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository,
    private val homeRepository: HomeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val targetProductId: String? = savedStateHandle[Routes.Checkout.ARG_PRODUCT_ID]
    private val targetQuantity: Int = savedStateHandle[Routes.Checkout.ARG_QUANTITY] ?: 1

    var phone = MutableStateFlow("")
    var address = MutableStateFlow("")
    var paymentMethod = MutableStateFlow("COD")

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // THÊM: StateFlow để quản lý danh sách lịch sử đơn hàng
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private var isHistoryLoading = false

    init {
        loadCheckoutData()
        loadUserDefaultInfo()
        loadOrderHistory() // THÊM: Nạp lịch sử đơn hàng ngay khi khởi tạo
    }

    /**
     * Tự động lấy thông tin từ Profile và Đơn hàng cũ để điền sẵn cho khách
     */
    private fun loadUserDefaultInfo() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                if (!user.phoneNumber.isNullOrBlank()) {
                    phone.value = user.phoneNumber
                }

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

    /**
     * Hàm phân luồng: Phân biệt giữa Mua Ngay và Thanh Toán Toàn Bộ Giỏ Hàng
     */
    private fun loadCheckoutData() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser() ?: return@launch

                if (targetProductId != null) {
                    // --- LUỒNG 1: MUA NGAY TỪ TRANG CHI TIẾT SẢN PHẨM ---
                    val productData = homeRepository.getProductById(targetProductId)
                    if (productData != null) {
                        val temporaryCartItem = CartItem(
                            id = null,
                            userId = user.id,
                            productId = targetProductId,
                            quantity = targetQuantity,
                            product = productData
                        )
                        _cartItems.value = listOf(temporaryCartItem)
                    } else {
                        _cartItems.value = emptyList()
                    }
                } else {
                    // --- LUỒNG 2: MUA TỪ GIỎ HÀNG (MẶC ĐỊNH CŨ) ---
                    _cartItems.value = cartRepository.fetchCartItems(user.id)
                }
            } catch (e: Exception) {
                _cartItems.value = emptyList()
            }
        }
    }

    /**
     * THÊM: Hàm gọi xuống Repository để lấy lịch sử đơn hàng đã mua
     */
    fun loadOrderHistory() {
        // Nếu đang chạy tiến trình lấy dữ liệu cũ, từ chối các lệnh gọi trùng lặp tiếp theo
        if (isHistoryLoading) {
            Log.d("CheckoutViewModel", "Tiến trình lấy lịch sử đơn hàng đang chạy. Bỏ qua yêu cầu trùng lặp.")
            return
        }

        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                try {
                    isHistoryLoading = true // Bật cờ khóa luồng
                    _orders.value = orderRepository.getOrderHistory(user.id)
                } catch (e: Exception) {
                    Log.e("CheckoutViewModel", "Lỗi nạp dữ liệu lịch sử đơn hàng: ${e.localizedMessage}")
                } finally {
                    isHistoryLoading = false // Mở khóa luồng sau khi hoàn tất (kể cả gặp lỗi)
                }
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
                _cartItems.value = emptyList()
                loadOrderHistory()
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Lỗi hệ thống khi chốt đơn")
            }
        }
    }
}