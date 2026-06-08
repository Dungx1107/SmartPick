package com.example.smartpick.features.checkout.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.smartpick.BaseUnitTest
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Order
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.cart.data.CartRepository
import com.example.smartpick.features.checkout.data.OrderRepository
import com.example.smartpick.features.home.data.HomeRepository
import com.example.smartpick.navigation.Routes
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockOrderRepository: OrderRepository
    private lateinit var mockCartRepository: CartRepository
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var mockHomeRepository: HomeRepository

    private val fakeUser = User(id = "user123", email = "test@example.com", phoneNumber = "0912345678")

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockOrderRepository = mockk(relaxed = true)
        mockCartRepository = mockk(relaxed = true)
        mockAuthRepository = mockk(relaxed = true)
        mockHomeRepository = mockk(relaxed = true)

        coEvery { mockAuthRepository.getCurrentUser() } returns fakeUser
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserDefaultInfo - Automatically populates phone and address from user and last order`() = runTest(testDispatcher) {
        val lastOrderInfo = com.example.smartpick.features.checkout.data.LastOrderInfoDto(
            phoneNumber = "0987654321",
            shippingAddress = "456 Oak St"
        )
        coEvery { mockOrderRepository.getLastOrderInfo(fakeUser.id) } returns lastOrderInfo

        val viewModel = CheckoutViewModel(
            mockOrderRepository, mockCartRepository, mockAuthRepository, mockHomeRepository, SavedStateHandle()
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Phải ưu tiên số điện thoại và địa chỉ từ đơn hàng gần nhất
        assertEquals("0987654321", viewModel.phone.value)
        assertEquals("456 Oak St", viewModel.address.value)
    }

    @Test
    fun `loadCheckoutData - Buy Now flow`() = runTest(testDispatcher) {
        val mockProduct = Product(id = "prod_123", ownerId = "seller1", name = "Test Product", price = 200.0, stock = 10)
        coEvery { mockHomeRepository.getProductById("prod_123") } returns mockProduct

        val savedStateHandle = SavedStateHandle(mapOf(
            Routes.Checkout.ARG_PRODUCT_ID to "prod_123",
            Routes.Checkout.ARG_QUANTITY to 3
        ))

        val viewModel = CheckoutViewModel(
            mockOrderRepository, mockCartRepository, mockAuthRepository, mockHomeRepository, savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val cartList = viewModel.cartItems.value
        assertEquals(1, cartList.size)
        assertEquals("prod_123", cartList[0].productId)
        assertEquals(3, cartList[0].quantity)
        assertEquals(mockProduct, cartList[0].product)
    }

    @Test
    fun `loadCheckoutData - Cart Checkout flow`() = runTest(testDispatcher) {
        val allCartItems = listOf(
            CartItem(id = "c1", userId = "user123", productId = "p1", quantity = 1),
            CartItem(id = "c2", userId = "user123", productId = "p2", quantity = 2),
            CartItem(id = "c3", userId = "user123", productId = "p3", quantity = 3)
        )
        coEvery { mockCartRepository.fetchCartItems(fakeUser.id) } returns allCartItems

        val savedStateHandle = SavedStateHandle(mapOf(
            Routes.Checkout.ARG_CART_ITEM_IDS to "c1,c3"
        ))

        val viewModel = CheckoutViewModel(
            mockOrderRepository, mockCartRepository, mockAuthRepository, mockHomeRepository, savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val cartList = viewModel.cartItems.value
        assertEquals(2, cartList.size)
        assertEquals("c1", cartList[0].id)
        assertEquals("c3", cartList[1].id)
    }

    @Test
    fun `placeOrder - Missing phone or address - Triggers onError`() = runTest(testDispatcher) {
        val viewModel = CheckoutViewModel(
            mockOrderRepository, mockCartRepository, mockAuthRepository, mockHomeRepository, SavedStateHandle()
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updatePhone("")
        viewModel.updateAddress("")

        var errorMsg: String? = null
        viewModel.placeOrder(
            onSuccess = { fail("Should not succeed") },
            onError = { errorMsg = it }
        )

        assertEquals("Vui lòng nhập đầy đủ thông tin nhận hàng", errorMsg)
    }

    @Test
    fun `placeOrder - Empty cart - Triggers onError`() = runTest(testDispatcher) {
        val viewModel = CheckoutViewModel(
            mockOrderRepository, mockCartRepository, mockAuthRepository, mockHomeRepository, SavedStateHandle()
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updatePhone("0912345678")
        viewModel.updateAddress("123 Main St")

        var errorMsg: String? = null
        viewModel.placeOrder(
            onSuccess = { fail("Should not succeed") },
            onError = { errorMsg = it }
        )

        assertEquals("Giỏ hàng của bạn đang trống", errorMsg)
    }

    @Test
    fun `placeOrder - Stock insufficient - Triggers out of stock error`() = runTest(testDispatcher) {
        val mockProduct = Product(id = "prod_123", ownerId = "seller1", name = "Shoes", price = 200.0, stock = 1)
        coEvery { mockHomeRepository.getProductById("prod_123") } returns mockProduct

        val savedStateHandle = SavedStateHandle(mapOf(
            Routes.Checkout.ARG_PRODUCT_ID to "prod_123",
            Routes.Checkout.ARG_QUANTITY to 5 // quantity (5) > stock (1)
        ))

        val viewModel = CheckoutViewModel(
            mockOrderRepository, mockCartRepository, mockAuthRepository, mockHomeRepository, savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updatePhone("0912345678")
        viewModel.updateAddress("123 Main St")

        var errorMsg: String? = null
        viewModel.placeOrder(
            onSuccess = { fail("Should not succeed") },
            onError = { errorMsg = it }
        )

        assertEquals("Sản phẩm 'Shoes' chỉ còn 1 chiếc trong kho!", errorMsg)
    }

    @Test
    fun `placeOrder - Success flow`() = runTest(testDispatcher) {
        val mockProduct = Product(id = "prod_123", ownerId = "seller1", name = "Shoes", price = 200.0, stock = 10)
        coEvery { mockHomeRepository.getProductById("prod_123") } returns mockProduct
        coEvery { mockOrderRepository.checkout(any(), any(), any(), any(), any()) } returns Result.success(Unit)

        val savedStateHandle = SavedStateHandle(mapOf(
            Routes.Checkout.ARG_PRODUCT_ID to "prod_123",
            Routes.Checkout.ARG_QUANTITY to 2
        ))

        val viewModel = CheckoutViewModel(
            mockOrderRepository, mockCartRepository, mockAuthRepository, mockHomeRepository, savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updatePhone("0912345678")
        viewModel.updateAddress("123 Main St")

        var successTriggered = false
        viewModel.placeOrder(
            onSuccess = { successTriggered = true },
            onError = { fail("Should not fail: $it") }
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successTriggered)
        assertTrue(viewModel.cartItems.value.isEmpty()) // Cart is cleared on success
    }
}
