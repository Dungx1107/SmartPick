package com.example.smartpick.features.profile.viewmodel

import app.cash.turbine.test
import com.example.smartpick.BaseUnitTest
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.model.User
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.feed.data.FeedRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockFeedRepository: FeedRepository
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var viewModel: ProfileViewModel

    private val fakeUser = User(id = "user123", email = "test@example.com")
    private val fakePosts = listOf(
        Triple(Post(id = "post1", userId = "user123", content = "Hello Profile"), fakeUser, null as Product?)
    )
    private val fakeSoldItems = listOf(
        FeedRepository.SoldItemDto(
            id = "s1",
            quantity = 1,
            priceAtPurchase = 120.0,
            createdAt = "2026-06-08T12:00:00Z",
            products = null
        )
    )

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockFeedRepository = mockk(relaxed = true)
        mockAuthRepository = mockk(relaxed = true)

        coEvery { mockAuthRepository.getCurrentUser() } returns fakeUser
        coEvery { mockFeedRepository.getUserPosts("user123", "user123") } returns fakePosts
        coEvery { mockFeedRepository.getSoldItems("user123") } returns fakeSoldItems

        viewModel = ProfileViewModel(mockFeedRepository, mockAuthRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Idle`() {
        assertTrue(viewModel.userPosts.value.isEmpty())
        assertTrue(viewModel.soldItems.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadUserPosts - Success flow`() = runTest(testDispatcher) {
        viewModel.isLoading.test {
            assertEquals(false, awaitItem()) // Initial

            viewModel.loadUserPosts("user123", "user123")

            assertEquals(true, awaitItem()) // Loading
            assertEquals(false, awaitItem()) // Done
            ensureAllEventsConsumed()
        }

        assertEquals(fakePosts, viewModel.userPosts.value)
        assertEquals(fakeSoldItems, viewModel.soldItems.value)
    }

    @Test
    fun `deletePost - Success - Removes post from local state`() = runTest(testDispatcher) {
        // Prepare list in state
        viewModel.loadUserPosts("user123", "user123")
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { mockFeedRepository.deletePost("post1") } returns Result.success(Unit)

        var successTriggered = false
        viewModel.deletePost("post1", onSuccess = { successTriggered = true }, onError = {})
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successTriggered)
        assertTrue(viewModel.userPosts.value.isEmpty())
    }

    @Test
    fun `toggleReaction - Updates local state and calls repository`() = runTest(testDispatcher) {
        viewModel.loadUserPosts("user123", "user123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.userPosts.value[0].first.reactionCount)

        viewModel.toggleReaction("post1", ReactionType.LIKE)

        assertEquals(1, viewModel.userPosts.value[0].first.reactionCount)
        assertEquals(ReactionType.LIKE, viewModel.userPosts.value[0].first.currentUserReaction)

        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { mockFeedRepository.toggleReaction("post1", fakeUser.id, ReactionType.LIKE) }
    }
}
