package com.example.smartpick.core.utils

import com.example.smartpick.BaseUnitTest
import org.junit.Assert.*
import org.junit.Test

class ValidatorTest : BaseUnitTest() {

    @Test
    fun `isValidEmail - Email hop le`() {
        assertTrue(Validator.isValidEmail("test@example.com"))
        assertTrue(Validator.isValidEmail("user.name+tag@gmail.com"))
    }

    @Test
    fun `isValidEmail - Email khong hop le`() {
        assertFalse(Validator.isValidEmail("invalid-email"))
        assertFalse(Validator.isValidEmail("test@"))
        assertFalse(Validator.isValidEmail("@example.com"))
        assertFalse(Validator.isValidEmail(""))
    }

    @Test
    fun `isStrongPassword - Password manh`() {
        assertTrue(Validator.isStrongPassword("StrongPass123!"))
        assertTrue(Validator.isStrongPassword("P@ssw0rd2026"))
    }

    @Test
    fun `isStrongPassword - Password yeu`() {
        // Thieu chu hoa
        assertFalse(Validator.isStrongPassword("weakpass123!"))
        // Thieu chu thuong
        assertFalse(Validator.isStrongPassword("WEAKPASS123!"))
        // Thieu so
        assertFalse(Validator.isStrongPassword("WeakPass!!!!"))
        // Thieu ky tu dac biet
        assertFalse(Validator.isStrongPassword("WeakPass123"))
        // Qua ngan
        assertFalse(Validator.isStrongPassword("Wp1!"))
    }

    @Test
    fun `isTestValidPassword - Password thu nghiem`() {
        assertTrue(Validator.isTestValidPassword("123456"))
        assertFalse(Validator.isTestValidPassword("12345"))
    }

    @Test
    fun `isValidPhone - So dien thoai`() {
        // Hop le (vietnam phone format)
        assertTrue(Validator.isValidPhone("0912345678"))
        assertTrue(Validator.isValidPhone("+84912345678"))
        assertTrue(Validator.isValidPhone("")) // phone blank is true (optional)

        // Khong hop le
        assertFalse(Validator.isValidPhone("123456789"))
        assertFalse(Validator.isValidPhone("091234567")) // 9 digits after 0 (total 10 digits needed)
        assertFalse(Validator.isValidPhone("09123456789")) // too long
    }

    @Test
    fun `isValidUsername - Ten dang nhap`() {
        // Hop le (3-20 ky tu, chu, so, cham, gach duoi)
        assertTrue(Validator.isValidUsername("user_name"))
        assertTrue(Validator.isValidUsername("user.name"))
        assertTrue(Validator.isValidUsername("user123"))

        // Khong hop le
        assertFalse(Validator.isValidUsername("us")) // qua ngan
        assertFalse(Validator.isValidUsername("username_too_long_for_this_validation")) // qua dai
        assertFalse(Validator.isValidUsername("user name")) // chua khoang trang
        assertFalse(Validator.isValidUsername("user@123")) // chua ky tu la
    }
}
