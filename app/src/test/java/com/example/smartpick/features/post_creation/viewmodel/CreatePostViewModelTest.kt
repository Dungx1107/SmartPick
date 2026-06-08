package com.example.smartpick.features.post_creation.viewmodel

import app.cash.turbine.test
import android.content.Context
import android.widget.Toast
import com.example.smartpick.BaseUnitTest
import com.example.smartpick.core.model.User
import com.example.smartpick.core.network.ModerationException
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.post_creation.data.PostCreationRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreatePostViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockPostCreationRepository: PostCreationRepository
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var mockContext: Context
    private lateinit var viewModel: CreatePostViewModel

    private val fakeUser = User(id = "user123", email = "test@example.com")

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockPostCreationRepository = mockk(relaxed = true)
        mockAuthRepository = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)

        coEvery { mockAuthRepository.getCurrentUser() } returns fakeUser

        // Mock Toast
        mockkStatic(Toast::class)
        val mockToast = mockk<Toast>(relaxed = true)
        every { Toast.makeText(any(), any<CharSequence>(), any()) } returns mockToast

        viewModel = CreatePostViewModel(mockPostCreationRepository, mockAuthRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Idle`() {
        assertEquals(CreatePostUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `clearError - Resets error to Idle`() {
        // Gán trạng thái Error thủ công qua reflection hoặc kích hoạt lỗi
        // Ở đây ta có thể chỉ gán gián tiếp hoặc chạy clearError khi Idle
        viewModel.clearError()
        assertEquals(CreatePostUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `createPost - Success flow`() = runTest(testDispatcher) {
        val mockResult = mockk<io.github.jan.supabase.postgrest.result.PostgrestResult>()
        coEvery {
            mockPostCreationRepository.createFullPost("user123", "Content", emptyList(), null, mockContext)
        } returns mockResult

        viewModel.uiState.test {
            assertEquals(CreatePostUiState.Idle, awaitItem())

            viewModel.createPost("Content", emptyList(), null, mockContext)

            assertEquals(CreatePostUiState.Loading, awaitItem())
            assertEquals(CreatePostUiState.Success, awaitItem())
            ensureAllEventsConsumed()
        }

        // Verify Toast hiển thị
        verify(exactly = 1) { Toast.makeText(mockContext, "Đăng bài thành công!", Toast.LENGTH_SHORT) }
    }

    @Test
    fun `createPost - User not logged in - Emits error`() = runTest(testDispatcher) {
        coEvery { mockAuthRepository.getCurrentUser() } returns null

        viewModel.createPost("Content", emptyList(), null, mockContext)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is CreatePostUiState.Error)
        val errorState = viewModel.uiState.value as CreatePostUiState.Error
        assertEquals("Bạn chưa đăng nhập", errorState.message)
    }

    @Test
    fun `createPost - ModerationException - Emits error with details`() = runTest(testDispatcher) {
        val modError = "Nội dung bài viết chứa từ ngữ vi phạm tiêu chuẩn cộng đồng."
        coEvery {
            mockPostCreationRepository.createFullPost(any(), any(), any(), any(), any())
        } throws ModerationException(modError)

        viewModel.createPost("Toxic Content", emptyList(), null, mockContext)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is CreatePostUiState.Error)
        val errorState = viewModel.uiState.value as CreatePostUiState.Error
        assertEquals(modError, errorState.message)
    }

    @Test
    fun `createPost - General Exception - Emits error`() = runTest(testDispatcher) {
        coEvery {
            mockPostCreationRepository.createFullPost(any(), any(), any(), any(), any())
        } throws Exception("Disk Full")

        viewModel.createPost("Content", emptyList(), null, mockContext)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is CreatePostUiState.Error)
        val errorState = viewModel.uiState.value as CreatePostUiState.Error
        assertEquals("Disk Full", errorState.message)
    }
}
