package com.example.smartpick.core.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.smartpick.BaseUnitTest
import io.mockk.*
import org.junit.Test

class NotificationHelperTest : BaseUnitTest() {

    @Test
    fun `showNotification - Verify Android services called correctly`() {
        // Mock Context và NotificationManager
        val mockContext = mockk<Context>(relaxed = true)
        val mockNotificationManager = mockk<NotificationManager>(relaxed = true)

        every { mockContext.getSystemService(Context.NOTIFICATION_SERVICE) } returns mockNotificationManager
        every { mockContext.packageName } returns "com.example.smartpick"

        // Mock Static Uri
        mockkStatic(Uri::class)
        val mockUri = mockk<Uri>()
        every { Uri.parse(any()) } returns mockUri

        // Mock Static PendingIntent
        mockkStatic(PendingIntent::class)
        val mockPendingIntent = mockk<PendingIntent>()
        every {
            PendingIntent.getActivity(any(), any(), any(), any())
        } returns mockPendingIntent

        // Mock NotificationCompat.Builder
        mockkConstructor(NotificationCompat.Builder::class)
        val mockBuilder = mockk<NotificationCompat.Builder>(relaxed = true)
        val mockNotification = mockk<Notification>()
        every { anyConstructed<NotificationCompat.Builder>().build() } returns mockNotification

        // Thực thi
        NotificationHelper.showNotification(
            context = mockContext,
            title = "Test Title",
            body = "Test Body",
            deepLink = "smartpick://product/123",
            notificationId = 999
        )

        // Xác nhận đã gọi các API NotificationManager của hệ thống
        verify {
            mockNotificationManager.notify(999, mockNotification)
        }
    }
}
