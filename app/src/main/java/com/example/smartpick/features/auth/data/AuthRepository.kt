package com.example.smartpick.features.auth.data

import android.util.Log
import com.example.smartpick.BuildConfig
import com.example.smartpick.core.model.User
import com.example.smartpick.core.network.SupabaseClient
import com.example.smartpick.core.network.SupabaseClient.supabaseClient
import com.example.smartpick.core.utils.Constants
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    private val supabase = supabaseClient
    
    // Luồng trạng thái session từ SDK
    val sessionStatus: Flow<SessionStatus> = supabase.auth.sessionStatus
    /**
     * Hàm lấy thông tin user hiện tại từ Database
     */
    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        try {
            // 1. Kiểm tra xem Supabase Auth có session nào đang chạy không
            val authUser = supabase.auth.currentUserOrNull() ?: return@withContext null

            // Thử lấy dữ liệu tối đa 3 lần nếu chưa thấy trong DB
            var result: User? = null
            repeat(3) { i ->
                result = try {
                    supabase.postgrest["users"]
                        .select { filter { eq("id", authUser.id) } }
                        .decodeSingle<User>()
                } catch (e: Exception) { null }

                if (result != null) return@withContext result
                delay(500) // Đợi 0.5s trước khi thử lại
            }

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

    suspend fun signUpManual(
        email: String,
        pass: String,
        name: String,
        user: String,
        phone: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signUpWith(Email) {
                this.email = email.trim()
                password = pass.trim()
                data = buildJsonObject {
                    put(Constants.UserMetadata.FULL_NAME.trim(), name)
                    put(Constants.UserMetadata.USERNAME.trim(), user)
                    put(Constants.UserMetadata.PHONE_NUMBER.trim(), phone)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            val message = e.message ?: ""
            Log.e("AUTH_ERROR", "Raw error: $message") // In log để xem lỗi thật từ Supabase

            val errorMsg = when {
                message.contains("already registered", ignoreCase = true) ->
                    "Email này đã được đăng ký bởi một tài khoản khác."

                message.contains("duplicate key", ignoreCase = true) ->
                    "Tên đăng nhập (Username) đã có người sử dụng."

                message.contains("Network", ignoreCase = true) ->
                    "Lỗi kết nối mạng, vui lòng thử lại."

                else -> "Đăng ký thất bại: $message"
            }
            Result.failure(Exception(errorMsg))
        }
    }

    /* kiểm tra trùng lặp username và email khi đăng kí*/
    suspend fun checkAvailability(username: String, email: String):
            Result<AvailabilityResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d("AUTH_DEBUG", "Đang kết nối tới: ${BuildConfig.SUPABASE_URL}")
            val response = supabase.postgrest.rpc(
                function = "check_user_availability",
                parameters = mapOf(
                    "p_username" to username,
                    "p_email" to email
                )
            ).decodeSingle<AvailabilityResponse>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /* đăng nhập thủ công */
    suspend fun signInManual(email: String, pass: String):
            Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signInWith(Email) {
                this.email = email
                password = pass
            }
            Result.success(Unit)
        } catch (e: Exception) {
            val errorMsg = when {
                // Bắt lỗi email chưa xác nhận
                e.message?.contains("Email not confirmed", ignoreCase = true) == true ->
                    "Vui lòng xác nhận email của bạn trước khi đăng nhập."

                e.message?.contains("Invalid login credentials", ignoreCase = true) == true ->
                    "Email hoặc mật khẩu không chính xác."

                else -> e.message ?: "Đăng nhập thất bại"
            }
            Result.failure(Exception(errorMsg))
        }
    }
}
