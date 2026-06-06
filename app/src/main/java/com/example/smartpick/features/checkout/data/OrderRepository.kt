package com.example.smartpick.features.checkout.data

import android.util.Log
import com.example.smartpick.core.data.dto.OrderItemRequestDto
import com.example.smartpick.core.data.dto.OrderRequestDto
import com.example.smartpick.core.data.dto.OrderResponseDto
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Notification
import com.example.smartpick.features.notification.data.NotificationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

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

    suspend fun checkout(
        userId: String,
        cartItems: List<CartItem>,
        address: String,
        phone: String,
        paymentMethod: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "==== BẮT ĐẦU CHECKOUT ====")
            Log.d(TAG, "UserId: $userId, Items count: ${cartItems.size}")

            if (cartItems.isEmpty()) return@withContext Result.failure(Exception("Giỏ hàng trống"))

            val totalAmount = cartItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }
            val orderRequest = OrderRequestDto(
                userId = userId,
                totalAmount = totalAmount,
                shippingAddress = address,
                phoneNumber = phone,
                paymentMethod = paymentMethod
            )

            Log.d(TAG, "[Step 1] Đang chèn vào bảng orders...")
            val orderResponse = postgrest["orders"].insert(orderRequest) { select() }.decodeSingle<OrderResponseDto>()
            Log.d(TAG, "[Step 1] Thành công! OrderId mới: ${orderResponse.id}")

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
            Log.d(TAG, "[Step 2] Chèn chi tiết đơn hàng thành công.")

            // 4. Gửi thông báo cho chủ hàng
            Log.d(TAG, "[Step 3] Bắt đầu kích hoạt luồng thông báo cho Seller...")
            sendOrderNotificationsToOwners(
                cartItems,
                orderResponse.id,
                buyerId = userId
            )

            Log.d(TAG, "[Step 4] Xóa giỏ hàng...")
            postgrest["cart_items"].delete { filter { eq("user_id", userId) } }

            Log.d(TAG, "==== CHECKOUT HOÀN TẤT THÀNH CÔNG ====")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "!!!! LỖI CHECKOUT: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun sendOrderNotificationsToOwners(
        cartItems: List<CartItem>,
        orderId: String,
        buyerId: String
    ) {
        try {
            cartItems.forEach { item ->
                val product = item.product ?: return@forEach
                val ownerId = product.ownerId
                if (ownerId.isBlank()) {
                    Log.w(TAG, "CẢNH BÁO: Phát hiện sản phẩm không có ownerId. Bỏ qua.")
                    return@forEach
                }

                val content = "Bạn có đơn hàng mới! Khách hàng vừa đặt mua sản phẩm ${product.name}."

                Log.d(TAG, "--> Đang gửi thông báo tới Owner: $ownerId cho sản phẩm: ${product.name}")

                // A. Gửi thông báo In-app (Lưu Database)
                val notification = Notification(
                    receiverId = ownerId,
                    senderId = buyerId,
                    type = "ORDER",
                    title = "Đơn hàng mới",
                    content = content,
                    targetId = orderId
                )

                val dbResult = notificationRepository.sendNotification(notification)
                if (dbResult.isSuccess) {
                    Log.d(TAG, "[DB] Đã lưu thông báo vào bảng notifications thành công.")
                } else {
                    Log.e(TAG, "[DB] THẤT BẠI khi lưu thông báo: ${dbResult.exceptionOrNull()?.message}")
                }

                // B. Gửi Push Notification (Edge Function)
                Log.d(TAG, "    [FCM] Đang gọi triggerPushNotification...")
                notificationRepository.triggerPushNotification(
                    receiverId = ownerId,
                    title = "Đơn hàng mới",
                    body = "Bạn vừa nhận được một đơn đặt hàng mới.",
                    type = "order",
                    targetId = orderId
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi trong quá trình gửi thông báo: ${e.message}", e)
        }
    }

    suspend fun getLastOrderInfo(userId: String): LastOrderInfoDto? = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest["orders"]
                .select(columns = Columns.raw("phone_number, shipping_address")) {
                    filter { eq("user_id", userId) }
                    order("created_at", Order.DESCENDING)
                    limit(1)
                }.decodeSingleOrNull<LastOrderInfoDto>()
        } catch (e: Exception) {
            null
        }
    }
}