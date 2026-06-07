package com.example.smartpick.features.seller.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.seller.data.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SellerStats(
    val totalRevenue: Double = 0.0,
    val totalOrders: Int = 0,
    val totalProductsSold: Int = 0
)

@HiltViewModel
class SellerViewModel @Inject constructor(
    private val sellerRepository: SellerRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _myProducts = MutableStateFlow<List<Product>>(emptyList())
    val myProducts: StateFlow<List<Product>> = _myProducts.asStateFlow()

    private val _soldOrders = MutableStateFlow<List<SellerRepository.SoldOrderItemDto>>(emptyList())
    val soldOrders: StateFlow<List<SellerRepository.SoldOrderItemDto>> = _soldOrders.asStateFlow()

    private val _sellerStats = MutableStateFlow(SellerStats())
    val sellerStats: StateFlow<SellerStats> = _sellerStats.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSellerData()
    }

    fun loadSellerData() {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            if (user != null) {
                // Tải dữ liệu song song
                val products = sellerRepository.getSellerProducts(user.id)
                val orders = sellerRepository.getSoldOrders(user.id)

                _myProducts.value = products
                _soldOrders.value = orders

                // Tính toán thống kê tự động
                var revenue = 0.0
                var itemsSold = 0
                orders.forEach {
                    revenue += (it.priceAtPurchase * it.quantity)
                    itemsSold += it.quantity
                }

                _sellerStats.value = SellerStats(
                    totalRevenue = revenue,
                    totalOrders = orders.size,
                    totalProductsSold = itemsSold
                )
            }
            _isLoading.value = false
        }
    }
}