package com.example.smartpick.core.network

import com.example.smartpick.BuildConfig
import com.example.smartpick.core.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Exception dùng cho các lỗi liên quan tới kiểm duyệt nội dung.
 */
class ModerationException(message: String) : Exception(message)

/**
 * Service xử lý kiểm duyệt:
 * - Text bằng Gemini API
 * - Image bằng Sightengine API
 */
@Singleton
class ModerationService @Inject constructor(
    @Named("LlmClient") private val client: OkHttpClient
) {

    companion object {
        private const val TAG = "ModerationService"
    }

    /* API Key Gemini */
    private val geminiApiKey = BuildConfig.GEMINI_KEY

    /* Key Sightengine */
    private val sightengineUser = BuildConfig.SIGHTENGINE_USER
    private val sightengineSecret = BuildConfig.SIGHTENGINE_SECRET

    /**
     * Kiểm duyệt nội dung text bằng Gemini AI.
     *
     * @param text Nội dung cần kiểm tra
     *
     * @return
     * true  -> an toàn
     * false -> độc hại
     */
    suspend fun checkTextContent(text: String): Boolean = withContext(Dispatchers.IO) {

        if (text.isBlank()) {
            Logger.e(TAG, "Nội dung text rỗng, không cần kiểm duyệt.")
            return@withContext false
        }

        val startTime = System.currentTimeMillis()

        Logger.d(TAG, "======================================")
        Logger.d(TAG, "BẮT ĐẦU KIỂM DUYỆT TEXT")
        Logger.d(TAG, "Text nhận được: $text")

        /* Check API key */
        if (geminiApiKey.isBlank()) {

            Logger.e(TAG, "GEMINI API KEY BỊ RỖNG")

            return@withContext false
        }

        Logger.d(TAG, "Gemini API Key tồn tại")
        Logger.d(TAG, "Gemini Key Length = ${geminiApiKey.length}")

        /* Endpoint Gemini API */
        val url =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=$geminiApiKey"

        Logger.d(TAG, "Gemini Endpoint đã được tạo")

        /**
         * Prompt yêu cầu AI chỉ trả về:
         * SAFE hoặc TOXIC
         */
        val prompt = """
            Bạn là một hệ thống kiểm duyệt bình luận chuyên nghiệp. 
            Nhiệm vụ: Kiểm tra câu sau có chứa từ ngữ chửi thề, thô tục, xúc phạm hoặc bạo lực bằng tiếng Việt hay bằng bất kì ngôn ngữ nào (bao gồm cả teencode, viết tắt) hay không.
            Nếu câu văn AN TOÀN, chỉ trả về đúng 1 chữ: SAFE.
            Nếu câu văn ĐỘC HẠI, chỉ trả về đúng 1 chữ: TOXIC.
            Không giải thích gì thêm.
            Câu cần kiểm tra: "$text"
        """.trimIndent()

        Logger.d(TAG, "Prompt đã tạo")
        Logger.d(TAG, "Prompt Length = ${prompt.length}")

        /* JSON body gửi lên Gemini */
        val jsonBody = JSONObject().apply {
            put(
                "contents",
                JSONArray().put(
                    JSONObject().put(
                        "parts",
                        JSONArray().put(
                            JSONObject().put("text", prompt)
                        )
                    )
                )
            )
        }

        Logger.d(TAG, "JSON Body đã tạo")

        /* Convert sang RequestBody */
        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        Logger.d(TAG, "RequestBody đã convert thành công")

        /* Tạo POST request */
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        Logger.d(TAG, "POST Request đã build")

        try {

            /**
             * Gửi request tới Gemini
             * và parse kết quả trả về.
             */
            Logger.d(TAG, "ĐANG GỬI REQUEST GEMINI...")

            client.newCall(request).execute().use { response ->

                Logger.d(TAG, "ĐÃ NHẬN RESPONSE GEMINI")

                val body = response.body?.string() ?: ""

                Logger.d(TAG, "HTTP CODE = ${response.code}")
                Logger.d(TAG, "Response Successful = ${response.isSuccessful}")
                Logger.d(TAG, "Raw Response Length = ${body.length}")

                /* Nếu API lỗi thì reject */
                if (!response.isSuccessful) {

                    Logger.e(TAG, "REQUEST GEMINI THẤT BẠI")

                    when (response.code) {

                        400 -> Logger.e(TAG, "BAD REQUEST / API KEY INVALID")

                        401 -> Logger.e(TAG, "UNAUTHORIZED")

                        403 -> Logger.e(TAG, "FORBIDDEN")

                        429 -> Logger.e(TAG, "RATE LIMIT EXCEEDED")

                        500 -> Logger.e(TAG, "GEMINI SERVER ERROR")
                    }

                    Logger.e(TAG, "RAW ERROR BODY:")
                    Logger.e(TAG, body)

                    return@withContext false
                }

                Logger.d(TAG, "BẮT ĐẦU PARSE JSON RESPONSE")

                val responseJson = JSONObject(body)

                /**
                 * Lấy nội dung phản hồi từ AI.
                 */
                val aiReply = responseJson
                    .optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")
                    ?.trim()
                    ?.uppercase()
                    ?: "ERROR"

                Logger.d(TAG, "KẾT QUẢ AI = $aiReply")

                val isSafe = aiReply.trim().uppercase() == "SAFE"
                Logger.d(TAG, "TEXT SAFE = $isSafe")

                val endTime = System.currentTimeMillis()

                Logger.d(TAG, "THỜI GIAN XỬ LÝ = ${endTime - startTime} ms")

                Logger.d(TAG, "KẾT THÚC KIỂM DUYỆT TEXT")
                Logger.d(TAG, "======================================")

                return@withContext isSafe

            }

        } catch (e: Exception) {

            Logger.e(TAG, "EXCEPTION GEMINI")

            Logger.e(TAG, "Exception Type = ${e::class.java.simpleName}")
            Logger.e(TAG, "Message = ${e.message}")
            Logger.e(TAG, "Cause = ${e.cause}")

            e.printStackTrace()

            return@withContext false
        }
    }

    /**
     * Kiểm duyệt nội dung ảnh bằng Sightengine API.
     *
     * Các tiêu chí:
     * - Nudity
     * - Violence
     * - Gore
     * - Offensive
     * - Weapon
     *
     * @param imageUrl URL ảnh cần kiểm tra
     *
     * @return
     * true  -> ảnh an toàn
     * false -> ảnh nhạy cảm
     */
    suspend fun checkImageContent(imageUrl: String): Boolean = withContext(Dispatchers.IO) {

        val startTime = System.currentTimeMillis()

        Logger.d(TAG, "======================================")
        Logger.d(TAG, "BẮT ĐẦU KIỂM DUYỆT ẢNH")
        Logger.d(TAG, "Image URL: $imageUrl")

        if (sightengineUser.isBlank() || sightengineSecret.isBlank()) {

            Logger.e(TAG, "SIGHTENGINE KEY BỊ RỖNG")

            return@withContext false
        }

        Logger.d(TAG, "Sightengine credentials OK")
        Logger.d(TAG, "User Length = ${sightengineUser.length}")
        Logger.d(TAG, "Secret Length = ${sightengineSecret.length}")

        /* Build URL request */
        val url = "https://api.sightengine.com/1.0/check.json"
            .toHttpUrlOrNull()!!
            .newBuilder()
            .addQueryParameter("url", imageUrl)
            .addQueryParameter(
                "models",
                "nudity-2.0,wad,offensive,gore,violence"
            )
            .addQueryParameter("api_user", sightengineUser)
            .addQueryParameter("api_secret", sightengineSecret)
            .build()

        Logger.d(TAG, "REQUEST URL CREATED")
        Logger.d(TAG, "api_user exists = ${sightengineUser.isNotBlank()}")
        Logger.d(TAG, "api_secret exists = ${sightengineSecret.isNotBlank()}")

        /* Tạo GET request */
        val request = Request.Builder()
            .url(url)
            .build()

        Logger.d(TAG, "GET Request đã build")

        try {

            /**
             * Gửi request tới Sightengine
             * và phân tích kết quả moderation.
             */
            Logger.d(TAG, "ĐANG GỬI REQUEST SIGHTENGINE...")

            client.newCall(request).execute().use { response ->

                Logger.d(TAG, "ĐÃ NHẬN RESPONSE SIGHTENGINE")

                val body = response.body?.string() ?: ""

                Logger.d(TAG, "HTTP CODE = ${response.code}")
                Logger.d(TAG, "Response Successful = ${response.isSuccessful}")
                Logger.d(TAG, "Raw Response Length = ${body.length}")

                /* Nếu API lỗi */
                if (!response.isSuccessful) {

                    Logger.e(TAG, "REQUEST SIGHTENGINE THẤT BẠI")

                    when (response.code) {

                        401 -> Logger.e(TAG, "INVALID API CREDENTIAL")

                        403 -> Logger.e(TAG, "ACCESS FORBIDDEN")

                        429 -> Logger.e(TAG, "RATE LIMIT EXCEEDED")

                        500 -> Logger.e(TAG, "SIGHTENGINE SERVER ERROR")
                    }

                    Logger.e(TAG, "RAW ERROR BODY:")
                    Logger.e(TAG, body)

                    return@withContext false
                }

                Logger.d(TAG, "BẮT ĐẦU PARSE JSON RESPONSE")

                val json = JSONObject(body)

                val status = json.optString("status")

                Logger.d(TAG, "STATUS = $status")

                if (status != "success") {

                    Logger.e(TAG, "SIGHTENGINE STATUS FAILURE")

                    val error = json.optJSONObject("error")

                    Logger.e(TAG, "ERROR JSON = ${error.toString()}")

                    return@withContext false
                }

                if (!json.has("weapon") || !json.has("nudity") || !json.has("gore")) {
                    Logger.e(TAG, "JSON Sightengine thiếu các trường dữ liệu quan trọng")
                    return@withContext false
                }
                /* Điểm weapon */
                val weaponScore =
                    json.optJSONObject("weapon")
                        ?.optDouble("prob", 0.0)
                        ?: 0.0

                /* Điểm gore */
                val goreScore =
                    json.optJSONObject("gore")
                        ?.optDouble("prob", 0.0)
                        ?: 0.0

                /* Điểm offensive */
                val offensiveScore =
                    json.optJSONObject("offensive")
                        ?.optDouble("prob", 0.0)
                        ?: 0.0

                /* Điểm violence */
                val violenceScore =
                    json.optJSONObject("violence")
                        ?.optDouble("prob", 0.0)
                        ?: 0.0

                /* Điểm non-nudity */
                val nudityNoneScore =
                    json.optJSONObject("nudity")
                        ?.optDouble("none", 1.0)
                        ?: 1.0

                /* Nếu none < 0.5 => có khả năng nudity */
                val isNudity = nudityNoneScore < 0.5

                Logger.d(TAG, "weaponScore = $weaponScore")
                Logger.d(TAG, "goreScore = $goreScore")
                Logger.d(TAG, "offensiveScore = $offensiveScore")
                Logger.d(TAG, "violenceScore = $violenceScore")
                Logger.d(TAG, "nudityNoneScore = $nudityNoneScore")
                Logger.d(TAG, "isNudity = $isNudity")

                val isUnsafe =
                    weaponScore > 0.5 ||
                            goreScore > 0.5 ||
                            offensiveScore > 0.5 ||
                            violenceScore > 0.5 ||
                            isNudity

                Logger.d(TAG, "IMAGE UNSAFE = $isUnsafe")

                val endTime = System.currentTimeMillis()

                Logger.d(TAG, "THỜI GIAN XỬ LÝ = ${endTime - startTime} ms")

                Logger.d(TAG, "KẾT THÚC KIỂM DUYỆT ẢNH")
                Logger.d(TAG, "======================================")

                return@withContext !isUnsafe
            }

        } catch (e: Exception) {

            Logger.e(TAG, "EXCEPTION SIGHTENGINE")

            Logger.e(TAG, "Exception Type = ${e::class.java.simpleName}")
            Logger.e(TAG, "Message = ${e.message}")
            Logger.e(TAG, "Cause = ${e.cause}")

            e.printStackTrace()

            return@withContext false
        }
    }
}