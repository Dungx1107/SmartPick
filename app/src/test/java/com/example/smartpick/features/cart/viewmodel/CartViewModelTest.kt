package com.example.smartpick.features.cart.viewmodel

import app.cash.turbine.test
import com.example.smartpick.BaseUnitTest
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.cart.data.CartRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val cartItemsFlow = MutableStateFlow<List<CartItem>>(emptyList())

    private lateinit var mockCartRepository: CartRepository
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var viewModel: CartViewModel

    private val fakeUser = User(id = "user123", email = "test@example.com")

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockCartRepository = mockk(relaxed = true)
        mockAuthRepository = mockk(relaxed = true)

        every { mockCartRepository.cartItemsFlow } returns cartItemsFlow
        coEvery { mockAuthRepository.getCurrentUser() } returns fakeUser

        // Khởi tạo viewModel
        viewModel = CartViewModel(mockCartRepository, mockAuthRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Verify defaults and refreshCart triggered`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockCartRepository.fetchCartItems(fakeUser.id) }
        assertTrue(viewModel.cartItems.value.isEmpty())
        assertTrue(viewModel.selectedIds.value.isEmpty())
        assertEquals(0, viewModel.totalCartCount.value)
    }

    @Test
    fun `toggleSelection - Adds or removes item ID from selection set`() = runTest(testDispatcher) {
        viewModel.toggleSelection("item_1")
        assertTrue(viewModel.selectedIds.value.contains("item_1"))

        viewModel.toggleSelection("item_1")
        assertFalse(viewModel.selectedIds.value.contains("item_1"))
    }

    @Test
    fun `selectAll - Toggle select all true and false`() = runTest(testDispatcher) {
        val cartList = listOf(
            CartItem(id = "item_1", userId = "user123", productId = "p1", quantity = 1),
            CartItem(id = "item_2", userId = "user123", productId = "p2", quantity = 2)
        )
        cartItemsFlow.value = cartList
        testDispatcher.scheduler.advanceUntilIdle()

        // Select All -> Set should contain all IDs
        viewModel.selectAll(true)
        assertEquals(setOf("item_1", "item_2"), viewModel.selectedIds.value)

        // Select All False -> Set should be empty
        viewModel.selectAll(false)
        assertTrue(viewModel.selectedIds.value.isEmpty())
    }

    @Test
    fun `totalCartCount - Map function calculates sum of quantities`() = runTest(testDispatcher) {
        viewModel.totalCartCount.test {
            assertEquals(0, awaitItem())

            cartItemsFlow.value = listOf(
                CartItem(id = "item_1", userId = "user123", productId = "p1", quantity = 3),
                CartItem(id = "item_2", userId = "user123", productId = "p2", quantity = 5)
            )

            assertEquals(8, awaitItem())
        }
    }

    @Test
    fun `increaseQuantity - When stock is sufficient - Calls repository update`() = runTest(testDispatcher) {
        val product = Product(id = "p1", ownerId = "s1", name = "Shoes", price = 100.0, stock = 5)
        val cartItem = CartItem(id = "item_1", userId = "user123", productId = "p1", quantity = 2, product = product)

        viewModel.increaseQuantity(cartItem)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockCartRepository.updateCartItemQuantity(fakeUser.id, "item_1", 3) }
        assertNull(viewModel.cartError.value)
    }

    @Test
    fun `increaseQuantity - When stock is insufficient - Emits error`() = runTest(testDispatcher) {
        val product = Product(id = "p1", ownerId = "s1", name = "Shoes", price = 100.0, stock = 2)
        val cartItem = CartItem(id = "item_1", userId = "user123", productId = "p1", quantity = 2, product = product)

        viewModel.increaseQuantity(cartItem)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { mockCartRepository.updateCartItemQuantity(any(), any(), any()) }
        assertEquals("Chỉ còn 2 sản phẩm trong kho!", viewModel.cartError.value)
    }

    @Test
    fun `decreaseQuantity - Calls repository update with quantity minus 1`() = runTest(testDispatcher) {
        val cartItem = CartItem(id = "item_1", userId = "user123", productId = "p1", quantity = 3)

        viewModel.decreaseQuantity(cartItem)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockCartRepository.updateCartItemQuantity(fakeUser.id, "item_1", 2) }
    }

    @Test
    fun `removeItem - Removes selection and sets quantity to 0`() = runTest(testDispatcher) {
        viewModel.toggleSelection("item_1")
        val cartItem = CartItem(id = "item_1", userId = "user123", productId = "p1", quantity = 1)

        viewModel.removeItem("item_1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.selectedIds.value.contains("item_1"))
        coVerify { mockCartRepository.updateCartItemQuantity(fakeUser.id, "item_1", 0) }
    }
}
