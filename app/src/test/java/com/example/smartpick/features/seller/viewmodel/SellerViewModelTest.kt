package com.example.smartpick.features.seller.viewmodel

import com.example.smartpick.core.model.User
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.seller.data.SellerRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SellerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockSellerRepo: SellerRepository
    private lateinit var mockAuthRepo: AuthRepository
    private lateinit var viewModel: SellerViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockSellerRepo = mockk()
        mockAuthRepo = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSellerData - Tính toán doanh thu và đơn hàng chuẩn xác`() = runTest(testDispatcher) {
        // 1. CHUẨN BỊ (ARRANGE)
        val fakeUserId = "seller_123"
        val mockUser = User(id = fakeUserId, fullName = "Test User")

        // Tạo 2 đơn hàng ảo (Đơn 1 mua 2 cái giá 100k, Đơn 2 mua 1 cái giá 50k)
        // Tổng doanh thu dự kiến: (2 * 100k) + (1 * 50k) = 250k. Tổng sp: 3. Tổng đơn: 2.
        val mockOrders = listOf(
            SellerRepository.SoldOrderItemDto(id = "1", orderId = "o1", productId = "p1", quantity = 2, priceAtPurchase = 100000.0),
            SellerRepository.SoldOrderItemDto(id = "2", orderId = "o2", productId = "p2", quantity = 1, priceAtPurchase = 50000.0)
        )

        coEvery { mockAuthRepo.getCurrentUser() } returns mockUser
        coEvery { mockSellerRepo.getSellerProducts(fakeUserId) } returns emptyList()
        coEvery { mockSellerRepo.getSoldOrders(fakeUserId) } returns mockOrders

        // 2. THỰC THI (ACT)
        viewModel = SellerViewModel(mockSellerRepo, mockAuthRepo) // Init sẽ gọi luôn loadSellerData
        testDispatcher.scheduler.advanceUntilIdle() // Đợi coroutine chạy xong

        // 3. KIỂM TRA KẾT QUẢ (ASSERT)
        val stats = viewModel.sellerStats.value

        println("TEST KẾT QUẢ TÍNH DOANH THU:")
        println("Kỳ vọng: 250,000 | Thực tế: ${stats.totalRevenue}")
        println("Kỳ vọng (Sản phẩm bán): 3 | Thực tế: ${stats.totalProductsSold}")

        assertEquals(250000.0, stats.totalRevenue, 0.0) // So sánh số Double
        assertEquals(3, stats.totalProductsSold)
        assertEquals(2, stats.totalOrders)
        assertFalse(viewModel.isLoading.value) // Trạng thái Loading phải tắt
    }
}