package com.example.smartpick.features.profile.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import javax.inject.Inject

// Repository dùng để xử lý các thao tác liên quan đến user
class UserRepository @Inject constructor(
    private val supabase: SupabaseClient    // Inject Supabase client để gọi API (database + storage)
) {
    /**
     * Upload ảnh avatar lên Supabase Storage (bucket: avatars)
     *
     * @param userId: id của user (dùng để đặt tên file)
     * @param imageBytes: dữ liệu ảnh dạng ByteArray
     * @return: URL public của ảnh sau khi upload
     */
    suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): String {

        // Lấy reference tới bucket "avatars" trên Supabase Storage
        val bucket = supabase.storage.from("avatars")

        /* Tạo tên file duy nhất:
         - gồm userId
         - thêm timestamp để tránh bị cache ảnh cũ */
        val fileName = "avatar_${userId}_${System.currentTimeMillis()}.jpg"

        /* Đường dẫn lưu file trong bucket
        "public/" thường dùng để cho phép truy cập public */
        val path = "public/$fileName"

        /* Upload file lên storage
           upsert = true nghĩa là: nếu file đã tồn tại -> ghi đè */
        bucket.upload(path, imageBytes, upsert = true)

        // Trả về URL public để hiển thị ảnh (ví dụ dùng cho ImageView)
        return bucket.publicUrl(path)
    }

    /**
     * Cập nhật URL avatar mới vào bảng "users" trong database
     *
     * @param userId: id của user cần update
     * @param newUrl: URL ảnh mới (lấy từ hàm uploadAvatar)
     */
    suspend fun updateUserAvatar(userId: String, newUrl: String) {
        // Gọi tới bảng "users"
        supabase.from("users").update(
            // Dữ liệu cần update:
            // set cột avatar_url = newUrl
            mapOf("avatar_url" to newUrl)
        ) {
            // Điều kiện WHERE id = userId
            filter {
                eq("id", userId)
            }
        }
    }

    /**
     * Cập nhật toàn bộ hồ sơ vào bảng "users"
     */
    suspend fun updateUserProfile(
        userId: String,
        avatarUrl: String?,
        fullName: String,
        username: String,
        phone: String,
        email:String
    ) {
        val updateData = mutableMapOf<String, String>()

        // Chỉ thêm avatar_url vào map nếu nó không null
        avatarUrl?.let { updateData["avatar_url"] = it }

        updateData["fullname"] = fullName
        updateData["username"] = username
        updateData["phone_number"] = phone
        updateData["email"] = email

        supabase.from("users").update(updateData) {
            filter { eq("id", userId) }
        }
    }
}