package com.example.smartpick.features.review.viewmodel

import app.cash.turbine.test
import com.example.smartpick.BaseUnitTest
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.Review
import com.example.smartpick.core.model.ReviewUser
import com.example.smartpick.core.model.User
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.review.data.ReviewRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewHubViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockReviewRepository: ReviewRepository
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var viewModel: ReviewHubViewModel

    private val fakeUser = User(id = "user123", email = "test@example.com")
    private val fakeProducts = listOf(
        Product(id = "p1", ownerId = "seller1", name = "Shoes", price = 100.0)
    )
    private val fakeReviews = listOf(
        Review(
            id = "r1",
            userId = "user123",
            productId = "p2",
            rating = 4,
            content = "Okay",
            user = ReviewUser(id = "user123", fullName = "John", avatarUrl = null),
            createdAt = "2026-06-08T12:00:00Z"
        )
    )

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockReviewRepository = mockk(relaxed = true)
        mockAuthRepository = mockk(relaxed = true)

        coEvery { mockAuthRepository.getCurrentUser() } returns fakeUser
        coEvery { mockReviewRepository.getProductsToReview("user123") } returns fakeProducts
        coEvery { mockReviewRepository.getMyReviewedProducts("user123") } returns fakeReviews

        viewModel = ReviewHubViewModel(mockReviewRepository, mockAuthRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Fetches review data automatically`() = runTest(testDispatcher) {
        viewModel.isLoading.test {
            // Because fetchReviewData runs immediately in init, we might capture the state transitions
            // Wait, runTest scheduler is advanced during initialization if we yield or wait
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { mockReviewRepository.getProductsToReview("user123") }
            coVerify { mockReviewRepository.getMyReviewedProducts("user123") }

            assertEquals(fakeProducts, viewModel.productsToReview.value)
            assertEquals(fakeReviews, viewModel.reviewedProducts.value)
            assertEquals(false, viewModel.isLoading.value)
        }
    }

    @Test
    fun `fetchReviewData - User logged in - Updates products and reviews`() = runTest(testDispatcher) {
        viewModel.fetchReviewData()

        viewModel.isLoading.test {
            assertEquals(true, awaitItem()) // Starts loading
            assertEquals(false, awaitItem()) // Finishes loading
            ensureAllEventsConsumed()
        }

        assertEquals(fakeProducts, viewModel.productsToReview.value)
        assertEquals(fakeReviews, viewModel.reviewedProducts.value)
    }
}
