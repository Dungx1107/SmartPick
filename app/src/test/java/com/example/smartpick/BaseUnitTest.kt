package com.example.smartpick

import android.util.Log
import android.util.Patterns
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.regex.Pattern

/**
 * Lớp cơ sở cho các JVM Unit Tests.
 * Giúp tự động giả lập (Mock) các lớp hệ thống của Android như Log và Patterns
 * để tránh lỗi crash khi chạy test trên JVM local.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
abstract class BaseUnitTest {

    protected val baseTestDispatcher = StandardTestDispatcher()

    @Before
    open fun setUpMocks() {
        Dispatchers.setMain(baseTestDispatcher)

        // 1. Giả lập Android Log
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.i(any(), any()) } returns 0

        // 1.5. Giả lập Settings cho Supabase Storage trên JVM
        mockkStatic("com.russhwolf.settings.NoArgKt")
        val mockSettings = mockk<com.russhwolf.settings.Settings>(relaxed = true)
        every { com.russhwolf.settings.Settings() } returns mockSettings

        // 2. Giả lập Patterns.EMAIL_ADDRESS bằng phản xạ (Reflection)
        setupEmailPatternMock()
    }

    @After
    open fun tearDownMocks() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    private fun setupEmailPatternMock() {
        // Mock Build.VERSION.SDK_INT
        try {
            setStaticFinalFieldUsingUnsafe(Class.forName("android.os.Build\$VERSION"), "SDK_INT", 26)
        } catch (e: Throwable) {
            // Ignore if build version class is not found
        }

        // Mock Patterns.EMAIL_ADDRESS
        val mockPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        setStaticFinalFieldUsingUnsafe(Patterns::class.java, "EMAIL_ADDRESS", mockPattern)

        // Mock BuildConfig để tránh lỗi khi nạp SupabaseProvider
        try {
            setStaticFinalFieldUsingUnsafe(BuildConfig::class.java, "SUPABASE_URL", "https://mock.supabase.co")
            setStaticFinalFieldUsingUnsafe(BuildConfig::class.java, "SUPABASE_KEY", "mock_supabase_key")
            setStaticFinalFieldUsingUnsafe(BuildConfig::class.java, "GEMINI_KEY", "mock_gemini_key")
            setStaticFinalFieldUsingUnsafe(BuildConfig::class.java, "SIGHTENGINE_USER", "mock_user")
            setStaticFinalFieldUsingUnsafe(BuildConfig::class.java, "SIGHTENGINE_SECRET", "mock_secret")
        } catch (e: Throwable) {
            // BuildConfig có thể chưa được tạo hoặc không có trường này
        }
    }

    protected fun setStaticFinalFieldUsingUnsafe(clazz: Class<*>, fieldName: String, value: Any) {
        try {
            val field = clazz.getDeclaredField(fieldName)
            field.isAccessible = true
            
            val unsafeClass = Class.forName("sun.misc.Unsafe")
            val unsafeField = unsafeClass.getDeclaredField("theUnsafe")
            unsafeField.isAccessible = true
            val unsafe = unsafeField.get(null)
            
            val staticFieldBaseMethod = unsafeClass.getMethod("staticFieldBase", java.lang.reflect.Field::class.java)
            val staticFieldOffsetMethod = unsafeClass.getMethod("staticFieldOffset", java.lang.reflect.Field::class.java)
            val putObjectMethod = unsafeClass.getMethod("putObject", Any::class.java, Long::class.javaPrimitiveType, Any::class.java)
            
            val base = staticFieldBaseMethod.invoke(unsafe, field)
            val offset = staticFieldOffsetMethod.invoke(unsafe, field) as Long
            putObjectMethod.invoke(unsafe, base, offset, value)
        } catch (e: Exception) {
            System.err.println("Không thể mock trường $fieldName trong lớp ${clazz.simpleName} bằng Unsafe: ${e.message}")
        }
    }

    protected fun setInstanceFieldUsingUnsafe(target: Any, fieldName: String, value: Any) {
        try {
            val clazz = target.javaClass
            val field = clazz.getDeclaredField(fieldName)
            field.isAccessible = true
            
            val unsafeClass = Class.forName("sun.misc.Unsafe")
            val unsafeField = unsafeClass.getDeclaredField("theUnsafe")
            unsafeField.isAccessible = true
            val unsafe = unsafeField.get(null)
            
            val objectFieldOffsetMethod = unsafeClass.getMethod("objectFieldOffset", java.lang.reflect.Field::class.java)
            val putObjectMethod = unsafeClass.getMethod("putObject", Any::class.java, Long::class.javaPrimitiveType, Any::class.java)
            
            val offset = objectFieldOffsetMethod.invoke(unsafe, field) as Long
            putObjectMethod.invoke(unsafe, target, offset, value)
        } catch (e: Exception) {
            System.err.println("Không thể mock trường $fieldName trong đối tượng ${target.javaClass.simpleName} bằng Unsafe: ${e.message}")
        }
    }
}
