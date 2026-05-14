package com.example.smartpick.features.profile.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.features.profile.data.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository    // Inject repository để gọi upload + update database
) : ViewModel() {
    private val _isUploading =
        MutableStateFlow(false)    // State dùng để báo UI biết đang upload hay không (loading)
    val isUploading =
        _isUploading.asStateFlow()    // Expose ra ngoài dưới dạng read-only (UI chỉ đọc, không sửa)

    private val _selectedImage = MutableStateFlow<Any?>(null)
    val selectedImage = _selectedImage.asStateFlow()

    fun updateSelectedImage(image: Any?) {
        _selectedImage.value = image
    }
    fun saveProfile(
        userId: String,
        name: String,
        username: String,
        phone: String,
        email: String,
        context: Context,
        currentAvatarUrl: String?,
        onSuccess: (newAvatarUrl: String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isUploading.value = true
            try {
                var finalAvatarUrl = currentAvatarUrl

                // 1. Nếu có ảnh mới được chọn -> Convert sang ByteArray và Upload
                if (_selectedImage.value != null) {
                    val imageBytes = when (val img = _selectedImage.value) {
                        is Uri -> context.contentResolver.openInputStream(img)?.readBytes()
                        is Bitmap -> {
                            val stream = ByteArrayOutputStream()
                            img.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                            stream.toByteArray()
                        }

                        else -> null
                    }

                    if (imageBytes != null) {
                        finalAvatarUrl = userProfileRepository.uploadAvatar(userId, imageBytes)
                    }
                }

                // 2. Cập nhật database
                userProfileRepository.updateUserProfile(
                    userId = userId,
                    avatarUrl = finalAvatarUrl,
                    fullName = name,
                    username = username,
                    phone = phone,
                    email = email
                )
                _selectedImage.value = null
                onSuccess(finalAvatarUrl.toString())

                // 2. Cập nhật Database với URL ảnh (mới hoặc cũ)
                if (finalAvatarUrl != null) {
                    userProfileRepository.updateUserAvatar(userId, finalAvatarUrl)
                    onSuccess(finalAvatarUrl) // Gọi callback để AuthViewModel đồng bộ app
                }

            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.message ?: "Đã xảy ra lỗi hệ thống")
            } finally {
                _isUploading.value = false
            }
        }
    }


}
