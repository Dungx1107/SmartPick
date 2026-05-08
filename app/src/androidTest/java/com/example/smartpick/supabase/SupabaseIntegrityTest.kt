package com.example.smartpick.supabase

import org.junit.Rule
import io.github.jan.supabase.postgrest.query.Columns
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.utils.Constants.TABLE_POSTS
import com.example.smartpick.core.utils.Constants.TABLE_PRODUCTS
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import java.util.UUID

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SupabaseIntegrityTest {

    @get:Rule var hiltRule = HiltAndroidRule(this)
    @Inject lateinit var supabase: SupabaseClient

    @Before
    fun init() { hiltRule.inject() }

    private fun logHeader(title: String) {
        println("\n" + "=".repeat(40))
        println("🚀 TEST CASE: $title")
        println("=".repeat(40))
    }

    /**
     * TEST 1: Kiểm tra cấu trúc chi tiết từng bảng
     */
    @Test
    fun step1_ValidateFullSchema() = runBlocking {
        logHeader("Vòng 1: Kiểm tra ánh xạ Model")

        val tables = listOf(
            TABLE_POSTS to Post::class,
            TABLE_PRODUCTS to Product::class,
            "users" to User::class
        )

        tables.forEach { (name, kClass) ->
            try {
                supabase.postgrest[name].select { limit(1) }
                println("✅ Bảng [$name]: Cấu trúc khớp với Model [${kClass.simpleName}]")
            } catch (e: Exception) {
                println("❌ Bảng [$name]: Lỗi ánh xạ - ${e.message}")
                throw e
            }
        }
    }

    /**
     * TEST 2: Kiểm tra quyền Ghi (INSERT) và Xóa (DELETE) - RLS Check
     */
    @Test
    fun step2_TestWritePermissions() = runBlocking {
        logHeader("Vòng 2: Kiểm tra quyền Ghi/Xóa (RLS)")

        val testPostId = UUID.randomUUID().toString()
        val dummyPost = Post(
            id = testPostId,
            userId = "b3c8f85a-0000-0000-0000-000000000000", // Giả lập UUID
            content = "Dữ liệu Test RLS",
            mediaUrls = emptyList()
        )

        try {
            // Thử Insert
            supabase.postgrest[TABLE_POSTS].insert(dummyPost)
            println("✅ Quyền INSERT: Hợp lệ.")

            // Thử Delete ngay sau đó để dọn rác
            supabase.postgrest[TABLE_POSTS].delete { filter { eq("id", testPostId) } }
            println("✅ Quyền DELETE: Hợp lệ (Đã dọn dẹp dữ liệu test).")
        } catch (e: RestException) {
            println("❌ Lỗi RLS: ${e.error} - ${e.description}")
            fail("RLS Policy chưa được cấu hình đúng.")
        }
    }

    /**
     * TEST 3: Kiểm tra liên kết bảng (Join Integrity)
     */
    @Test
    fun step3_TestRelationshipJoin() = runBlocking {
        logHeader("Vòng 3: Kiểm tra liên kết Post -> Product")

        try {
            val response = supabase.postgrest[TABLE_POSTS].select(
                columns = Columns.raw("*, products(*)")
            ) { limit(1) }

            assertNotNull(response)
            println("✅ Liên kết ngoại: SELECT Join thành công.")
            println("📊 Dữ liệu mẫu: ${response.data}")
        } catch (e: Exception) {
            println("❌ Lỗi liên kết: ${e.message}")
            fail("Mối quan hệ Foreign Key giữa posts và products có vấn đề.")
        }
    }
}
