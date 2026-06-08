package com.example.smartpick.features.product_detail.viewmodel

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
class ProductDetailViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockHomeRepository: HomeRepository
    private lateinit var mockCartRepository: CartRepository
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var viewModel: ProductDetailViewModel

    private val fakeUser = User(id = "user123", email = "test@example.com")
    private val fakeProduct = Product(id = "p1", ownerId = "s1", name = "Nike", price = 100.0, stock = 5)

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockHomeRepository = mockk(relaxed = true)
        mockCartRepository = mockk(relaxed = true)
        mockAuthRepository = mockk(relaxed = true)

        coEvery { mockAuthRepository.getCurrentUser() } returns fakeUser
        coEvery { mockHomeRepository.getProductById("p1") } returns fakeProduct
        coEvery { mockHomeRepository.getPostIdByProductId("p1") } returns "post123"

        viewModel = ProductDetailViewModel(mockHomeRepository, mockCartRepository, mockAuthRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Default values`() {
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.product)
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.postId.value)
    }

    @Test
    fun `loadProductDetail - Success - Updates state`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            assertEquals(ProductDetailUiState(isLoading = false, product = null, error = null), awaitItem())

            viewModel.loadProductDetail("p1")

            assertEquals(ProductDetailUiState(isLoading = true, product = null, error = null), awaitItem())
            val success = awaitItem()
            assertFalse(success.isLoading)
            assertEquals(fakeProduct, success.product)
            assertNull(success.error)
            ensureAllEventsConsumed()
        }

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("post123", viewModel.postId.value)
    }

    @Test
    fun `loadProductDetail - Failure - Sets error`() = runTest(testDispatcher) {
        coEvery { mockHomeRepository.getProductById("p1") } throws Exception("Product error")

        viewModel.loadProductDetail("p1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.product)
        assertEquals("Product error", viewModel.uiState.value.error)
    }

    @Test
    fun `isProductAvailable - Stock positive - Returns true`() {
        assertTrue(viewModel.isProductAvailable(fakeProduct))
        assertFalse(viewModel.isProductAvailable(fakeProduct.copy(stock = 0)))
    }

    @Test
    fun `addToCart - Success flow`() = runTest(testDispatcher) {
        coEvery { mockCartRepository.addToCart("user123", "p1", "post123") } returns Result.success(Unit)

        // Preload product and post ID
        viewModel.loadProductDetail("p1")
        testDispatcher.scheduler.advanceUntilIdle()

        var successTriggered = false
        viewModel.addToCart(
            product = fakeProduct,
            onSuccess = { successTriggered = true },
            onError = { fail("Should not fail") }
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successTriggered)
        coVerify { mockCartRepository.addToCart("user123", "p1", "post123") }
    }
}
