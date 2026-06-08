package com.example.smartpick.features.feed.viewmodel

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
class FeedViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockFeedRepository: FeedRepository
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var viewModel: FeedViewModel

    private val fakeUser = User(id = "user123", email = "test@example.com")
    private val fakeFeed = listOf(
        Triple(Post(id = "post1", userId = "user_a", content = "Normal Post"), User(id = "user_a", fullName = "Alice"), null as Product?),
        Triple(Post(id = "post2", userId = "user_b", content = "Shared Post", sharedPostId = "post1"), User(id = "user_b", fullName = "Bob"), null as Product?)
    )

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockFeedRepository = mockk(relaxed = true)
        mockAuthRepository = mockk(relaxed = true)

        coEvery { mockAuthRepository.getCurrentUser() } returns fakeUser
        coEvery { mockFeedRepository.getPostsWithUsers(fakeUser.id) } returns fakeFeed

        viewModel = FeedViewModel(mockFeedRepository, mockAuthRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Starts Loading and loads feed`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockFeedRepository.getPostsWithUsers(fakeUser.id) }
        assertTrue(viewModel.uiState.value is FeedUiState.Success)
        val successState = viewModel.uiState.value as FeedUiState.Success
        // post2 has sharedPostId != null so it should be filtered out
        assertEquals(1, successState.posts.size)
        assertEquals("post1", successState.posts[0].first.id)
    }

    @Test
    fun `loadFeed - Failure - Sets error state`() = runTest(testDispatcher) {
        coEvery { mockFeedRepository.getPostsWithUsers(fakeUser.id) } throws Exception("Network Error")

        viewModel.loadFeed()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is FeedUiState.Error)
        val errorState = viewModel.uiState.value as FeedUiState.Error
        assertEquals("Network Error", errorState.message)
    }

    @Test
    fun `loadReactedPosts - Success - Populates reactedPosts list`() = runTest(testDispatcher) {
        val mockReacted = listOf(
            Triple(Post(id = "post1", userId = "user_a"), User(id = "user_a"), null as Product?)
        )
        coEvery { mockFeedRepository.getReactedPosts(fakeUser.id) } returns mockReacted

        viewModel.isReactedLoading.test {
            assertEquals(false, awaitItem()) // Initial state

            viewModel.loadReactedPosts()

            assertEquals(true, awaitItem()) // Loading
            assertEquals(false, awaitItem()) // Finished
            ensureAllEventsConsumed()
        }

        assertEquals(mockReacted, viewModel.reactedPosts.value)
    }

    @Test
    fun `toggleReaction - Updates local UI state and calls repository`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle() // Đợi init loadFeed xong

        // Ban đầu post1 có 0 reaction
        val initialPosts = (viewModel.uiState.value as FeedUiState.Success).posts
        assertEquals(0, initialPosts[0].first.reactionCount)

        viewModel.toggleReaction("post1", ReactionType.LIKE)

        // Phải thay đổi local state lập tức
        val updatedPosts = (viewModel.uiState.value as FeedUiState.Success).posts
        assertEquals(1, updatedPosts[0].first.reactionCount)
        assertEquals(ReactionType.LIKE, updatedPosts[0].first.currentUserReaction)

        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { mockFeedRepository.toggleReaction("post1", fakeUser.id, ReactionType.LIKE) }
    }

    @Test
    fun `sharePost - Success - Triggers share and silent refresh`() = runTest(testDispatcher) {
        coEvery { mockFeedRepository.sharePost("post1", fakeUser.id, "Cool caption") } returns Result.success(Unit)

        var successTriggered = false
        viewModel.sharePost("post1", "Cool caption") {
            successTriggered = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successTriggered)
        coVerify { mockFeedRepository.sharePost("post1", fakeUser.id, "Cool caption") }
    }

    @Test
    fun `deletePost - Success - Removes post from feed list`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle() // Đợi init loadFeed
        coEvery { mockFeedRepository.deletePost("post1") } returns Result.success(Unit)

        var successTriggered = false
        viewModel.deletePost("post1", onSuccess = { successTriggered = true }, onError = {})
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successTriggered)
        val finalPosts = (viewModel.uiState.value as FeedUiState.Success).posts
        assertTrue(finalPosts.isEmpty()) // post1 bị xóa
    }
}
