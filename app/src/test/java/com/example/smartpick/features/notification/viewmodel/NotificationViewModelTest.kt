package com.example.smartpick.features.notification.viewmodel

import com.example.smartpick.BaseUnitTest
import com.example.smartpick.core.model.Notification
import com.example.smartpick.features.notification.data.NotificationRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val notificationFlow = MutableStateFlow<List<Notification>>(emptyList())

    private lateinit var mockNotificationRepository: NotificationRepository
    private lateinit var viewModel: NotificationViewModel

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockNotificationRepository = mockk(relaxed = true)
        every { mockNotificationRepository.observeNotifications("user123") } returns notificationFlow

        viewModel = NotificationViewModel(mockNotificationRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Verify default values`() {
        assertEquals(0, viewModel.unreadCount.value)
        assertNull(viewModel.latestNotification.value)
        assertTrue(viewModel.uiNotifications.value.isEmpty())
    }

    @Test
    fun `subscribeToNotifications - Receives flow - Updates uiNotifications, unreadCount and latestNotification`() = runTest(testDispatcher) {
        val notifications = listOf(
            Notification(id = "n1", receiverId = "user123", senderId = "s1", type = "comment", title = "Alice commented", content = "Good job", isRead = false, createdAt = "2026-06-08T12:00:00Z"),
            Notification(id = "n2", receiverId = "user123", senderId = "s2", type = "like", title = "Bob liked", content = "Nice post", isRead = true, createdAt = "2026-06-08T11:00:00Z"),
            Notification(id = "n3", receiverId = "user123", senderId = "s3", type = "order", title = "Order success", content = "Purchased shoes", isRead = false, createdAt = "2026-06-08T12:30:00Z")
        )

        viewModel.subscribeToNotifications("user123")
        notificationFlow.value = notifications
        testDispatcher.scheduler.advanceUntilIdle()

        val uiList = viewModel.uiNotifications.value
        assertEquals(3, uiList.size)
        // Check sorting: n3 is newest (12:30), then n1 (12:00), then n2 (11:00)
        assertEquals("n3", uiList[0].id)
        assertEquals("n1", uiList[1].id)
        assertEquals("n2", uiList[2].id)

        // Check unreadCount: n3 and n1 are unread -> 2
        assertEquals(2, viewModel.unreadCount.value)

        // Check latestNotification: newest unread is n3
        val latest = viewModel.latestNotification.value
        assertNotNull(latest)
        assertEquals("n3", latest?.id)
        assertTrue(latest?.isUnread == true)
    }

    @Test
    fun `markAsRead - Optimistically marks as read locally and triggers repository update`() = runTest(testDispatcher) {
        val notifications = listOf(
            Notification(id = "n1", receiverId = "user123", senderId = "s1", type = "comment", title = "Alice commented", content = "Good job", isRead = false, createdAt = "2026-06-08T12:00:00Z")
        )

        viewModel.subscribeToNotifications("user123")
        notificationFlow.value = notifications
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.unreadCount.value)
        assertTrue(viewModel.uiNotifications.value[0].isUnread)

        // Mark as read
        viewModel.markAsRead("n1")

        // Check local state updated optimistically instantly
        assertFalse(viewModel.uiNotifications.value[0].isUnread)
        assertEquals(0, viewModel.unreadCount.value)

        testDispatcher.scheduler.advanceUntilIdle()

        // Verify repository called
        coVerify(exactly = 1) { mockNotificationRepository.markAsRead("n1") }
    }
}
