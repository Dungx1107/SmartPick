package com.example.smartpick.core.utils

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {
    private const val CHANNEL_ID = "smartpick_channel_id"

    fun showNotification(
        context: Context,
        title: String,
        body: String,
        deepLink: String,
        notificationId: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Tạo Notification Channel (Bắt buộc cho Android 8.0 trở lên)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SmartPick Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh thông báo chính của SmartPick"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Tạo Intent xử lý Deep Link khi User click vào thông báo
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink)).apply {
            setPackage(context.packageName) // Đảm bảo mở bằng ứng dụng hiện tại
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Build giao diện Notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            // QUAN TRỌNG: Thay R.drawable.ic_launcher_foreground bằng icon app của bạn.
            // Nếu dùng icon mặc định của Android, giao diện sẽ bị lỗi màu trắng hoặc hiển thị sai.
            .setSmallIcon(R.drawable.ic_dialog_info)
//            .setSmallIcon(R.mipmap.ic_launcher.)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // Hỗ trợ hiển thị text dài
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Tự động tắt thông báo khi click
            .setContentIntent(pendingIntent) // Gắn sự kiện click

        // 4. Hiển thị
        notificationManager.notify(notificationId, builder.build())
    }
}