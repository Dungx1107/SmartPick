package com.example.smartpick.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providePostgrest(client: SupabaseClient): Postgrest {
        return client.postgrest
    }
    @Provides
    @Singleton
    @Named("DefaultClient")
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }
    @Provides
    @Singleton
    @Named("LlmClient")
    fun provideLlmOkHttpClient(
        @Named("DefaultClient") defaultClient: OkHttpClient
    ): OkHttpClient {
        return defaultClient.newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideModerationService(
        @Named("LlmClient") client: OkHttpClient
    ): ModerationService {
        return ModerationService(client)
    }

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return SupabaseProvider.client
    }
}