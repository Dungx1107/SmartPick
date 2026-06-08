package com.example.smartpick.features.review.viewmodel

import app.cash.turbine.test
import com.example.smartpick.BaseUnitTest
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
class ReviewViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockReviewRepository: ReviewRepository
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var viewModel: ReviewViewModel

    private val fakeUser = User(id = "user123", email = "test@example.com")
    private val fakeReviews = listOf(
        Review(
            id = "r1",
            userId = "user123",
            productId = "p1",
            rating = 5,
            content = "Excellent!",
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
        coEvery { mockReviewRepository.getProductReviews("p1") } returns fakeReviews

        viewModel = ReviewViewModel(mockReviewRepository, mockAuthRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Default values`() {
        assertTrue(viewModel.productReviews.value.isEmpty())
        assertFalse(viewModel.canReview.value)
        assertFalse(viewModel.isSubmitting.value)
    }

    @Test
    fun `loadReviewData - User logged in and bought product - Loads reviews and allows review`() = runTest(testDispatcher) {
        coEvery { mockReviewRepository.checkUserBoughtProduct("user123", "p1") } returns true

        viewModel.loadReviewData("p1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(fakeReviews, viewModel.productReviews.value)
        assertTrue(viewModel.canReview.value)
    }

    @Test
    fun `loadReviewData - User not logged in - Loads reviews but denies review`() = runTest(testDispatcher) {
        coEvery { mockAuthRepository.getCurrentUser() } returns null

        viewModel.loadReviewData("p1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(fakeReviews, viewModel.productReviews.value)
        assertFalse(viewModel.canReview.value)
    }

    @Test
    fun `submitProductReview - Blank content - Fails immediately`() = runTest(testDispatcher) {
        var errorMsg: String? = null
        viewModel.submitProductReview(
            productId = "p1",
            orderItemId = "oi1",
            rating = 5,
            content = "   ",
            onSuccess = { fail("Should not succeed") },
            onError = { errorMsg = it }
        )

        assertEquals("Nội dung đánh giá không được để trống", errorMsg)
        coVerify(exactly = 0) { mockReviewRepository.submitReview(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `submitProductReview - Success flow`() = runTest(testDispatcher) {
        coEvery {
            mockReviewRepository.submitReview("user123", "p1", "oi1", 5, "Good product")
        } returns Result.success(Unit)

        var successTriggered = false
        viewModel.isSubmitting.test {
            assertEquals(false, awaitItem()) // Initial

            viewModel.submitProductReview(
                productId = "p1",
                orderItemId = "oi1",
                rating = 5,
                content = "Good product",
                onSuccess = { successTriggered = true },
                onError = { fail("Should not fail") }
            )

            assertEquals(true, awaitItem()) // Loading
            assertEquals(false, awaitItem()) // Done
            ensureAllEventsConsumed()
        }

        assertTrue(successTriggered)
        coVerify { mockReviewRepository.submitReview("user123", "p1", "oi1", 5, "Good product") }
    }

    @Test
    fun `submitProductReview - Failure flow`() = runTest(testDispatcher) {
        coEvery {
            mockReviewRepository.submitReview("user123", "p1", "oi1", 5, "Good product")
        } returns Result.failure(Exception("Submit failed"))

        var errorMsg: String? = null
        viewModel.submitProductReview(
            productId = "p1",
            orderItemId = "oi1",
            rating = 5,
            content = "Good product",
            onSuccess = { fail("Should not succeed") },
            onError = { errorMsg = it }
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Submit failed", errorMsg)
    }
}
