package com.example.smartpick.features.comment.viewmodel

import app.cash.turbine.test
import com.example.smartpick.BaseUnitTest
import com.example.smartpick.core.model.Comment
import com.example.smartpick.core.model.User
import com.example.smartpick.features.comment.data.CommentRepository
import com.example.smartpick.features.notification.data.NotificationRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommentViewModelTest : BaseUnitTest() {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockCommentRepository: CommentRepository
    private lateinit var mockNotificationRepository: NotificationRepository
    private lateinit var viewModel: CommentViewModel

    private val fakeUser = User(id = "user123", email = "test@example.com", fullName = "John Doe")

    @Before
    override fun setUpMocks() {
        super.setUpMocks()
        Dispatchers.setMain(testDispatcher)

        mockCommentRepository = mockk(relaxed = true)
        mockNotificationRepository = mockk(relaxed = true)

        viewModel = CommentViewModel(mockCommentRepository, mockNotificationRepository)
    }

    @After
    override fun tearDownMocks() {
        super.tearDownMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state - Verify defaults`() {
        assertTrue(viewModel.comments.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.isSending.value)
        assertNull(viewModel.replyingTo.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `loadComments - Success - Groups replies inside parent comments`() = runTest(testDispatcher) {
        val commentsFromRepo = listOf(
            Comment(id = "c1", postId = "post123", userId = "user_a", content = "Parent Comment", createdAt = "2026-06-08T12:00:00Z", user = User(id = "user_a", fullName = "Alice")),
            Comment(id = "c2", postId = "post123", userId = "user_b", content = "Reply Comment", createdAt = "2026-06-08T12:05:00Z", user = User(id = "user_b", fullName = "Bob"), parentId = "c1")
        )

        coEvery { mockCommentRepository.getComments("post123", "user123") } returns commentsFromRepo

        viewModel.loadComments("post123", "owner123", "user123")
        testDispatcher.scheduler.advanceUntilIdle()

        val uiComments = viewModel.comments.value
        assertEquals(1, uiComments.size) // Only 1 top-level comment
        assertEquals("c1", uiComments[0].id)
        assertEquals("Parent Comment", uiComments[0].content)

        assertEquals(1, uiComments[0].replies.size) // 1 reply nested
        assertEquals("c2", uiComments[0].replies[0].id)
        assertEquals("Reply Comment", uiComments[0].replies[0].content)
        assertEquals("Alice", uiComments[0].replies[0].replyToName) // Bob replies to Alice
    }

    @Test
    fun `loadComments - Failure - Sets error state`() = runTest(testDispatcher) {
        coEvery { mockCommentRepository.getComments("post123", "user123") } throws Exception("Database Error")

        viewModel.loadComments("post123", "owner123", "user123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.comments.value.isEmpty())
        assertEquals("Lỗi tải bình luận", viewModel.error.value)
    }

    @Test
    fun `sendComment - Top level comment - Success flow`() = runTest(testDispatcher) {
        coEvery {
            mockCommentRepository.insertComment(
                postId = "post123",
                userId = "user123",
                content = "Hello World",
                receiverId = "owner123",
                parentId = null
            )
        } returns "new_comment_id"

        viewModel.sendComment(
            postId = "post123",
            userId = "user123",
            content = "Hello World",
            postOwnerId = "owner123",
            currentUserName = "John Doe"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Phải cập nhật local state bằng cách thêm bình luận mới
        val uiComments = viewModel.comments.value
        assertEquals(1, uiComments.size)
        assertEquals("new_comment_id", uiComments[0].id)
        assertEquals("Hello World", uiComments[0].content)
        assertEquals("John Doe", uiComments[0].authorName)
        assertNull(uiComments[0].parentId)

        // Không gửi push notification cho chính mình (receiverId = owner123)
        // Nếu receiverId != userId thì mới gửi. Ở đây receiverId = owner123 (khác user123) -> Phải gửi push!
        coVerify(exactly = 1) {
            mockNotificationRepository.triggerPushNotification(
                receiverId = "owner123",
                title = "Bình luận mới",
                body = "John Doe đã phản hồi: Hello World",
                type = "comment",
                postId = "post123",
                targetId = "new_comment_id"
            )
        }
    }

    @Test
    fun `sendComment - Reply comment - Success flow`() = runTest(testDispatcher) {
        // Chuẩn bị có sẵn 1 comment Alice trong ViewModel
        val aliceComment = CommentUIState(
            id = "c1", authorId = "user_alice", authorName = "Alice", authorAvatar = null,
            content = "Hi", timeAgo = "Vừa xong", likesCount = 0, isLiked = false, isAuthor = false,
            parentId = null, replyToName = null, replies = emptyList()
        )
        // Set State trực tiếp qua Reflection hoặc nạp qua loadComments
        // Cách tốt nhất là mock loadComments trước
        val commentsFromRepo = listOf(
            Comment(id = "c1", postId = "post123", userId = "user_alice", content = "Hi", createdAt = "2026-06-08T12:00:00Z", user = User(id = "user_alice", fullName = "Alice"))
        )
        coEvery { mockCommentRepository.getComments("post123", "user123") } returns commentsFromRepo
        viewModel.loadComments("post123", "owner123", "user123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Chọn c1 làm reply target
        viewModel.setReplyingTo(viewModel.comments.value[0])

        coEvery {
            mockCommentRepository.insertComment(
                postId = "post123",
                userId = "user123",
                content = "Reply content",
                receiverId = "user_alice", // Gửi cho chủ nhân comment c1
                parentId = "c1"
            )
        } returns "reply_id"

        viewModel.sendComment(
            postId = "post123",
            userId = "user123",
            content = "Reply content",
            postOwnerId = "owner123",
            currentUserName = "John Doe"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Phải cập nhật local state reply cho c1
        val uiComments = viewModel.comments.value
        assertEquals(1, uiComments.size)
        assertEquals(1, uiComments[0].replies.size)
        assertEquals("reply_id", uiComments[0].replies[0].id)
        assertEquals("Reply content", uiComments[0].replies[0].content)
        assertEquals("Alice", uiComments[0].replies[0].replyToName)

        // Phải reset trạng thái replyingTo
        assertNull(viewModel.replyingTo.value)

        // Phải trigger push cho Alice
        coVerify(exactly = 1) {
            mockNotificationRepository.triggerPushNotification(
                receiverId = "user_alice",
                title = "Phản hồi mới",
                body = "John Doe đã phản hồi: Reply content",
                type = "comment",
                postId = "post123",
                targetId = "reply_id"
            )
        }
    }

    @Test
    fun `toggleLikeComment - Success - Optimistically updates UI first and calls repo`() = runTest(testDispatcher) {
        val commentsFromRepo = listOf(
            Comment(id = "c1", postId = "post123", userId = "user_alice", content = "Hi", createdAt = "2026-06-08T12:00:00Z", user = User(id = "user_alice", fullName = "Alice"))
        )
        coEvery { mockCommentRepository.getComments("post123", "user123") } returns commentsFromRepo
        viewModel.loadComments("post123", "owner123", "user123")
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery {
            mockCommentRepository.toggleLike(
                commentId = "c1",
                userId = "user123",
                isLiked = any(),
                commentOwnerId = "user_alice",
                postId = "post123"
            )
        } returns Unit

        viewModel.toggleLikeComment(
            commentId = "c1",
            currentUserId = "user123",
            postId = "post123",
            postOwnerId = "owner123",
            currentUserName = "John Doe"
        )
        
        // Optimistic UI check (Chưa cần await background job kết thúc)
        val comment = viewModel.comments.value[0]
        assertTrue(comment.isLiked)
        assertEquals(1, comment.likesCount)

        testDispatcher.scheduler.advanceUntilIdle()

        // Phải gửi push cho Alice báo có người thích
        coVerify(exactly = 1) {
            mockNotificationRepository.triggerPushNotification(
                receiverId = "user_alice",
                title = "Lượt thích mới",
                body = "John Doe đã thích bình luận của bạn",
                type = "like",
                postId = "post123"
            )
        }
    }

    @Test
    fun `toggleLikeComment - Failure - Rolls back UI changes`() = runTest(testDispatcher) {
        val commentsFromRepo = listOf(
            Comment(id = "c1", postId = "post123", userId = "user_alice", content = "Hi", createdAt = "2026-06-08T12:00:00Z", user = User(id = "user_alice", fullName = "Alice"))
        )
        coEvery { mockCommentRepository.getComments("post123", "user123") } returns commentsFromRepo
        viewModel.loadComments("post123", "owner123", "user123")
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery {
            mockCommentRepository.toggleLike(
                commentId = "c1",
                userId = "user123",
                isLiked = any(),
                commentOwnerId = "user_alice",
                postId = "post123"
            )
        } throws Exception("Network Error")

        viewModel.toggleLikeComment(
            commentId = "c1",
            currentUserId = "user123",
            postId = "post123",
            postOwnerId = "owner123",
            currentUserName = "John Doe"
        )

        testDispatcher.scheduler.advanceUntilIdle()

        // Phải hoàn tác về trạng thái ban đầu do bị lỗi mạng
        val comment = viewModel.comments.value[0]
        assertFalse(comment.isLiked)
        assertEquals(0, comment.likesCount)
        assertEquals("Không thể thực hiện tương tác. Vui lòng thử lại.", viewModel.error.value)
    }
}
