package com.example.smartpick.core.utils

import com.example.smartpick.BaseUnitTest
import org.junit.Assert.*
import org.junit.Test

class FileUtilsTest : BaseUnitTest() {

    @Test
    fun `isVideoUrl - URL video hop le`() {
        assertTrue(FileUtils.isVideoUrl("https://example.com/video.mp4"))
        assertTrue(FileUtils.isVideoUrl("http://site.vn/clip.MOV"))
        assertTrue(FileUtils.isVideoUrl("video.mkv"))
        assertTrue(FileUtils.isVideoUrl("filename.3gp"))
        assertTrue(FileUtils.isVideoUrl("path/to/file.m4v"))
    }

    @Test
    fun `isVideoUrl - URL khong phai video`() {
        assertFalse(FileUtils.isVideoUrl("https://example.com/image.jpg"))
        assertFalse(FileUtils.isVideoUrl("http://site.vn/doc.pdf"))
        assertFalse(FileUtils.isVideoUrl("archive.zip"))
        assertFalse(FileUtils.isVideoUrl("text.txt"))
        assertFalse(FileUtils.isVideoUrl("no_extension"))
    }
}
