package com.example.smartpick.features.auth.data

import android.util.Log
import com.example.smartpick.core.model.User
import com.example.smartpick.core.network.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {

    private val supabase = SupabaseClient.supabaseClient

    /**
     * Hàm lấy thông tin user hiện tại từ Database
     */
    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        try {
            // 1. Kiểm tra xem Supabase Auth có session nào đang chạy không
            val authUser = supabase.auth.currentUserOrNull() ?: return@withContext null

            // 2. Lấy data từ bảng users khớp với ID của Auth
            val result = supabase.postgrest["users"]
                .select {
                    filter {
                        eq("id", authUser.id)
                    }
                }
                .decodeSingle<User>()

            Log.d("AUTH", "Đã lấy được user hiện tại: ${result.fullName}")
            return@withContext result
        } catch (e: Exception) {
            Log.e("AUTH", "Lỗi khi lấy user hiện tại: ${e.message}")
            null
        }
    }

    /**
     * Hàm đăng nhập bằng Google và lưu thông tin user vào database
     * suspend: hàm chạy bất đồng bộ, có thể tạm dừng
     * googleIdToken: token nhận được từ Google sau khi đăng nhập thành côn
     */
    suspend fun signInWithGoogleAndSaveUser(googleIdToken: String): User? =
        withContext(Dispatchers.IO) {
            /**
             * withContext(Dispatchers.IO):
             * chuyển công việc sang luồng I/O (network, database)
             * tránh block luồng chính của UI
             */
            try {
                // lay instance Supabase da khoi tao san (singleton)
                val supabaseClient = SupabaseClient.supabaseClient

                /**
                 * Bước 1: Đăng nhập Supabase bằng ID token từ Google
                 * signInWith: phương thức đăng nhập với các provider
                 * IDToken: kiểu đăng nhập bằng token
                 * provider = Google: xác định đăng nhập qua Google
                 * idToken = googleIdToken: truyền token vào
                 */
                Log.d("AUTH", "Bắt đầu sign in với Google token")

                supabaseClient.auth.signInWith(IDToken) {
                    provider = Google
                    idToken = googleIdToken
                }

                Log.d("AUTH", "Sign in xong")

                /**
                 * Lấy thông tin user hiện tại sau khi đăng nhập
                 * (có thể null nếu thất bại)
                 */
                val session = supabaseClient.auth.currentSessionOrNull()
                Log.d("AUTH", "Session: $session")

                val currentUser = session?.user
                Log.d("AUTH", "User: $currentUser")

                if (currentUser != null) {
                    /**
                     * Lấy tên từ metadata của user (dữ liệu bổ sung từ Google)
                     * userMetadata: JSON chứa thông tin như full_name, avatar_url, email...
                     * ?. (safe call): nếu metadata null thì trả về null, không bị lỗi
                     */
                    val fullName = currentUser.userMetadata
                        ?.get("full_name") // Lấy giá trị của key "full_name"
                        ?.jsonPrimitive?.content // Chuyển thành chuỗi thuần

                    // Lấy avatar URL từ metadata
                    val avatarUrl = currentUser.userMetadata
                        ?.get("avatar_url")
                        ?.jsonPrimitive?.content

                    // Tạo object User từ dữ liệu lấy được
                    val myUser = User(
                        id = currentUser.id, // ID của user trong Supabase
                        email = currentUser.email, // Email từ Google
                        fullName = fullName,
                        avatarUrl = avatarUrl,
                        username = currentUser.email?.substringBefore("@") // Lấy phần trước dấu @ làm username
                    )

                    /**
                     * Bước 2: Lưu user vào bảng "users" trong database
                     * postgrest: công cụ tương tác với database qua API
                     * ["users"]: chọn bảng users (Dũng nhớ check lại tên bảng trên Supabase nhé)
                     * upsert: update nếu đã tồn tại, insert nếu chưa có
                     */
                    supabaseClient.postgrest["users"].upsert(myUser)

                    Log.d("AUTH", "Lưu Database xong: ${myUser.fullName}")

                    // CỰC KỲ QUAN TRỌNG: Trả về myUser để ViewModel nhận được dữ liệu
                    return@withContext myUser
                } else {
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e("Loi_Supabase", "Chi tiết lỗi: ${e.message}", e)
                return@withContext null
            }
        }


    suspend fun signOut() {
        withContext(Dispatchers.IO) {
            supabase.auth.signOut()
        }
    }
}