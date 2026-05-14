package com.example.smartpick.core.utils

import java.time.ZonedDateTime
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object TimeFormatter {
    fun formatTimeAgo(isoString: String?): String {
        if (isoString == null) return "Vừa xong"
        return try {
            val past = ZonedDateTime.parse(isoString)
            val now = ZonedDateTime.now()
            val duration = Duration.between(past, now)

            when {
                duration.toMinutes() < 1 -> "Vừa xong"
                duration.toMinutes() < 60 -> "${duration.toMinutes()} phút trước"
                duration.toHours() < 24 -> "${duration.toHours()} giờ trước"
                duration.toDays() < 30 -> "${duration.toDays()} ngày trước"
                else -> past.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            }
        } catch (e: DateTimeParseException) {
            // Lỗi định dạng ngày tháng không hợp lệ
            android.util.Log.e("TimeFormatter", "Invalid date format: $isoString", e)
            "Không xác định"
        } catch (e: Exception) {
            // Các lỗi khác (null pointer, v.v.)
            android.util.Log.e("TimeFormatter", "Unexpected error when parsing date: $isoString", e)
            "Vừa xong"
        }
    }
}