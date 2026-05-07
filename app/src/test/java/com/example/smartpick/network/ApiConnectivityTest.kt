package com.example.smartpick.network

import com.example.smartpick.BuildConfig
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test

class ApiConnectivityTest {

    private val client = OkHttpClient()

    @Test
    fun checkAllApiKeys() = runBlocking {
        println("\n" + "=" .repeat(70))
        println("BẮT ĐẦU KIỂM TRA TOÀN BỘ API KEYS TRONG LOCAL.PROPERTIES")
        println("=" .repeat(70))

        // 1. Kiểm tra Gemini Key
        checkGeminiKey(BuildConfig.GEMINI_KEY)

        // 2. Kiểm tra Sightengine Credentials
        checkSightengineKey(BuildConfig.SIGHTENGINE_USER, BuildConfig.SIGHTENGINE_SECRET)

        // 3. Kiểm tra Supabase Key (Dựa trên URL và Key trong BuildConfig)
        checkSupabaseKey(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_KEY)

        println("\n" + "=".repeat(70))
        println("KẾT THÚC KIỂM TRA")
        println("=".repeat(70))
    }

    private fun checkGeminiKey(key: String) {
        println("\n[1] ĐANG KIỂM TRA GEMINI API...")
        if (key.isBlank() || key == "null") {
            println("❌ LỖI: Key đang bị trống hoặc mang giá trị null. Hãy check lại file local.properties.")
            return
        }

        val url = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=$key"
        val jsonBody = "{ \"contents\": [{ \"parts\":[{ \"text\": \"Ping\" }] }] }"
        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(requestBody).build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    println("✅ THÀNH CÔNG: Gemini Key chuẩn.")
                } else {
                    println("❌ THẤT BẠI: Gemini Key sai hoặc bị từ chối.")
                    println("   Mã lỗi: ${response.code}")
                    println("   Phản hồi: ${response.body?.string()?.take(100)}...")
                }
            }
        } catch (e: Exception) {
            println("❌ LỖI KẾT NỐI: ${e.message}")
        }
    }

    private fun checkSightengineKey(user: String, secret: String) {
        println("\n[2] ĐANG KIỂM TRA SIGHTENGINE API...")
        if (user.isBlank() || secret.isBlank()) {
            println("❌ LỖI: User ID hoặc Secret đang bị rỗng.")
            return
        }

        // Sử dụng ảnh mẫu của Sightengine để test kết nối
        val url = "https://api.sightengine.com/1.0/check.json?url=https://sightengine.com/assets/img/examples/example7.jpg&models=properties&api_user=$user&api_secret=$secret"
        val request = Request.Builder().url(url).build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful && body.contains("\"status\":\"success\"")) {
                    println("✅ THÀNH CÔNG: Sightengine Credentials chuẩn.")
                } else {
                    println("❌ THẤT BẠI: Thông tin Sightengine không chính xác.")
                    println("   Phản hồi: $body")
                }
            }
        } catch (e: Exception) {
            println("❌ LỖI KẾT NỐI: ${e.message}")
        }
    }

    private fun checkSupabaseKey(baseUrl: String, key: String) {
        println("\n[3] ĐANG KIỂM TRA SUPABASE KẾT NỐI...")
        if (baseUrl.isBlank() || key.isBlank()) {
            println("❌ LỖI: URL hoặc Key Supabase bị rỗng.")
            return
        }

        // Gọi thử endpoint auth của Supabase (endpoint tối giản nhất)
        val url = "$baseUrl/auth/v1/health"
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", key)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    println("✅ THÀNH CÔNG: Supabase URL và Key chuẩn.")
                } else {
                    println("❌ THẤT BẠI: Không thể xác thực với Supabase.")
                    println("   Mã lỗi: ${response.code}")
                }
            }
        } catch (e: Exception) {
            println("❌ LỖI KẾT NỐI: ${e.message}")
        }
    }
}