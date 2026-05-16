package com.example.smartpick.features.chatbot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.BuildConfig
import com.example.smartpick.core.model.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor() : ViewModel() {

    // Khởi tạo model Gemini-1.5-flash siêu tốc độ
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_KEY,
        systemInstruction = content {
            text("Bạn là AI Curator của SmartPick, một chuyên gia tư vấn mua sắm am hiểu công nghệ và thời trang. " +
                    "Nhiệm vụ của bạn là giúp người dùng chọn đồ, mix-match trang phục hoặc tìm kiếm sản phẩm. " +
                    "Hãy trả lời một cách thân thiện, chuyên nghiệp, ngắn gọn và có sử dụng emoji phù hợp.")
        }
    )

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("Chào bạn! Tôi là AI Curator của SmartPick ✨. Hôm nay tôi có thể giúp gì cho phong cách của bạn?", isFromUser = false)
    ))
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        // 1. Thêm tin nhắn của User
        val userMessage = ChatMessage(prompt, isFromUser = true)
        _chatHistory.value = _chatHistory.value + userMessage

        // 2. Thêm tin nhắn Loading ảo của AI
        val loadingMessage = ChatMessage("", isFromUser = false, isLoading = true)
        _chatHistory.value = _chatHistory.value + loadingMessage

        viewModelScope.launch {
            try {
                // 3. Gửi cho Gemini xử lý
                val response = generativeModel.generateContent(prompt)
                val aiText = response.text ?: "Xin lỗi, tôi không thể xử lý yêu cầu này lúc này."

                // 4. Xóa tin nhắn Loading và hiện kết quả thật
                _chatHistory.value = _chatHistory.value.filter { !it.isLoading } + ChatMessage(aiText, isFromUser = false)
            } catch (e: Exception) {
                // Xử lý lỗi (Vd: Chưa nhập API Key, mất mạng)
                _chatHistory.value = _chatHistory.value.filter { !it.isLoading } +
                        ChatMessage("Đã xảy ra lỗi kết nối: ${e.message}", isFromUser = false)
            }
        }
    }
}