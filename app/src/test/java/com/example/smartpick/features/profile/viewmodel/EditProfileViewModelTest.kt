package com.example.smartpick.features.profile.viewmodel

import android.content.Context
import android.net.Uri
import app.cash.turbine.test
import com.example.smartpick.BaseUnitTest
import com.example.smartpick.features.profile.data.UserProfileRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockUserProfileRepository: UserProfileRepository
    private lateinit var mockContext: Context
    private lateinit var viewModel: EditProfileViewModel

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockUserProfileRepository = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)

        viewModel = EditProfileViewModel(mockUserProfileRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Idle`() {
        assertFalse(viewModel.isUploading.value)
        assertNull(viewModel.selectedImage.value)
    }

    @Test
    fun `updateSelectedImage - Updates state`() {
        val mockUri = mockk<Uri>()
        viewModel.updateSelectedImage(mockUri)
        assertEquals(mockUri, viewModel.selectedImage.value)
    }

    @Test
    fun `saveProfile - Success flow without changing image`() = runTest(testDispatcher) {
        coEvery {
            mockUserProfileRepository.updateUserProfile(any(), any(), any(), any(), any(), any())
        } returns Unit

        var successNewAvatar: String? = null
        viewModel.isUploading.test {
            assertEquals(false, awaitItem())

            viewModel.saveProfile(
                userId = "user123",
                name = "John",
                username = "john123",
                phone = "0912345678",
                email = "john@example.com",
                context = mockContext,
                currentAvatarUrl = "http://avatar.com/old.jpg",
                onSuccess = { successNewAvatar = it },
                onError = { fail("Should not fail") }
            )

            assertEquals(true, awaitItem())
            assertEquals(false, awaitItem())
            ensureAllEventsConsumed()
        }

        assertEquals("http://avatar.com/old.jpg", successNewAvatar)
        coVerify {
            mockUserProfileRepository.updateUserProfile(
                userId = "user123",
                avatarUrl = "http://avatar.com/old.jpg",
                fullName = "John",
                username = "john123",
                phone = "0912345678",
                email = "john@example.com"
            )
        }
    }

    @Test
    fun `saveProfile - Failure flow`() = runTest(testDispatcher) {
        coEvery {
            mockUserProfileRepository.updateUserProfile(any(), any(), any(), any(), any(), any())
        } throws Exception("Update failed")

        var errorMsg: String? = null
        viewModel.saveProfile(
            userId = "user123",
            name = "John",
            username = "john123",
            phone = "0912345678",
            email = "john@example.com",
            context = mockContext,
            currentAvatarUrl = "http://avatar.com/old.jpg",
            onSuccess = { fail("Should not succeed") },
            onError = { errorMsg = it }
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Update failed", errorMsg)
    }
}
