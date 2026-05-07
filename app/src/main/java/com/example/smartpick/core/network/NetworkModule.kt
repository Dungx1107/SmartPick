package com.example.smartpick.core.network

import com.example.smartpick.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun providePostgrest(client: SupabaseClient): Postgrest {
        // Hilt sửa lỗi MissingBinding cho FeedRepositoryImpl
        return client.postgrest
    }

    // --- THÊM 2 HÀM NÀY CHO KIỂM DUYỆT ---
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideModerationService(client: OkHttpClient): ModerationService {
        return ModerationService(client)
    }
}