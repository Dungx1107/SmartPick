package com.example.smartpick.test

import android.util.Log
import com.example.smartpick.BuildConfig
import com.example.smartpick.data.remote.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

object SupabaseTest {

    suspend fun testConnection() {
        try {
            val response = SupabaseClient.supabaseClient
                .from("test") // nhớ tạo bảng này trong Supabase
                .select()

            println("✅ Kết nối OK: $response")

        } catch (e: Exception) {
            println("❌ Lỗi: ${e.message}")
            Log.d("SUPABASE_DEBUG", BuildConfig.SUPABASE_URL)
        }
    }
}