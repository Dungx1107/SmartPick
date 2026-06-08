package com.example.smartpick.core.data.mapper

import com.example.smartpick.BaseUnitTest
import com.example.smartpick.core.data.dto.*
import com.example.smartpick.core.model.*
import com.example.smartpick.features.comment.data.dto.CommentResponse
import com.example.smartpick.features.feed.data.dto.FullPostResponse
import org.junit.Assert.*
import org.junit.Test

class DataMappersTest : BaseUnitTest() {

    @Test
    fun `User mapping - DTO to Domain and back`() {
        val dto = UserDto(
            id = "u1",
            email = "user@test.com",
            username = "testuser",
            fullName = "Test User",
            avatarUrl = "https://avatar.com/1",
            phoneNumber = "0912345678",
            createdAt = "2026-06-08T12:00:00Z",
            updatedAt = "2026-06-08T13:00:00Z"
        )

        // DTO -> Domain
        val domain = dto.toDomain()
        assertEquals(dto.id, domain.id)
        assertEquals(dto.email, domain.email)
        assertEquals(dto.username, domain.username)
        assertEquals(dto.fullName, domain.fullName)
        assertEquals(dto.avatarUrl, domain.avatarUrl)
        assertEquals(dto.phoneNumber, domain.phoneNumber)
        assertEquals(dto.createdAt, domain.createdAt)
        assertEquals(dto.updatedAt, domain.updatedAt)

        // Domain -> DTO
        val backToDto = domain.toDto()
        assertEquals(domain.id, backToDto.id)
        assertEquals(domain.email, backToDto.email)
        assertEquals(domain.username, backToDto.username)
        assertEquals(domain.fullName, backToDto.fullName)
        assertEquals(domain.avatarUrl, backToDto.avatarUrl)
        assertEquals(domain.phoneNumber, backToDto.phoneNumber)
        assertEquals(domain.createdAt, backToDto.createdAt)
        assertEquals(domain.updatedAt, backToDto.updatedAt)
    }

    @Test
    fun `Product mapping - DTO to Domain and back`() {
        val sellerProfile = SellerProfileDto(fullName = "Seller FullName")
        val dto = ProductDto(
            id = "p1",
            ownerId = "s1",
            name = "Awesome Shoes",
            brand = "Nike",
            category = "Shoes",
            price = 1500000.0,
            imageUrls = listOf("img1.png", "img2.png"),
            videoUrl = "video.mp4",
            status = "active",
            stock = 10,
            soldCount = 2,
            createdAt = "2026-06-08T10:00:00Z",
            postId = "post1",
            sellerProfile = sellerProfile
        )

        // DTO -> Domain
        val domain = dto.toDomain()
        assertEquals(dto.id, domain.id)
        assertEquals(dto.ownerId, domain.ownerId)
        assertEquals(dto.name, domain.name)
        assertEquals(dto.brand, domain.brand)
        assertEquals(dto.category, domain.category)
        assertEquals(dto.price, domain.price, 0.0)
        assertEquals(dto.imageUrls, domain.imageUrls)
        assertEquals(dto.videoUrl, domain.videoUrl)
        assertEquals(dto.status, domain.status)
        assertEquals(dto.stock, domain.stock)
        assertEquals(dto.soldCount, domain.soldCount)
        assertEquals(dto.createdAt, domain.createdAt)
        assertEquals(dto.postId, domain.postId)
        assertEquals("Seller FullName", domain.ownerName)

        // Domain -> DTO
        val backToDto = domain.toDto()
        assertEquals(domain.id, backToDto.id)
        assertEquals(domain.ownerId, backToDto.ownerId)
        assertEquals(domain.name, backToDto.name)
        assertEquals(domain.brand, backToDto.brand)
        assertEquals(domain.category, backToDto.category)
        assertEquals(domain.price, backToDto.price, 0.0)
        assertEquals(domain.imageUrls, backToDto.imageUrls)
        assertEquals(domain.videoUrl, backToDto.videoUrl)
        assertEquals(domain.status, backToDto.status)
        assertEquals(domain.stock, backToDto.stock)
        assertEquals(domain.soldCount, backToDto.soldCount)
        assertEquals(domain.createdAt, backToDto.createdAt)
        assertEquals(domain.postId, backToDto.postId)
    }

    @Test
    fun `Post mapping - DTO to Domain and back`() {
        val dto = PostDto(
            id = "po1",
            userId = "u1",
            productId = "p1",
            content = "Great post",
            mediaUrls = listOf("media1.png"),
            createdAt = "2026-06-08T11:00:00Z"
        )

        // DTO -> Domain
        val domain = dto.toDomain()
        assertEquals(dto.id, domain.id)
        assertEquals(dto.userId, domain.userId)
        assertEquals(dto.productId, domain.productId)
        assertEquals(dto.content, domain.content)
        assertEquals(dto.mediaUrls, domain.mediaUrls)
        assertEquals(dto.createdAt, domain.createdAt)

        // Domain -> DTO
        val backToDto = domain.toDto()
        assertEquals(domain.id, backToDto.id)
        assertEquals(domain.userId, backToDto.userId)
        assertEquals(domain.productId, backToDto.productId)
        assertEquals(domain.content, backToDto.content)
        assertEquals(domain.mediaUrls, backToDto.mediaUrls)
        assertEquals(domain.createdAt, backToDto.createdAt)
    }

    @Test
    fun `Notification mapping - DTO to Domain and back`() {
        val dto = NotificationDto(
            id = "n1",
            receiverId = "r1",
            senderId = "s1",
            postId = "po1",
            type = "like",
            content = "User liked your post",
            isRead = false,
            createdAt = "2026-06-08T12:30:00Z",
            title = "New Like",
            targetId = "t1"
        )

        // DTO -> Domain
        val domain = dto.toDomain()
        assertEquals(dto.id, domain.id)
        assertEquals(dto.receiverId, domain.receiverId)
        assertEquals(dto.senderId, domain.senderId)
        assertEquals(dto.postId, domain.postId)
        assertEquals(dto.type, domain.type)
        assertEquals(dto.content, domain.content)
        assertEquals(dto.isRead, domain.isRead)
        assertEquals(dto.createdAt, domain.createdAt)
        assertEquals(dto.title, domain.title)
        assertEquals(dto.targetId, domain.targetId)

        // Domain -> DTO
        val backToDto = domain.toDto()
        assertEquals(domain.id, backToDto.id)
        assertEquals(domain.receiverId, backToDto.receiverId)
        assertEquals(domain.senderId, backToDto.senderId)
        assertEquals(domain.postId, backToDto.postId)
        assertEquals(domain.type, backToDto.type)
        assertEquals(domain.content, backToDto.content)
        assertEquals(domain.isRead, backToDto.isRead)
        assertEquals(domain.createdAt, backToDto.createdAt)
        assertEquals(domain.title, backToDto.title)
        assertEquals(domain.targetId, backToDto.targetId)
    }

    @Test
    fun `Comment mapping - DTO to Domain and back`() {
        val userDto = UserDto(id = "u1", email = "u@t.com", username = "user", fullName = "User Name")
        val response = CommentResponse(
            id = "c1",
            postId = "po1",
            userId = "u1",
            content = "Nice comment",
            createdAt = "2026-06-08T14:00:00Z",
            user = userDto,
            likesCount = 5,
            isLiked = true,
            parentId = "c0"
        )

        // Response -> Domain
        val domain = response.toDomain()
        assertEquals(response.id, domain.id)
        assertEquals(response.postId, domain.postId)
        assertEquals(response.userId, domain.userId)
        assertEquals(response.content, domain.content)
        assertEquals(response.createdAt, domain.createdAt)
        assertEquals(response.user.fullName, domain.user.fullName)
        assertEquals(response.likesCount, domain.likesCount)
        assertEquals(response.isLiked, domain.isLiked)
        assertEquals(response.parentId, domain.parentId)

        // Domain -> DTO
        val backToDto = domain.toDto()
        assertEquals(domain.id, backToDto.id)
        assertEquals(domain.postId, backToDto.postId)
        assertEquals(domain.userId, backToDto.userId)
        assertEquals(domain.content, backToDto.content)
        assertEquals(domain.parentId, backToDto.parentId)
        assertEquals(domain.createdAt, backToDto.createdAt)
    }

    @Test
    fun `CartItem mapping - DTO to Domain and back`() {
        val productDto = ProductDto(id = "p1", ownerId = "s1", name = "Shoes", price = 1000.0)
        val dto = CartItemDto(
            id = "cart1",
            userId = "u1",
            productId = "p1",
            quantity = 3,
            postId = "po1",
            createdAt = "2026-06-08T15:00:00Z",
            updatedAt = "2026-06-08T16:00:00Z",
            products = productDto
        )

        // DTO -> Domain
        val domain = dto.toDomain()
        assertEquals(dto.id, domain.id)
        assertEquals(dto.userId, domain.userId)
        assertEquals(dto.productId, domain.productId)
        assertEquals(dto.quantity, domain.quantity)
        assertEquals(dto.postId, domain.originPostId)
        assertEquals(dto.createdAt, domain.createdAt)
        assertEquals(dto.updatedAt, domain.updatedAt)
        assertNotNull(domain.product)
        assertEquals("Shoes", domain.product?.name)

        // Domain -> DTO
        val backToDto = domain.toDto()
        assertEquals(domain.id, backToDto.id)
        assertEquals(domain.userId, backToDto.userId)
        assertEquals(domain.productId, backToDto.productId)
        assertEquals(domain.quantity, backToDto.quantity)
        assertEquals(domain.originPostId, backToDto.postId)
        assertEquals(domain.createdAt, backToDto.createdAt)
        assertEquals(domain.updatedAt, backToDto.updatedAt)
    }

    @Test
    fun `Review mapping - DTO to Domain and back`() {
        val userDto = ReviewUserDto(id = "u1", fullName = "Reviewer", avatarUrl = "avatar.png")
        val prodDto = ProductDto(id = "p1", ownerId = "s1", name = "Product Name", price = 2000.0)
        
        // ReviewUser mapping
        val reviewer = userDto.toDomain()
        assertEquals(userDto.id, reviewer.id)
        assertEquals(userDto.fullName, reviewer.fullName)
        assertEquals(userDto.avatarUrl, reviewer.avatarUrl)

        val backToReviewUserDto = reviewer.toDto()
        assertEquals(reviewer.id, backToReviewUserDto.id)
        assertEquals(reviewer.fullName, backToReviewUserDto.fullName)
        assertEquals(reviewer.avatarUrl, backToReviewUserDto.avatarUrl)

        // ReviewResponseDto mapping
        val responseDto = ReviewResponseDto(
            id = "r1",
            userId = "u1",
            productId = "p1",
            orderItemId = "oi1",
            rating = 5,
            content = "Excellent product!",
            createdAt = "2026-06-08T17:00:00Z",
            products = prodDto,
            user = userDto
        )

        // DTO -> Domain
        val domain = responseDto.toDomain()
        assertEquals(responseDto.id, domain.id)
        assertEquals(responseDto.userId, domain.userId)
        assertEquals(responseDto.productId, domain.productId)
        assertEquals(responseDto.orderItemId, domain.orderItemId)
        assertEquals(responseDto.rating, domain.rating)
        assertEquals(responseDto.content, domain.content)
        assertEquals(responseDto.createdAt, domain.createdAt)
        assertNotNull(domain.product)
        assertNotNull(domain.user)

        // Domain -> DTO
        val backToDto = domain.toDto()
        assertEquals(domain.id, backToDto.id)
        assertEquals(domain.userId, backToDto.userId)
        assertEquals(domain.productId, backToDto.productId)
        assertEquals(domain.orderItemId, backToDto.orderItemId)
        assertEquals(domain.rating, backToDto.rating)
        assertEquals(domain.content, backToDto.content)
        assertEquals(domain.createdAt, backToDto.createdAt)
    }

    @Test
    fun `Order mapping - DTO to Domain`() {
        val prodMinDto = ProductMinDto(id = "p1", name = "Order Product", imageUrls = listOf("p1.png"))
        val orderItemDto = OrderItemWithProductDto(
            id = "oi1",
            productId = "p1",
            quantity = 2,
            priceAtPurchase = 300.0,
            products = prodMinDto
        )
        val responseDto = OrderResponseDto(
            id = "ord1",
            userId = "u1",
            totalAmount = 600.0,
            shippingAddress = "123 Main St",
            phoneNumber = "0987654321",
            paymentMethod = "COD",
            status = "completed",
            createdAt = "2026-06-08T18:00:00Z",
            orderItems = listOf(orderItemDto)
        )

        val domain = responseDto.toDomain()
        assertEquals(responseDto.id, domain.id)
        assertEquals(responseDto.userId, domain.userId)
        assertEquals(responseDto.totalAmount, domain.totalAmount, 0.0)
        assertEquals(responseDto.shippingAddress, domain.shippingAddress)
        assertEquals(responseDto.phoneNumber, domain.phoneNumber)
        assertEquals(responseDto.paymentMethod, domain.paymentMethod)
        assertEquals(responseDto.status, domain.status)
        assertEquals(responseDto.createdAt, domain.createdAt)
        assertEquals(1, domain.items.size)
        assertEquals("oi1", domain.items[0].id)
        assertEquals("p1", domain.items[0].productId)
        assertEquals(2, domain.items[0].quantity)
        assertEquals(300.0, domain.items[0].priceAtPurchase, 0.0)
        assertEquals("Order Product", domain.items[0].productName)
        assertEquals("p1.png", domain.items[0].productImageUrl)
    }

    @Test
    fun `FullPostResponse toPostDomain - Mapping check`() {
        val fullPost = FullPostResponse(
            id = "f1",
            userId = "u1",
            productId = "p1",
            content = "Full content",
            mediaUrls = listOf("url1.png"),
            createdAt = "2026-06-08T19:00:00Z"
        )
        val domain = fullPost.toPostDomain()
        assertEquals(fullPost.id, domain.id)
        assertEquals(fullPost.userId, domain.userId)
        assertEquals(fullPost.productId, domain.productId)
        assertEquals(fullPost.content, domain.content)
        assertEquals(fullPost.mediaUrls, domain.mediaUrls)
        assertEquals(fullPost.createdAt, domain.createdAt)
    }
}
