package com.example.smartpick.features.auth.viewmodel

import app.cash.turbine.test
import com.example.smartpick.BaseUnitTest
import com.example.smartpick.core.model.User
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.auth.data.AvailabilityResponse
import io.github.jan.supabase.gotrue.SessionStatus
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val sessionStatusFlow = MutableSharedFlow<SessionStatus>(replay = 1)

    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockAuthRepository = mockk(relaxed = true)
        every { mockAuthRepository.sessionStatus } returns sessionStatusFlow

        // Khởi tạo viewModel mặc định
        viewModel = AuthViewModel(mockAuthRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Verify defaults`() = runTest(testDispatcher) {
        assertEquals(AuthState.Idle, viewModel.authState.value)
        assertNull(viewModel.currentUser.value)
        assertTrue(viewModel.isInitializing.value)
        assertFalse(viewModel.hasShownWelcomeToast.value)
    }

    @Test
    fun `observeSession - When Authenticated - Updates currentUser and finishes initialization`() = runTest(testDispatcher) {
        val mockUser = User(id = "user123", email = "test@example.com", fullName = "Test User")
        coEvery { mockAuthRepository.getCurrentUser() } returns mockUser

        // Giả lập trạng thái đã đăng nhập
        sessionStatusFlow.tryEmit(SessionStatus.Authenticated(mockk(), mockk()))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockUser, viewModel.currentUser.value)
        assertFalse(viewModel.isInitializing.value)
    }

    @Test
    fun `observeSession - When NotAuthenticated - Clears currentUser and finishes initialization`() = runTest(testDispatcher) {
        // Giả lập trạng thái đăng xuất
        sessionStatusFlow.tryEmit(SessionStatus.NotAuthenticated)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.currentUser.value)
        assertFalse(viewModel.isInitializing.value)
    }

    @Test
    fun `signInManual - Success flow`() = runTest(testDispatcher) {
        coEvery { mockAuthRepository.signInManual(any(), any()) } returns Result.success(Unit)

        viewModel.authState.test {
            assertEquals(AuthState.Idle, awaitItem()) // Trạng thái ban đầu

            viewModel.signInManual("test@example.com", "Password123")
            
            assertEquals(AuthState.Loading, awaitItem()) // Đang xử lý
            assertEquals(AuthState.Success, awaitItem()) // Thành công
            
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `signInManual - Failure flow`() = runTest(testDispatcher) {
        val errorMsg = "Email hoặc mật khẩu không chính xác"
        coEvery { mockAuthRepository.signInManual(any(), any()) } returns Result.failure(Exception(errorMsg))

        viewModel.authState.test {
            assertEquals(AuthState.Idle, awaitItem())

            viewModel.signInManual("test@example.com", "WrongPass")

            assertEquals(AuthState.Loading, awaitItem())
            val errorState = awaitItem() as AuthState.Error
            assertEquals(errorMsg, errorState.message)

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `signInManual - Empty inputs - Emits Error state without network call`() = runTest(testDispatcher) {
        viewModel.authState.test {
            assertEquals(AuthState.Idle, awaitItem())

            viewModel.signInManual("", "")

            val errorState = awaitItem() as AuthState.Error
            assertEquals("Vui lòng nhập đầy đủ thông tin", errorState.message)
            coVerify(exactly = 0) { mockAuthRepository.signInManual(any(), any()) }

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `onSignUp - Success flow when username and email are available`() = runTest(testDispatcher) {
        val availabilityResponse = AvailabilityResponse(usernameTaken = false, emailTaken = false)
        coEvery { mockAuthRepository.checkAvailability(any(), any()) } returns Result.success(availabilityResponse)
        coEvery { mockAuthRepository.signUpManual(any(), any(), any(), any(), any()) } returns Result.success(Unit)

        viewModel.authState.test {
            assertEquals(AuthState.Idle, awaitItem())

            viewModel.onSignUp("test@example.com", "Pass123", "Name", "user123", "0912345678")

            assertEquals(AuthState.Loading, awaitItem())
            assertEquals(AuthState.Success, awaitItem())

            coVerify { mockAuthRepository.signUpManual("test@example.com", "Pass123", "Name", "user123", "0912345678") }
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `onSignUp - Fails when email already taken`() = runTest(testDispatcher) {
        val availabilityResponse = AvailabilityResponse(usernameTaken = false, emailTaken = true)
        coEvery { mockAuthRepository.checkAvailability(any(), any()) } returns Result.success(availabilityResponse)

        viewModel.authState.test {
            assertEquals(AuthState.Idle, awaitItem())

            viewModel.onSignUp("test@example.com", "Pass123", "Name", "user123", "0912345678")

            assertEquals(AuthState.Loading, awaitItem())
            val errorState = awaitItem() as AuthState.Error
            assertTrue(errorState.message.contains("Email"))

            coVerify(exactly = 0) { mockAuthRepository.signUpManual(any(), any(), any(), any(), any()) }
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `logout - Reset status`() = runTest(testDispatcher) {
        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockAuthRepository.signOut() }
        assertEquals(AuthState.Idle, viewModel.authState.value)
        assertFalse(viewModel.hasShownWelcomeToast.value)
    }
}
