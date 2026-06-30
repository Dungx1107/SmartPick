package com.example.smartpick.features.post_creation.data

import com.example.smartpick.core.network.ModerationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LmStudioModerator @Inject constructor(
    @Named("LlmClient") private val client: OkHttpClient
) {
//    private val lmStudioUrl = "http://10.11.238.87:1234/v1/chat/completions"
    private val lmStudioUrl = "http://192.168.0.125:1234/v1/chat/completions"

    /**
     * Kiểm duyệt nội dung văn bản (Gồm nội dung bài viết và tên sản phẩm).
     * Nếu phát hiện vi phạm sẽ ném ra ModerationException để ViewModel xử lý UI.
     */
    fun validateText(content: String, productName: String?) {
        val textToValidate = buildString {
            if (content.isNotBlank()) append(content).append(" ")
            productName?.let { if (it.isNotBlank()) append(it) }
        }.trim()

        if (textToValidate.isEmpty()) return

        try {
            val jsonRequest = JSONObject().apply {
//                put("model", "qwen3-coder-30b")
                put("model", "qwen-3-14b-instruct")
                put("temperature", 0.0)
                put("max_tokens", 50)

                val messagesArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "Bạn là một hệ thống kiểm duyệt nội dung tự động cho mạng xã hội mua sắm. Nhiệm vụ của bạn là kiểm tra văn bản của người dùng có chứa từ ngữ tục tĩu, xúc phạm, lừa đảo, phản động hoặc vi phạm thuần phong mỹ tục Việt Nam hay không. Chỉ trả về duy nhất một từ 'SAFE' nếu an toàn, hoặc 'UNSAFE' nếu vi phạm. Tuyệt đối không giải thích, không thêm từ ngữ nào khác.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", textToValidate)
                    })
                }
                put("messages", messagesArray)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonRequest.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(lmStudioUrl)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw ModerationException("Không thể kết nối đến hệ thống kiểm duyệt (Mã lỗi: ${response.code}).")
                }

                val responseBody = response.body?.string() ?: throw ModerationException("Hệ thống kiểm duyệt trả về dữ liệu rỗng.")

                android.util.Log.d("LM_STUDIO_RESPONSE", "JSON nhận về: $responseBody")

                val jsonResponse = JSONObject(responseBody)
                val choices = jsonResponse.optJSONArray("choices")

                if (choices != null && choices.length() > 0) {
                    val message = choices.getJSONObject(0).optJSONObject("message")
                    val llmResult = message?.optString("content")?.trim()?.uppercase() ?: ""

                    android.util.Log.d("LM_STUDIO_RESPONSE", "Từ khóa Model phản hồi: $llmResult")

                    if (llmResult == "UNSAFE" || (llmResult.contains("UNSAFE") && !llmResult.startsWith("SAFE"))) {
                        throw ModerationException("Nội dung bài viết hoặc thông tin sản phẩm chứa từ ngữ vi phạm tiêu chuẩn cộng đồng.")
                    }
                }
            }
        } catch (e: ModerationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            throw ModerationException("Lỗi hệ thống trong quá trình kiểm duyệt nội dung.")
        }
    }
}