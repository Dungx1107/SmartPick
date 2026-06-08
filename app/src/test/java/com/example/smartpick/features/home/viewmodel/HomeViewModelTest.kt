package com.example.smartpick.features.home.viewmodel

import app.cash.turbine.test
import com.example.smartpick.BaseUnitTest
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.cart.data.CartRepository
import com.example.smartpick.features.home.data.HomeRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockHomeRepository: HomeRepository
    private lateinit var mockCartRepository: CartRepository
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var viewModel: HomeViewModel

    private val mockProducts = listOf(
        Product(id = "p1", ownerId = "s1", name = "Nike Shoes", price = 100.0, postId = "po1"),
        Product(id = "p2", ownerId = "s2", name = "Adidas Shirt", price = 50.0, postId = "po2")
    )
    private val fakeUser = User(id = "user123", email = "test@example.com")

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockHomeRepository = mockk(relaxed = true)
        mockCartRepository = mockk(relaxed = true)
        mockAuthRepository = mockk(relaxed = true)

        coEvery { mockHomeRepository.getAllProducts() } returns mockProducts
        coEvery { mockAuthRepository.getCurrentUser() } returns fakeUser

        viewModel = HomeViewModel(mockHomeRepository, mockCartRepository, mockAuthRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Starts Loading and fetches products`() = runTest(testDispatcher) {
        // ViewModel init calls fetchProducts automatically
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockHomeRepository.getAllProducts() }
        assertTrue(viewModel.uiState.value is HomeUiState.Success)
        val successState = viewModel.uiState.value as HomeUiState.Success
        assertEquals(mockProducts, successState.products)
    }

    @Test
    fun `fetchProducts - Failure - Emits error state`() = runTest(testDispatcher) {
        coEvery { mockHomeRepository.getAllProducts() } throws Exception("API Error")

        viewModel.fetchProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is HomeUiState.Error)
        val errorState = viewModel.uiState.value as HomeUiState.Error
        assertEquals("API Error", errorState.message)
    }

    @Test
    fun `searchProducts - Filter by query`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        // Search for "nike"
        viewModel.searchProducts("nike")
        val stateAfterSearch = viewModel.uiState.value as HomeUiState.Success
        assertEquals(1, stateAfterSearch.products.size)
        assertEquals("p1", stateAfterSearch.products[0].id)

        // Blank query restores all products
        viewModel.searchProducts("")
        val stateAfterClear = viewModel.uiState.value as HomeUiState.Success
        assertEquals(2, stateAfterClear.products.size)
    }

    @Test
    fun `addToCart - User not logged in - Triggers onError`() = runTest(testDispatcher) {
        coEvery { mockAuthRepository.getCurrentUser() } returns null

        var errorMsg: String? = null
        viewModel.addToCart(
            product = mockProducts[0],
            onSuccess = { fail("Should not succeed") },
            onError = { errorMsg = it }
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Bạn cần đăng nhập để thực hiện tính năng này", errorMsg)
        coVerify(exactly = 0) { mockCartRepository.addToCart(any(), any(), any()) }
    }

    @Test
    fun `addToCart - Success flow`() = runTest(testDispatcher) {
        coEvery { mockCartRepository.addToCart(fakeUser.id, "p1", "po1") } returns Result.success(Unit)

        var successTriggered = false
        viewModel.addToCart(
            product = mockProducts[0],
            onSuccess = { successTriggered = true },
            onError = { fail("Should not fail: $it") }
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successTriggered)
        coVerify { mockCartRepository.addToCart(fakeUser.id, "p1", "po1") }
    }

    @Test
    fun `addToCart - Failure flow`() = runTest(testDispatcher) {
        coEvery { mockCartRepository.addToCart(fakeUser.id, "p1", "po1") } returns Result.failure(Exception("Limit reached"))

        var errorMsg: String? = null
        viewModel.addToCart(
            product = mockProducts[0],
            onSuccess = { fail("Should not succeed") },
            onError = { errorMsg = it }
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Limit reached", errorMsg)
    }
}
