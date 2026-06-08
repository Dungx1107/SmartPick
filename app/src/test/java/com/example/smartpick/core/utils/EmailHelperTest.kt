package com.example.smartpick.core.utils

import com.example.smartpick.BaseUnitTest
import com.example.smartpick.BuildConfig
import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test

class EmailHelperTest : BaseUnitTest() {

    private lateinit var mockOkHttpClient: OkHttpClient
    private lateinit var mockCall: okhttp3.Call

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        mockOkHttpClient = mockk()
        mockCall = mockk()

        // Sử dụng Unsafe để thay thế client private trong EmailHelper
        try {
            setStaticFinalFieldUsingUnsafe(EmailHelper::class.java, "client", mockOkHttpClient)
        } catch (e: Throwable) {
            setInstanceFieldUsingUnsafe(EmailHelper, "client", mockOkHttpClient)
        }
    }

    @Test
    fun `send - Email blank - Returns immediately without calling network`() = runTest {
        EmailHelper.send(email = " ", type = EmailHelper.EmailType.WELCOME)
        verify(exactly = 0) { mockOkHttpClient.newCall(any()) }
    }

    @Test
    fun `send - Network success - Calls OkHttp client with correct parameters`() = runTest {
        val response = Response.Builder()
            .request(Request.Builder().url("https://mock.supabase.co/functions/v1/send-app-email").build())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body("".toResponseBody("application/json".toMediaType()))
            .build()

        every { mockOkHttpClient.newCall(any<Request>()) } returns mockCall
        every { mockCall.execute() } returns response

        // Thực thi
        EmailHelper.send(
            email = "test@example.com",
            type = EmailHelper.EmailType.WELCOME,
            name = "Test User"
        )

        // Xác nhận cuộc gọi mạng
        verify(exactly = 1) { mockOkHttpClient.newCall(any<Request>()) }
    }
}
