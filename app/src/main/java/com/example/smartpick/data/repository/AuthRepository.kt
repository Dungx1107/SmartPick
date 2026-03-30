package com.example.smartpick.data.repository

import android.util.Log
import com.example.smartpick.data.model.User
import com.example.smartpick.data.remote.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.utils.io.printStack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonPrimitive

class AuthRepository {
    /**
     * Hàm đăng nhập bằng Google và lưu thông tin user vào database
     * suspend: hàm chạy bất đồng bộ, có thể tạm dừng
     * googleIdToken: token nhận được từ Google sau khi đăng nhập thành côn
     */
    suspend fun signInWithGoogleAndSaveUser(googleIdToken: String) {
        /**
         * withContext(Dispatchers.IO):
         * chuyển công việc sang luồng I/O (network, database)
         * tránh block luồng chính của UI
         */
        withContext(Dispatchers.IO) {
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

                supabaseClient.auth.signInWith(IDToken) {
                    provider = Google
                    idToken = googleIdToken
                }

                /**
                 * Lấy thông tin user hiện tại sau khi đăng nhập
                 *(có thể null nếu thất bại)
                 */

                val currentUser = supabaseClient.auth.currentUserOrNull()

                if (currentUser != null) {
                    /**
                     *Lấy tên từ metadata của user (dữ liệu bổ sung từ Google)
                     *userMetadata: JSON chứa thông tin như full_name, avatar_url, email...
                     * ?. (safe call): nếu metadata null thì trả về null, không bị lỗi
                     */
                    val fullName = currentUser.userMetadata
                        ?.get("full_name")// Lấy giá trị của key "full_name"
                        ?.jsonPrimitive?.content// Chuyển thành chuỗi thuần

                    // Lấy avatar URL từ metadata
                    val avatarUrl = currentUser.userMetadata
                        ?.get("avatar_url")
                        ?.jsonPrimitive?.content

                    // Tạo object User từ dữ liệu lấy được
                    val myUser = User(
                        id = currentUser.id,//ID của user trong Supabase
                        email = currentUser.email,// Email từ Google
                        fullName = fullName,
                        avatarUrl = avatarUrl
                    )

                    /**
                     * Bước 2: Lưu user vào bảng "users" trong database
                     * postgrest: công cụ tương tác với database qua API
                     * ["users"]: chọn bảng users
                     * upsert: update nếu đã tồn tại, insert nếu chưa có (tránh trùng lặp)
                     */
                    supabaseClient.postgrest["users"].upsert(myUser)
                }
            } catch (e: Exception) {
                e.printStack()
                Log.e("Loi_Supabase", "Chi tiết lỗi: ${e.message}", e)
                throw e // Vẫn ném lỗi ra ngoài cho ViewModel
            }
        }
    }
}