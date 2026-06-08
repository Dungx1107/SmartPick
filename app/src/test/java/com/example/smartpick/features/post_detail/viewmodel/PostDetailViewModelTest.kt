package com.example.smartpick.features.post_detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.smartpick.BaseUnitTest
import com.example.smartpick.features.post_detail.data.dto.PostDetailResponse
import com.example.smartpick.core.data.dto.UserDto
import com.example.smartpick.features.post_detail.data.PostDetailRepository
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
class PostDetailViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockPostDetailRepository: PostDetailRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: PostDetailViewModel

    private val fakeUserDto = UserDto(id = "user123", email = "test@example.com", fullName = "John")
    private val fakePostDetail = PostDetailResponse(
        id = "post123",
        content = "Detail content",
        mediaUrls = emptyList(),
        createdAt = "2026-06-08T12:00:00Z",
        user = fakeUserDto,
        product = null,
        likesCount = 5,
        isLiked = true,
        sharedPostId = null
    )

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockPostDetailRepository = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle(mapOf(Routes.PostDetail.ARG_POST_ID to "post123"))

        coEvery { mockPostDetailRepository.getPostDetail("post123") } returns fakePostDetail

        viewModel = PostDetailViewModel(mockPostDetailRepository, savedStateHandle)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Automatically triggers loadPostDetail`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockPostDetailRepository.getPostDetail("post123") }
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("post123", viewModel.uiState.value.post?.id)
        assertEquals("Detail content", viewModel.uiState.value.post?.content)
        assertEquals("John", viewModel.uiState.value.user?.fullName)
    }

    @Test
    fun `loadPostDetail - Success - Updates uiState`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            // Drop initial loading/done state from init trigger
            // To test clean loadPostDetail flow, we can call it explicitly
            viewModel.loadPostDetail("post123")

            val loading = awaitItem()
            assertTrue(loading.isLoading)

            val success = awaitItem()
            assertFalse(success.isLoading)
            assertEquals("post123", success.post?.id)
            assertNull(success.error)
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `loadPostDetail - Failure - Emits error`() = runTest(testDispatcher) {
        coEvery { mockPostDetailRepository.getPostDetail("post123") } throws Exception("Failed to load post")

        viewModel.loadPostDetail("post123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.post)
        assertEquals("Failed to load post", viewModel.uiState.value.error)
    }
}
