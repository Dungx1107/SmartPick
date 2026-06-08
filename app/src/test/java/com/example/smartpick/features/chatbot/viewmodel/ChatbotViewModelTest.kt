package com.example.smartpick.features.chatbot.viewmodel

import app.cash.turbine.test
import com.example.smartpick.BaseUnitTest
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatbotViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ChatbotViewModel
    private lateinit var mockGenerativeModel: GenerativeModel

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockGenerativeModel = mockk(relaxed = true)

        viewModel = ChatbotViewModel()
        setInstanceFieldUsingUnsafe(viewModel, "generativeModel", mockGenerativeModel)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Contains initial welcome message`() = runTest(testDispatcher) {
        val history = viewModel.chatHistory.value
        assertEquals(1, history.size)
        assertFalse(history[0].isFromUser)
        assertTrue(history[0].text.contains("AI Curator"))
    }

    @Test
    fun `sendMessage - Success flow`() = runTest(testDispatcher) {
        val mockResponse = mockk<GenerateContentResponse>()
        every { mockResponse.text } returns "Đây là câu trả lời từ Gemini!"
        coEvery { mockGenerativeModel.generateContent(any<String>()) } returns mockResponse

        viewModel.chatHistory.test {
            // Trạng thái ban đầu (Welcome message)
            val initial = awaitItem()
            assertEquals(1, initial.size)

            viewModel.sendMessage("Tư vấn áo thun")

            // Sau khi gửi: Thêm tin nhắn của User (2 items)
            val userMsgState = awaitItem()
            assertEquals(2, userMsgState.size)
            assertEquals("Tư vấn áo thun", userMsgState[1].text)
            assertTrue(userMsgState[1].isFromUser)

            // Tiếp theo: Thêm tin nhắn Loading ảo của AI (3 items)
            val loadingState = awaitItem()
            assertEquals(3, loadingState.size)
            assertTrue(loadingState[2].isLoading)

            // Cuối cùng: Xóa loading và thay bằng tin nhắn phản hồi thật (3 items)
            val successState = awaitItem()
            assertEquals(3, successState.size)
            assertFalse(successState[2].isLoading)
            assertEquals("Đây là câu trả lời từ Gemini!", successState[2].text)

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `sendMessage - Blank input - Returns immediately without calling API`() = runTest(testDispatcher) {
        viewModel.sendMessage("   ")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.chatHistory.value.size) // Vẫn chỉ có welcome message
        coVerify(exactly = 0) { mockGenerativeModel.generateContent(any<String>()) }
    }

    @Test
    fun `sendMessage - Exception flow - Displays error message`() = runTest(testDispatcher) {
        coEvery { mockGenerativeModel.generateContent(any<String>()) } throws Exception("Quá hạn mức API Key")

        viewModel.chatHistory.test {
            awaitItem() // Welcome

            viewModel.sendMessage("Tư vấn giày")
            
            awaitItem() // User Msg
            awaitItem() // Loading

            // Success state thay thế loading bằng thông báo lỗi
            val errorState = awaitItem()
            assertEquals(3, errorState.size)
            assertFalse(errorState[2].isLoading)
            assertTrue(errorState[2].text.contains("Đã xảy ra lỗi kết nối: Quá hạn mức API Key"))

            ensureAllEventsConsumed()
        }
    }
}
