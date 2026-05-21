package com.example.smartpick.core.network

import android.util.Log
import com.example.smartpick.BuildConfig
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import kotlin.time.Duration.Companion.seconds
import io.github.jan.supabase.functions.Functions

object SupabaseProvider {
    @OptIn(SupabaseInternal::class)
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        httpEngine = OkHttp.create {
            addInterceptor { chain ->
                val request = chain.request()
                try {
                    chain.proceed(request)
                } catch (e: Exception) {
                    Log.e("NetworkDebug", "Failing URL: ${request.url}")
                    throw e
                }
            }
        }
        install(Auth)
        install(Storage)
        install(Functions)
        install(Realtime) {
            // Cấu hình Realtime
            // Tùy chọn: Tự động kết nối lại nếu mất mạng
            reconnectDelay = 5.seconds
        }
        install(Postgrest) {
            // Cấu hình serializer để xử lý linh hoạt hơn
            serializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true // Ép kiểu về default nếu gặp null
                encodeDefaults = true
            })
        }

        httpConfig {
            install(HttpTimeout) {
                requestTimeoutMillis = 30000 // Tăng lên 30 giây
                connectTimeoutMillis = 30000
            }
        }
    }
}