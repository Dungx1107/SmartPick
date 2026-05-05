package com.example.smartpick.core.utils

object Constants {
    // Google Auth
    const val WEB_CLIENT_ID = "375937408952-g4bq1lg0fu3sfst7udjsnee02d1jnn1i.apps.googleusercontent.com"

    // Social Providers
    const val PROVIDER_GOOGLE = "google"
    const val TABLE_USERS = "users"
    const val TABLE_POSTS = "posts"
    const val TABLE_PRODUCTS = "products"

    // Database Tables
    object UserMetadata {
        const val FULL_NAME = "full_name"
        const val USERNAME = "username"
        const val AVATAR_URL = "avatar_url"
        const val PHONE_NUMBER = "phone_number"
        const val EMAIL = "email"
        const val AVATARS = "avatars"

    }

    // Validation Messages
    object ValidationError {
        const val FULL_NAME_EMPTY = "Họ tên không được để trống"
        const val EMAIL_EMPTY = "Email không được bỏ trống !!!"
        const val EMAIL_INVALID = "Email không đúng định dạng"
        const val PHONE_INVALID = "Số điện thoại không đúng định dạng"
        const val USERNAME_EMPTY = "Username không được bỏ trống !!!"
        const val USERNAME_INVALID = "Username từ 3-20 ký tự, không chứa ký tự đặc biệt"
        const val PASSWORD_EMPTY = "Mật khẩu không được để trống !!!"
        const val PASSWORD_INVALID = "Mật khẩu phải có ít nhất 6 ký tự"
        const val PASSWORD_MISMATCH = "Mật khẩu xác nhận không khớp"

        const val EMAIL_ALREADY_EXISTS = "Email này đã được sử dụng cho tài khoản khác."
        const val USERNAME_ALREADY_EXISTS = "Username này đã có người sử dụng."
        const val GOOGLE_NOT_REGISTERED = "Tài khoản Google này chưa được đăng ký trong hệ thống."
    }
}