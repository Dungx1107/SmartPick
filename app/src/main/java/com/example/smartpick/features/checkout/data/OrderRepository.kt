package com.example.smartpick.features.checkout.data

import android.util.Log
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.OrderItemRequest
import com.example.smartpick.core.model.OrderRequest
import com.example.smartpick.core.model.OrderResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    private val postgrest = supabase.postgrest
    private val TAG = "OrderRepository"

    /**
     * Thực hiện quy trình Transaction đặt hàng trên Supabase
     */
    suspend fun checkout(
        userId: String,
        cartItems: List<CartItem>,
        address: String,
        phone: String,
        paymentMethod: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (cartItems.isEmpty()) return@withContext Result.failure(Exception("Giỏ hàng trống"))

            // 1. Tính tổng tiền đơn hàng
            val totalAmount = cartItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }
            val orderRequest = OrderRequest(
                userId = userId,
                totalAmount = totalAmount,
                shippingAddress = address,
                phoneNumber = phone,
                paymentMethod = paymentMethod
            )

            // 2. Chèn thông tin vào bảng 'orders' và lấy thông tin phản hồi (chứa ID đơn hàng)
            val orderResponse = postgrest["orders"].insert(orderRequest) { select() }.decodeSingle<OrderResponse>()

            // 3. Tạo danh sách các item chi tiết dựa trên ID đơn hàng vừa tạo
            val orderItems = cartItems.map { item ->
                OrderItemRequest(
                    orderId = orderResponse.id,
                    productId = item.productId,
                    quantity = item.quantity,
                    priceAtPurchase = item.product?.price ?: 0.0
                )
            }
            postgrest["order_items"].insert(orderItems)

            // 4. Xóa toàn bộ giỏ hàng của user sau khi đặt hàng thành công
            postgrest["cart_items"].delete { filter { eq("user_id", userId) } }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Checkout error: ${e.message}", e)
            Result.failure(e)
        }
    }
}