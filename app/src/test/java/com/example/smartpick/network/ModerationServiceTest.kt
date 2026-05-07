package com.example.smartpick.network

import com.example.smartpick.core.network.ModerationService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ModerationServiceTest {

    private lateinit var client: OkHttpClient
    private lateinit var service: ModerationService
    private lateinit var mockCall: Call

    @Before
    fun setup() {
        client = mockk()
        mockCall = mockk()
        service = ModerationService(client)
    }

    // --- HELPER FORMATTING LOG ---
    private fun printTestHeader(name: String) {
        println("\n" + "=".repeat(60))
        println("TEST CASE: $name")
        println("-".repeat(60))
    }

    private fun printResult(content: String, expected: Any, actual: Any, error: String? = null) {
        println("Nội dung test: $content")
        println("Kết quả mong đợi: $expected")
        println("Kết quả thực tế : $actual")
        if (error != null) println("Lỗi ghi nhận   : $error")
        println("=".repeat(60))
    }

    private fun createMockResponse(code: Int, body: String): Response {
        return Response.Builder()
            .request(Request.Builder().url("https://api.test.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code == 200) "OK" else "Error")
            .body(body.toResponseBody("application/json".toMediaTypeOrNull()))
            .build()
    }

    // ============================================================
    // PHẦN 1: UNIT TESTS (MOCK LOGIC)
    // ============================================================

    @Test
    fun testText_EmptyString_ReturnsFalse() = runTest {
        printTestHeader("Xử lý chuỗi rỗng (Empty String)")

        // Không cần every { client.newCall } vì code sẽ return ngay lập tức
        val result = service.checkTextContent("   ") // Test với khoảng trắng

        printResult("'   ' (Khoảng trắng)", false, result, "Code phải return false ngay lập tức mà không gọi API")

        assertFalse(result)
        // Xác nhận là không có bất kỳ cuộc gọi mạng nào được thực hiện
        verify(exactly = 0) { client.newCall(any()) }
    }

    @Test
    fun testText_Gemini_MalformedJson_ReturnsFalse() = runTest {
        printTestHeader("Xử lý JSON sai định dạng từ Gemini")
        // Giả lập JSON không có trường "candidates"
        val malformedJson = "{\"status\": \"OK\"}"
        val response = createMockResponse(200, malformedJson)

        every { client.newCall(any()) } returns mockCall
        every { mockCall.execute() } returns response

        val result = service.checkTextContent("Hello")

        // Thực tế aiReply sẽ là "ERROR", và ERROR != SAFE nên result = false
        printResult("Hello", false, result, "JSON thiếu candidates -> aiReply=ERROR -> false")
        assertFalse(result)
    }

    @Test
    fun testImage_Sightengine_PartialData_ReturnsFalse() = runTest {
        printTestHeader("Xử lý JSON thiếu field từ Sightengine")
        val partialJson = "{\"status\": \"success\"}" // Thiếu các object weapon, nudity...
        val response = createMockResponse(200, partialJson)

        every { client.newCall(any()) } returns mockCall
        every { mockCall.execute() } returns response

        val result = service.checkImageContent("https://test.com/img.jpg")
        printResult("URL ảnh", false, result, "Thiếu dữ liệu điểm số trong JSON")
        assertFalse(result)
    }

    // ============================================================
    // PHẦN 2: INTEGRATION TESTS (API THẬT - CONNECTIVITY CHECK)
    // ============================================================

    @Test
    fun check_Gemini_Connectivity_And_Key() = runBlocking {
        printTestHeader("KIỂM TRA KẾT NỐI & KEY GEMINI")
        val realClient = OkHttpClient()
        val realService = ModerationService(realClient)

        try {
            val isSafe = realService.checkTextContent("Ping")
            // Nếu Key sai, service sẽ trả về false do dính lỗi 400
            if (!isSafe) {
                println("⚠️ CẢNH BÁO: Không thể xác thực Gemini. Kiểm tra lại API Key.")
            } else {
                println("✅ Gemini API: Kết nối tốt.")
            }
        } catch (e: Exception) {
            println("❌ LỖI KẾT NỐI: ${e.message}")
        }
        println("=".repeat(60))
    }

    @Test
    fun check_Sightengine_Connectivity_And_Key() = runBlocking {
        printTestHeader("KIỂM TRA KẾT NỐI & KEY SIGHTENGINE")
        val realClient = OkHttpClient()
        val realService = ModerationService(realClient)

        // Sử dụng một ảnh mẫu (standard) để tránh lỗi usage_limit của bản Demo
        val sampleUrl = "https://sightengine.com/assets/img/examples/example7.jpg"

        try {
            val isSafe = realService.checkImageContent(sampleUrl)
            if (!isSafe) {
                println("⚠️ CẢNH BÁO: Sightengine trả về False hoặc Lỗi xác thực.")
            } else {
                println("✅ Sightengine API: Kết nối tốt.")
            }
        } catch (e: Exception) {
            println("❌ LỖI KẾT NỐI: ${e.message}")
        }
        println("=".repeat(60))
    }

    @Test
    fun testImage_Sightengine_MissingFields_ReturnsFalse() = runTest {
        printTestHeader("Xử lý JSON thiếu field quan trọng từ Sightengine")
        // JSON có status success nhưng thiếu object "weapon"
        val missingFieldJson = """
        {
            "status": "success",
            "nudity": {"none": 0.99}
        }
    """.trimIndent()
        val response = createMockResponse(200, missingFieldJson)

        every { client.newCall(any()) } returns mockCall
        every { mockCall.execute() } returns response

        val result = service.checkImageContent("https://test.com/img.jpg")
        printResult("URL ảnh", false, result, "Thiếu field 'weapon' -> Chặn để đảm bảo an toàn")
        assertFalse(result)
    }

    // ============================================================
    // PHẦN 3: PLAYGROUND (KIỂM TRA NỘI DUNG THỰC TẾ)
    // ============================================================

    @Test
    fun playground_FullModeration_Flow() = runBlocking {
        val realClient = OkHttpClient()
        val realService = ModerationService(realClient)

        val testInputs = listOf(
            "Chúc bạn một ngày tốt lành!" to "SAFE",
            "Đồ ngu ngốc" to "TOXIC",
            "   " to "EMPTY/INVALID"
        )

        println("\n" + "=".repeat(80))
        println(String.format("| %-30s | %-15s | %-15s |", "NỘI DUNG TEST", "KỲ VỌNG", "THỰC TẾ (AI)"))
        println("-".repeat(80))

        for ((input, expected) in testInputs) {
            val isSafe = realService.checkTextContent(input)
            val actual = if (isSafe) "SAFE" else "TOXIC/BLOCKED"

            println(String.format("| %-30s | %-15s | %-15s |",
                input.ifBlank { "(Trống)" }, expected, actual))
        }
        println("=".repeat(80))
    }
}