package com.example.smartpick.features.checkout.data

import android.util.Log
import com.example.smartpick.core.data.dto.OrderItemRequestDto
import com.example.smartpick.core.data.dto.OrderRequestDto
import com.example.smartpick.core.data.dto.OrderResponseDto
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Notification
import com.example.smartpick.core.model.Order
import com.example.smartpick.features.notification.data.NotificationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order as SupabaseOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LastOrderInfoDto(
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("shipping_address") val shippingAddress: String? = null
)

@Singleton
class OrderRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val notificationRepository: NotificationRepository
) {
    private val postgrest = supabase.postgrest
    private val TAG = "OrderRepository"

    /**
     * Hàm xử lý đặt hàng (Checkout) chuẩn chỉ hệ thống
     */
    suspend fun checkout(
        userId: String,
        cartItems: List<CartItem>,
        address: String,
        phone: String,
        paymentMethod: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "==== BẮT ĐẦU CHECKOUT CHUẨN CHỈ HỆ THỐNG ====")
            if (cartItems.isEmpty()) return@withContext Result.failure(Exception("Giỏ hàng trống"))

            // [Step 1] Khởi tạo hóa đơn tổng trong bảng orders
            val totalAmount = cartItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }
            val orderRequest = OrderRequestDto(
                userId = userId,
                totalAmount = totalAmount,
                shippingAddress = address,
                phoneNumber = phone,
                paymentMethod = paymentMethod
            )

            Log.d(TAG, "[Step 1] Đang chèn vào bảng orders...")
            val orderResponse = postgrest["orders"].insert(orderRequest) { select() }
                .decodeSingle<OrderResponseDto>()

            // [Step 2] Chèn chi tiết hóa đơn vào bảng order_items -> Kích hoạt Server Trigger tự động trừ kho hàng
            Log.d(TAG, "[Step 2] Đang chèn ${cartItems.size} items vào order_items...")
            val orderItems = cartItems.map { item ->
                OrderItemRequestDto(
                    orderId = orderResponse.id,
                    productId = item.productId,
                    quantity = item.quantity,
                    priceAtPurchase = item.product?.price ?: 0.0
                )
            }
            postgrest["order_items"].insert(orderItems)

            // [Step 3] Điều phối thông báo và tin đẩy chuẩn chỉ cho các bên
            Log.d(TAG, "[Step 3] Bắt đầu kích hoạt luồng thông báo cho các bên...")
            sendOrderNotificationsToOwners(
                cartItems,
                orderResponse.id,
                buyerId = userId
            )

            // [Step 4] Đã lược bỏ cập nhật kho thủ công (Server Trigger đảm nhiệm)
            Log.d(TAG, "[Step 4] Luồng cập nhật kho tự động do Database Trigger xử lý.")

            // [Step 5] Giữ nguyên trạng thái giỏ hàng sau khi thanh toán thành công
            Log.d(TAG, "[Step 5] Giữ nguyên trạng thái giỏ hàng theo thiết kế.")

            Log.d(TAG, "==== CHECKOUT HOÀN TẤT THÀNH CÔNG VÀ ĐỒNG BỘ TOÀN DIỆN ====")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "!!!! LỖI GIAO DỊCH CHECKOUT: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Hàm lấy lịch sử mua hàng nâng cao kèm chi tiết cấu trúc cây của sản phẩm liên kết
     */
    suspend fun getOrderHistory(userId: String): List<Order> = withContext(Dispatchers.IO) {
        try {
            val response = postgrest["orders"]
                .select(columns = Columns.raw("*, order_items(*, products(*))")) {
                    filter { eq("user_id", userId) }
                    order("created_at", SupabaseOrder.DESCENDING)
                }
            response.decodeList<OrderResponseDto>().map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "!!!! LỖI LẤY LỊCH SỬ ĐƠN HÀNG: ${e.localizedMessage}", e)
            emptyList()
        }
    }

    /**
     * Điều hướng điều phối thông báo cho cả Người mua và Người bán tách biệt (Gồm DB và FCM)
     */
    private suspend fun sendOrderNotificationsToOwners(
        cartItems: List<CartItem>,
        orderId: String,
        buyerId: String
    ) {
        runCatching {
            val firstProduct = cartItems.firstOrNull()?.product

            // --- PHẦN 1: TẠO THÔNG BÁO CHO NGƯỜI MUA (BUYER) ---
            val buyerContent = if (cartItems.size > 1) {
                "Bạn đã mua thành công đơn hàng gồm sản phẩm '${firstProduct?.name}' và các món hàng khác."
            } else {
                "Bạn đã mua thành công đơn hàng sản phẩm '${firstProduct?.name}'."
            }

            val validSenderId = firstProduct?.ownerId?.ifBlank { buyerId } ?: buyerId

            val buyerNotification = Notification(
                id = "",
                receiverId = buyerId,
                senderId = validSenderId,
                type = "ORDER",
                title = "Đặt hàng thành công",
                content = buyerContent,
                targetId = orderId,
                postId = null,
                createdAt = ""
            )

            runCatching {
                notificationRepository.sendNotification(buyerNotification)
                Log.d(TAG, "[DB BUYER] Ghi nhận thành công bản ghi thông báo mua hàng cho khách.")
            }.onFailure { err ->
                Log.e(TAG, "[DB BUYER LỖI] Bỏ qua lỗi lưu thông báo người mua: ${err.message}")
            }

            Log.d(TAG, "    [FCM BUYER] Đang gọi triggerPushNotification cho Buyer...")
            runCatching {
                notificationRepository.triggerPushNotification(
                    receiverId = buyerId,
                    title = "Đặt hàng thành công",
                    body = buyerContent,
                    type = "order",
                    targetId = orderId
                )
            }.onFailure { fcmError ->
                Log.w(TAG, "    [FCM BUYER LỖI] Bỏ qua lỗi gửi tin đẩy tới thiết bị Buyer: ${fcmError.localizedMessage}")
            }

            // --- PHẦN 2: TẠO THÔNG BÁO CHO TỪNG NGƯỜI BÁN (SELLER) ---
            cartItems.forEach { item ->
                val product = item.product ?: return@forEach
                val ownerId = product.ownerId
                if (ownerId.isBlank()) return@forEach

                val sellerContent = "Bạn đã bán được sản phẩm '${product.name}' thuộc đơn hàng này."

                val validPostId = if (!item.originPostId.isNullOrBlank() && item.originPostId != "null") {
                    item.originPostId
                } else {
                    null
                }

                val sellerNotification = Notification(
                    id = "",
                    receiverId = ownerId,
                    senderId = buyerId,
                    type = "ORDER",
                    title = "Đã bán được sản phẩm",
                    content = sellerContent,
                    targetId = orderId,
                    postId = validPostId,
                    createdAt = ""
                )

                runCatching {
                    notificationRepository.sendNotification(sellerNotification)
                    Log.d(TAG, "[DB SELLER] Đã lưu thông báo người bán vào bảng notifications thành công.")
                }.onFailure { err ->
                    Log.e(TAG, "[DB SELLER LỖI] Bỏ qua lỗi lưu thông báo Seller: ${err.message}")
                }

                Log.d(TAG, "    [FCM SELLER] Đang gọi triggerPushNotification cho Seller...")
                runCatching {
                    notificationRepository.triggerPushNotification(
                        receiverId = ownerId,
                        title = "Đơn hàng mới",
                        body = "Bạn vừa bán được một sản phẩm mới từ cửa hàng.",
                        type = "order",
                        targetId = orderId
                    )
                }.onFailure { fcmError ->
                    Log.w(TAG, "    [FCM SELLER LỖI] Bỏ qua lỗi gửi tin đẩy tới thiết bị Seller: ${fcmError.localizedMessage}")
                }
            }
        }.onFailure { globalErr ->
            Log.e(TAG, "Lỗi tổng quát tại khối điều phối thông báo: ${globalErr.message}")
        }
    }

    /**
     * Lấy dữ liệu thông tin đơn hàng cũ để điền thông tin tự động
     */
    suspend fun getLastOrderInfo(userId: String): LastOrderInfoDto? = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest["orders"]
                .select(columns = Columns.raw("phone_number, shipping_address")) {
                    filter { eq("user_id", userId) }
                    order("created_at", SupabaseOrder.DESCENDING)
                    limit(1)
                }.decodeSingleOrNull<LastOrderInfoDto>()
        } catch (e: Exception) {
            null
        }
    }
}