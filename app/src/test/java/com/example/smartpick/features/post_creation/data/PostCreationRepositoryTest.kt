package com.example.smartpick.features.post_creation.data

import android.content.Context
import android.util.Log
import com.example.smartpick.core.network.ModerationException
import com.example.smartpick.core.network.ModerationService
import io.github.jan.supabase.SupabaseClient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostCreationRepositoryTest {

    private lateinit var mockSupabase: SupabaseClient
    private lateinit var mockModerationService: ModerationService
    private lateinit var mockContext: Context
    private lateinit var repository: PostCreationRepository

    @Before
    fun setup() {
        // FIX LỖI: Giả lập thư viện Log của Android để không bị crash khi chạy Unit Test trên JVM
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        mockSupabase = mockk()
        mockModerationService = mockk()
        mockContext = mockk()
        repository = PostCreationRepository(mockSupabase, mockModerationService)
    }

    @Test
    fun `createFullPost - Text độc hại - Chặn và ném ModerationException`() = runTest {
        // 1. CHUẨN BỊ (ARRANGE)
        val toxicContent = "Thằng ngu này m mua ko"
        // Giả lập API AI kiểm duyệt trả về False (Không an toàn)
        coEvery { mockModerationService.checkTextContent(toxicContent) } returns false

        // 2. THỰC THI VÀ KIỂM TRA LỖI (ACT & ASSERT)
        try {
            repository.createFullPost(
                userId = "user1",
                content = toxicContent,
                mediaUris = emptyList(),
                productData = null,
                context = mockContext
            )
            // Nếu code chạy qua được dòng trên mà không quăng lỗi, nghĩa là test thất bại
            fail("Code đã để lọt nội dung độc hại mà không bắn Exception!")
        } catch (e: Exception) {
            println("TEST BẮT LỖI TỪ CẤM:")
            println("Exception ném ra: ${e::class.java.simpleName}")
            println("Thông báo lỗi: ${e.message}")

            // Xác nhận lỗi quăng ra đúng là loại ModerationException
            assertTrue("Phải là ModerationException", e is ModerationException)
        }
    }
}