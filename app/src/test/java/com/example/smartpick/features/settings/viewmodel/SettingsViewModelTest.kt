package com.example.smartpick.features.settings.viewmodel

import com.example.smartpick.BaseUnitTest
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.settings.data.LanguageRepository
import com.example.smartpick.features.settings.data.ThemeRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val isDarkModeFlow = MutableStateFlow(false)
    private val languageFlow = MutableStateFlow("vi")

    private lateinit var mockThemeRepository: ThemeRepository
    private lateinit var mockLanguageRepository: LanguageRepository
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockThemeRepository = mockk(relaxed = true)
        mockLanguageRepository = mockk(relaxed = true)
        mockAuthRepository = mockk(relaxed = true)

        every { mockThemeRepository.isDarkMode } returns isDarkModeFlow
        every { mockLanguageRepository.language } returns languageFlow

        viewModel = SettingsViewModel(mockThemeRepository, mockLanguageRepository, mockAuthRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Reads values from repositories`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.isDarkMode.value)
        assertEquals("vi", viewModel.currentLanguage.value)
    }

    @Test
    fun `toggleTheme - Calls repository`() = runTest(testDispatcher) {
        viewModel.toggleTheme(true)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockThemeRepository.toggleTheme(true) }
    }

    @Test
    fun `setLanguage - Calls repository`() = runTest(testDispatcher) {
        viewModel.setLanguage("en")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockLanguageRepository.setLanguage("en") }
    }

    @Test
    fun `logout - Calls authRepository and triggers callback`() = runTest(testDispatcher) {
        var successCalled = false
        viewModel.logout {
            successCalled = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successCalled)
        coVerify { mockAuthRepository.signOut() }
    }
}
