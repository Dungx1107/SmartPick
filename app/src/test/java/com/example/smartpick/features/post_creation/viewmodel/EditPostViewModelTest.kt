package com.example.smartpick.features.post_creation.viewmodel

import app.cash.turbine.test
import android.content.Context
import android.net.Uri
import com.example.smartpick.BaseUnitTest
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
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
class EditPostViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockFeedRepository: FeedRepository
    private lateinit var mockContext: Context
    private lateinit var viewModel: EditPostViewModel

    private val fakePost = Post(id = "post123", userId = "user123", content = "Old Content")
    private val fakeUser = User(id = "user123", email = "test@example.com")
    private val fakeProduct = Product(id = "prod123", ownerId = "user123", name = "Shoes", price = 100.0)

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockFeedRepository = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)

        viewModel = EditPostViewModel(mockFeedRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Idle`() {
        assertEquals(EditPostUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `loadPost - Success - Emits Success state`() = runTest(testDispatcher) {
        coEvery {
            mockFeedRepository.getPostById("post123")
        } returns Result.success(Triple(fakePost, fakeUser, fakeProduct))

        viewModel.uiState.test {
            assertEquals(EditPostUiState.Idle, awaitItem())

            viewModel.loadPost("post123")

            assertEquals(EditPostUiState.Loading, awaitItem())
            val successState = awaitItem() as EditPostUiState.Success
            assertEquals(fakePost, successState.post)
            assertEquals(fakeUser, successState.user)
            assertEquals(fakeProduct, successState.product)
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `loadPost - Failure - Emits Error state`() = runTest(testDispatcher) {
        coEvery {
            mockFeedRepository.getPostById("post123")
        } returns Result.failure(Exception("Not found"))

        viewModel.loadPost("post123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is EditPostUiState.Error)
        val errorState = viewModel.uiState.value as EditPostUiState.Error
        assertEquals("Không thể tải bài viết", errorState.message)
    }

    @Test
    fun `savePostChanges - Success flow - Uploads media and updates repository`() = runTest(testDispatcher) {
        val mockUri1 = mockk<Uri>()
        val mockUri2 = mockk<Uri>()
        coEvery { mockFeedRepository.uploadMedia(mockContext, mockUri1) } returns "http://media.com/new1.jpg"
        coEvery { mockFeedRepository.uploadMedia(mockContext, mockUri2) } returns "http://media.com/new2.jpg"

        coEvery {
            mockFeedRepository.updatePostFull(
                "post123",
                "New Content",
                listOf("http://media.com/old.jpg", "http://media.com/new1.jpg", "http://media.com/new2.jpg"),
                fakeProduct
            )
        } returns Result.success(Unit)

        viewModel.uiState.test {
            assertEquals(EditPostUiState.Idle, awaitItem())

            viewModel.savePostChanges(
                postId = "post123",
                content = "New Content",
                existingUrls = listOf("http://media.com/old.jpg"),
                newUris = listOf(mockUri1, mockUri2),
                product = fakeProduct,
                context = mockContext
            )

            assertEquals(EditPostUiState.Loading, awaitItem())
            val successState = awaitItem() as EditPostUiState.UpdateSuccess
            assertEquals("Cập nhật bài viết thành công!", successState.message)
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `savePostChanges - Failure flow - Emits Error state`() = runTest(testDispatcher) {
        coEvery {
            mockFeedRepository.updatePostFull(any(), any(), any(), any())
        } returns Result.failure(Exception("Write failed"))

        viewModel.savePostChanges(
            postId = "post123",
            content = "New Content",
            existingUrls = emptyList(),
            newUris = emptyList(),
            product = null,
            context = mockContext
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is EditPostUiState.Error)
        val errorState = viewModel.uiState.value as EditPostUiState.Error
        assertEquals("Lỗi cập nhật: Write failed", errorState.message)
    }
}
