package com.example.smartpick.core.network

import com.example.smartpick.BaseUnitTest
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.functions.Functions
import org.junit.Assert.*
import org.junit.Test

class SupabaseProviderTest : BaseUnitTest() {

    @Test
    fun `testSupabaseClientInitialization - Client should be instantiated with correct plugins`() {
        val client = SupabaseProvider.client
        assertNotNull(client)

        // Kiểm tra xem các plugin chính đã được cài đặt chưa
        assertNotNull(client.pluginManager.getPlugin(Auth))
        assertNotNull(client.pluginManager.getPlugin(Postgrest))
        assertNotNull(client.pluginManager.getPlugin(Storage))
        assertNotNull(client.pluginManager.getPlugin(Functions))
        assertNotNull(client.pluginManager.getPlugin(Realtime))
    }
}
