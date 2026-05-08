package com.example.smartpick.core.utils

import android.util.Log
import com.example.smartpick.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object EmailHelper {

    private val client = OkHttpClient()

    enum class EmailType(val value: String) {
        WELCOME("welcome"),
        LOGIN_GOOGLE("login_google"),
        LOGIN_MANUAL("login_manual")
    }

    suspend fun send(
        email: String,
        type: EmailType,
        name: String = ""
    ) {
        if (email.isBlank()) return

        withContext(Dispatchers.IO) {
            try {
                val body = JSONObject().apply {
                    put("email", email)
                    put("name", name)
                    put("type", type.value)
                }.toString().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("${BuildConfig.SUPABASE_URL}/functions/v1/send-app-email")
                    .post(body)
                    .addHeader("Authorization", "Bearer ${BuildConfig.SUPABASE_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                Log.d("EmailHelper", "✅ [${type.value}] → $email | ${response.code}")

            } catch (e: Exception) {
                Log.e("EmailHelper", "❌ Failed: ${e.message}")
            }
        }
    }
}