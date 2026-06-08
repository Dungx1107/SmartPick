package com.example.smartpick.core.utils

import com.example.smartpick.BaseUnitTest
import org.junit.Test

class LoggerTest : BaseUnitTest() {

    @Test
    fun `testLoggerMethods`() {
        // Run without exception is success
        Logger.d("TEST_TAG", "Debug message test")
        Logger.e("TEST_TAG", "Error message test")
    }
}
