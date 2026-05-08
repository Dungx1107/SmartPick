package com.example.smartpick.features.post_creation.data

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.example.smartpick.core.model.Product
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class PostCreationRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: PostCreationRepository

    @Inject
    lateinit var supabaseClient: SupabaseClient

    private lateinit var context: Context


    @Before
    fun init() = runTest { // Thêm runTest ở đây
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()

        // ĐĂNG NHẬP TRƯỚC KHI TEST
        // Bạn nên dùng một tài khoản test cố định
        try {
            supabaseClient.auth.signInWith(IDToken) {
                // Hoặc dùng signInWith(Email) nếu bạn có tài khoản test
                // Ở đây tôi ví dụ dùng một session có sẵn hoặc đăng nhập nhanh
            }
            // Cách nhanh nhất cho môi trường Test:
            // Đảm bảo userId bạn dùng trong Test là ID của một user CÓ THẬT trong bảng auth.users
        } catch(e: Exception) {
            println("Không thể đăng nhập test: ${e.message}")
        }
    }

    @Test
    fun testCreateFullPost_WithProduct() = runTest {
        // 1. Giả lập dữ liệu sản phẩm
        val mockProduct = Product(
            ownerId = "04037596-f94d-4467-932b-92796e625529", // Thay bằng ID user thực tế của bạn trong DB
            name = "Sản phẩm Test Tự Động",
            price = 999000.0,
            brand = "Test Brand",
            category = "Test Category"
        )

        // 2. Danh sách Uri giả (Bạn cần ít nhất 1 file ảnh thực tế trong bộ nhớ máy để test upload)
        // Nếu không có Uri thực, bước uploadMedia sẽ trả về "" nhưng vẫn chạy tiếp được
        val uris = emptyList<Uri>()

        println("--- BẮT ĐẦU TEST ĐĂNG BÁN ---")

        try {
            repository.createFullPost(
                userId = "04037596-f94d-4467-932b-92796e625529", // Thay ID thực
                content = "Nội dung bài viết được tạo từ Auto-Test",
                mediaUris = uris,
                productData = mockProduct,
                context = context
            )
            println("--- TEST THÀNH CÔNG: Dữ liệu đã được gửi ---")
        } catch (e: Exception) {
            println("--- TEST THẤT BẠI: ${e.message} ---")
            e.printStackTrace()
            assert(false)
        }
    }
}