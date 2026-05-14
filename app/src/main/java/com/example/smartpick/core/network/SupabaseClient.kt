package com.example.smartpick.core.network

import com.example.smartpick.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json

object SupabaseClient {
    val supabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Auth)
        install(Storage)
        install(Postgrest){
            // Cấu hình serializer để xử lý linh hoạt hơn
            serializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true // Ép kiểu về default nếu gặp null
                encodeDefaults = true
            })
        }
    }
}