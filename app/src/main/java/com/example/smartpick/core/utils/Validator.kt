package com.example.smartpick.core.utils

import android.util.Patterns

object Validator {

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // dùng khi đưa ứng dụng vào thực tế, cần mk mạnh
    fun isStrongPassword(password: String): Boolean {
        val hasMinLength = password.length >= 8
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return hasMinLength &&
                hasUpperCase &&
                hasLowerCase &&
                hasDigit &&
                hasSpecialChar
    }

    // dùng khi test ứng dụng
    fun isTestValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidPhone(phone: String): Boolean {
        if (phone.isBlank()) return true // Nếu sđt là tùy chọn
        val phoneRegex = "^(0|\\+84)(\\d{9})$".toRegex()
        return phoneRegex.matches(phone)
    }

    fun isValidUsername(username: String): Boolean {
        // Từ 3-20 ký tự, chỉ gồm chữ, số, dấu chấm hoặc gạch dưới
        val usernameRegex = "^[a-zA-Z0-9._]{3,20}$".toRegex()
        return usernameRegex.matches(username)
    }
}