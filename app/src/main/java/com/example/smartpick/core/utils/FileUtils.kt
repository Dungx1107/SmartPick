package com.example.smartpick.core.utils

object FileUtils {
    private val VIDEO_EXTENSIONS = listOf("mp4", "mov", "m4v", "3gp", "mkv")

    /**
     * Kiểm tra xem URL có trỏ đến một tệp video hay không dựa trên đuôi file.
     */
    fun isVideoUrl(url: String): Boolean {
        val extension = url.substringAfterLast(".", "").lowercase()
        return VIDEO_EXTENSIONS.contains(extension)
    }
}