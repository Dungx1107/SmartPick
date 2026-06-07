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
import kotlinx.serialization.json.put

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
     * Hàm xử lý đặt hàng (Checkout) chính thức - Giữ nguyên giỏ hàng sau khi mua
     */
    suspend fun checkout(
        userId: String,
        cartItems: List<CartItem>,
        address: String,
        phone: String,
        paymentMethod: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "==== BẮT ĐẦU CHECKOUT TOÀN DIỆN ====")
            Log.d(TAG, "UserId: $userId, Items count: ${cartItems.size}")

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
            Log.d(TAG, "[Step 1] Thành công! OrderId mới: ${orderResponse.id}")

            // [Step 2] Đóng gói và chèn dữ liệu chi tiết hóa đơn vào bảng order_items
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

            // [Step 3] Phát thông báo hệ thống và tin đẩy cho cả hai bên
            Log.d(TAG, "[Step 3] Bắt đầu kích hoạt luồng thông báo cho các bên...")
            sendOrderNotificationsToOwners(
                cartItems,
                orderResponse.id,
                buyerId = userId
            )

            // [Step 4] Đồng bộ trừ kho và tăng số lượng bán dựa trên dữ liệu thời gian thực của Server
            Log.d(TAG, "[Step 4] Bắt đầu cập nhật lại kho và số lượng đã bán của sản phẩm...")
            cartItems.forEach { item ->
                val product = item.product
                if (product != null && !product.id.isNullOrEmpty()) {
                    runCatching {
                        val dbResponse = postgrest["products"].select {
                            filter { eq("id", product.id) }
                        }.decodeSingleOrNull<com.example.smartpick.core.data.dto.ProductDto>()

                        val currentStockInDb = dbResponse?.stock ?: product.stock
                        val currentSoldCountInDb = dbResponse?.soldCount ?: product.soldCount

                        val newStock = (currentStockInDb - item.quantity).coerceAtLeast(0)
                        val newSoldCount = currentSoldCountInDb + item.quantity

                        val updateStockJson = kotlinx.serialization.json.buildJsonObject {
                            put("stock", newStock)
                            put("sold_count", newSoldCount)
                        }

                        postgrest["products"].update(updateStockJson) {
                            filter { eq("id", product.id) }
                        }

                        Log.d(
                            TAG,
                            "    -> [SERVER UPDATE SUCCESS] Sản phẩm [${product.id}]: Tồn kho mới = $newStock, Số lượng bán mới = $newSoldCount"
                        )
                    }.onFailure { error ->
                        Log.e(TAG, "Lỗi trừ kho cục bộ tại sản phẩm [${product.id}]: ${error.message}")
                    }
                }
            }

            // [Step 5] THEO YÊU CẦU: Không cập nhật, không xóa bất cứ thứ gì ở giỏ hàng sau khi mua thành công
            Log.d(TAG, "[Step 5] Bỏ qua hành động dọn dẹp giỏ hàng theo thiết kế.")

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
            Log.d(TAG, "==== BẮT ĐẦU LẤY LỊCH SỬ ĐƠN HÀNG NÂNG CAO ====")

            val response = postgrest["orders"]
                .select(columns = Columns.raw("*, order_items(*, products(*))")) {
                    filter { eq("user_id", userId) }
                    order("created_at", SupabaseOrder.DESCENDING)
                }

            val dtoList = response.decodeList<OrderResponseDto>()
            val ordersList = dtoList.map { it.toDomain() }

            Log.d(TAG, "Lấy lịch sử đơn hàng nâng cao thành công, số lượng: ${ordersList.size}")
            ordersList
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
                postId = "00000000-0000-0000-0000-000000000000",
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
                Log.d(TAG, "    [FCM BUYER SUCCESS] Đã bắn FCM cho người mua.")
            }.onFailure { fcmError ->
                Log.w(TAG, "    [FCM BUYER LỖI] Bỏ qua lỗi gửi tin đẩy tới thiết bị Buyer: ${fcmError.localizedMessage}")
            }

            // --- PHẦN 2: TẠO THÔNG BÁO CHO TỪNG NGƯỜI BÁN (SELLER) ---
            cartItems.forEach { item ->
                val product = item.product ?: return@forEach
                val ownerId = product.ownerId
                if (ownerId.isBlank()) {
                    Log.w(TAG, "CẢNH BÁO: Phát hiện sản phẩm không có ownerId. Bỏ qua.")
                    return@forEach
                }

                val sellerContent = "Bạn đã bán được sản phẩm '${product.name}' thuộc đơn hàng này."
                Log.d(TAG, "--> Đang gửi thông báo tới Owner: $ownerId cho sản phẩm: ${product.name}")

                val validPostId = if (!item.originPostId.isNullOrBlank() && item.originPostId != "null") {
                    item.originPostId
                } else {
                    "00000000-0000-0000-0000-000000000000"
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
                    Log.d(TAG, "[DB] Đã lưu thông báo người bán vào bảng notifications thành công.")
                }.onFailure { err ->
                    Log.e(TAG, "[DB LỖI] Bỏ qua lỗi lưu thông báo Seller: ${err.message}")
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
                    Log.d(TAG, "    [FCM SELLER SUCCESS] Đã bắn FCM cho người bán.")
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