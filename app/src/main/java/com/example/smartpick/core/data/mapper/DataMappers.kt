package com.example.smartpick.core.data.mapper

import com.example.smartpick.core.data.dto.*
import com.example.smartpick.core.model.*
import com.example.smartpick.features.comment.data.dto.CommentResponse
import com.example.smartpick.features.feed.data.dto.FullPostResponse

// --- User Mappers ---
fun UserDto.toDomain(): User = User(
    id = id,
    email = email,
    username = username,
    fullName = fullName,
    avatarUrl = avatarUrl,
    phoneNumber = phoneNumber,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun User.toDto(): UserDto = UserDto(
    id = id,
    email = email,
    username = username,
    fullName = fullName,
    avatarUrl = avatarUrl,
    phoneNumber = phoneNumber,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// --- Product Mappers ---
fun ProductDto.toDomain(): Product = Product(
    id = id,
    ownerId = ownerId,
    name = name,
    brand = brand,
    category = category,
    price = price,
    imageUrls = imageUrls,
    videoUrl = videoUrl,
    status = status,
    stock = stock,
    soldCount = soldCount,
    createdAt = createdAt
)

fun Product.toDto(): ProductDto = ProductDto(
    id = id,
    ownerId = ownerId,
    name = name,
    brand = brand,
    category = category,
    price = price,
    imageUrls = imageUrls,
    videoUrl = videoUrl,
    status = status,
    stock = stock,
    soldCount = soldCount,
    createdAt = createdAt
)

// --- Post Mappers ---
fun PostDto.toDomain(): Post = Post(
    id = id,
    userId = userId,
    productId = productId,
    content = content,
    mediaUrls = mediaUrls,
    createdAt = createdAt
)

fun Post.toDto(): PostDto = PostDto(
    id = id,
    userId = userId,
    productId = productId,
    content = content,
    mediaUrls = mediaUrls,
    createdAt = createdAt
)

// --- Notification Mappers ---
fun NotificationDto.toDomain(): Notification = Notification(
    id = id ?: "",
    receiverId = receiverId,
    senderId = senderId,
    postId = postId,
    type = type,
    content = content,
    isRead = isRead,
    createdAt = createdAt,
    title = title,
    targetId = targetId
)

fun Notification.toDto(): NotificationDto = NotificationDto(
    id = id.ifEmpty { java.util.UUID.randomUUID().toString() },
    receiverId = receiverId,
    senderId = senderId,
    postId = postId,
    type = type,
    content = content,
    isRead = isRead,
    createdAt = createdAt,
    title = title,
    targetId = targetId
)

// --- Comment Mappers ---
fun CommentResponse.toDomain(): Comment = Comment(
    id = id,
    postId = postId,
    userId = userId,
    content = content,
    createdAt = createdAt,
    user = user.toDomain(),
    likesCount = likesCount,
    isLiked = isLiked,
    parentId = parentId
)

fun Comment.toDto(): CommentDto = CommentDto(
    id = id,
    postId = postId,
    userId = userId,
    content = content,
    parentId = parentId,
    createdAt = createdAt
)

fun CartDto.toDomain(): CartItem = CartItem(
    id = id,
    userId = userId,
    productId = productId,
    quantity = quantity,
    createdAt = createdAt,
    updatedAt = updatedAt,
    product = products?.toDomain()
)

fun CartItem.toDto(): CartDto = CartDto(
    id = id,
    userId = userId,
    productId = productId,
    quantity = quantity,
    createdAt = createdAt,
    updatedAt = updatedAt,
    products = product?.toDto()
)

// --- Review Mappers ---
fun ReviewUserDto.toDomain(): ReviewUser = ReviewUser(
    id = id,
    fullName = fullName,
    avatarUrl = avatarUrl
)

fun ReviewUser.toDto(): ReviewUserDto = ReviewUserDto(
    id = id,
    fullName = fullName,
    avatarUrl = avatarUrl
)

fun ReviewResponseDto.toDomain(): Review = Review(
    id = id,
    userId = userId,
    productId = productId,
    rating = rating,
    content = content,
    createdAt = createdAt,
    product = products?.toDomain(),
    user = user?.toDomain()
)

fun Review.toDto(): ReviewResponseDto = ReviewResponseDto(
    id = id,
    userId = userId,
    productId = productId,
    rating = rating,
    content = content,
    createdAt = createdAt,
    products = product?.toDto(),
    user = user?.toDto()
)

// --- Order Mappers ---
fun OrderResponseDto.toDomain(): Order = Order(
    id = id,
    totalAmount = totalAmount,
    status = status,
    createdAt = createdAt
)

// --- Complex Mapping (Feature specific) ---
fun FullPostResponse.toPostDomain(): Post {
    return Post(
        id = id,
        userId = userId,
        productId = productId,
        content = content,
        mediaUrls = mediaUrls,
        createdAt = createdAt
    )
}