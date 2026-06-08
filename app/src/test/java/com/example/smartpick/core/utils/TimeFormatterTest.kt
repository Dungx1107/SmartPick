package com.example.smartpick.core.utils

import com.example.smartpick.BaseUnitTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TimeFormatterTest : BaseUnitTest() {

    @Test
    fun `formatTimeAgo - Vua xong`() {
        val nowIso = ZonedDateTime.now().toString()
        assertEquals("Vừa xong", TimeFormatter.formatTimeAgo(nowIso))
    }

    @Test
    fun `formatTimeAgo - Vai phut truoc`() {
        val fifteenMinsAgo = ZonedDateTime.now().minusMinutes(15).toString()
        assertEquals("15 phút trước", TimeFormatter.formatTimeAgo(fifteenMinsAgo))
    }

    @Test
    fun `formatTimeAgo - Vai gio truoc`() {
        val threeHoursAgo = ZonedDateTime.now().minusHours(3).toString()
        assertEquals("3 giờ trước", TimeFormatter.formatTimeAgo(threeHoursAgo))
    }

    @Test
    fun `formatTimeAgo - Vai ngay truoc`() {
        val fiveDaysAgo = ZonedDateTime.now().minusDays(5).toString()
        assertEquals("5 ngày trước", TimeFormatter.formatTimeAgo(fiveDaysAgo))
    }

    @Test
    fun `formatTimeAgo - Ngay thang cu`() {
        // Mock một ngày cũ cố định cách đây hơn 30 ngày
        val oldDate = ZonedDateTime.now().minusDays(40)
        val oldDateIso = oldDate.toString()
        val expectedFormat = oldDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        assertEquals(expectedFormat, TimeFormatter.formatTimeAgo(oldDateIso))
    }

    @Test
    fun `formatTimeAgo - Null string - Returns Vua xong`() {
        assertEquals("Vừa xong", TimeFormatter.formatTimeAgo(null))
    }

    @Test
    fun `formatTimeAgo - Malformed input - Returns Khong xac dinh`() {
        assertEquals("Không xác định", TimeFormatter.formatTimeAgo("not-a-date"))
    }
}
