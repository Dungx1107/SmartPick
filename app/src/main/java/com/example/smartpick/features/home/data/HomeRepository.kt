// File: app/src/main/java/com/example/smartpick/features/home/data/HomeRepository.kt
package com.example.smartpick.features.home.data

import android.util.Log
import com.example.smartpick.core.data.dto.CartDto
import com.example.smartpick.core.data.dto.CartItemRequest
import com.example.smartpick.core.data.dto.ProductDto
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.OrderItemRequest
import com.example.smartpick.core.model.OrderRequest
import com.example.smartpick.core.model.OrderResponse
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReviewRequest
import com.example.smartpick.core.model.ReviewResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    supabase: SupabaseClient
) {
    private val postgrest = supabase.postgrest
    private val TAG = "HomeRepository"

    suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            val listDto = postgrest["products"].select().decodeList<ProductDto>()
            listDto.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "getAllProducts error", e)
            emptyList()
        }
    }

    suspend fun getPostIdByProductId(productId: String): String? = withContext(Dispatchers.IO) {
        try {
            val response = postgrest["posts"].select(Columns.raw("id")) {
                filter { eq("product_id", productId) }
                limit(1)
            }.decodeSingleOrNull<Map<String, String>>()
            response?.get("id")
        } catch (e: Exception) {
            Log.e(TAG, "getPostIdByProductId error", e)
            null
        }
    }

    suspend fun getCartItems(userId: String): List<CartItem> = withContext(Dispatchers.IO) {
        try {
            val listDto = postgrest["cart_items"].select(Columns.raw("*, products(*)")) {
                filter { eq("user_id", userId) }
            }.decodeList<CartDto>()
            listDto.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "getCartItems error", e)
            emptyList()
        }
    }

    suspend fun addToCart(userId: String, productId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existingDto = postgrest["cart_items"].select {
                filter { eq("user_id", userId); eq("product_id", productId) }
            }.decodeSingleOrNull<CartDto>()
            if (existingDto != null) {
                val existing = existingDto.toDomain()
                postgrest["cart_items"].update({ set("quantity", existing.quantity + 1) }) { filter { eq("id", existing.id!!) } }
            } else {
                val newItem = CartItemRequest(userId = userId, productId = productId, quantity = 1)
                postgrest["cart_items"].insert(newItem)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromCart(cartItemId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["cart_items"].delete { filter { eq("id", cartItemId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCartItemQuantity(cartItemId: String, newQuantity: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (newQuantity <= 0) return@withContext removeFromCart(cartItemId)
            postgrest["cart_items"].update({ set("quantity", newQuantity) }) { filter { eq("id", cartItemId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkout(
        userId: String,
        cartItems: List<CartItem>,
        address: String,
        phone: String,
        paymentMethod: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (cartItems.isEmpty()) return@withContext Result.failure(Exception("Giỏ hàng trống"))

            val totalAmount = cartItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }

            // 1. Insert Order
            val orderRequest = OrderRequest(
                userId = userId,
                totalAmount = totalAmount,
                shippingAddress = address,
                phoneNumber = phone,
                paymentMethod = paymentMethod
            )
            val orderResponse = postgrest["orders"].insert(orderRequest) { select() }.decodeSingle<OrderResponse>()

            // 2. Insert Order Items
            val orderItems = cartItems.map { item ->
                OrderItemRequest(
                    orderId = orderResponse.id,
                    productId = item.productId,
                    quantity = item.quantity,
                    priceAtPurchase = item.product?.price ?: 0.0
                )
            }
            postgrest["order_items"].insert(orderItems)

            // 3. Update Product Stock & Sold Count
            cartItems.forEach { item ->
                val currentProduct = item.product ?: return@forEach
                postgrest["products"].update({
                    set("stock", currentProduct.stock - item.quantity)
                    set("sold_count", currentProduct.soldCount + item.quantity)
                }) {
                    filter { eq("id", item.productId) }
                }
            }

            // 4. Clear Cart
            postgrest["cart_items"].delete { filter { eq("user_id", userId) } }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Checkout error", e)
            Result.failure(e)
        }
    }

    suspend fun getOrders(userId: String): List<OrderResponse> = withContext(Dispatchers.IO) {
        try {
            postgrest["orders"].select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<OrderResponse>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Lấy danh sách đánh giá của sản phẩm
     */
    suspend fun getProductReviews(productId: String): List<ReviewResponse> = withContext(Dispatchers.IO) {
        try {
            postgrest["reviews"].select {
                filter { eq("product_id", productId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<ReviewResponse>()
        } catch (e: Exception) {
            Log.e(TAG, "getProductReviews error", e)
            emptyList()
        }
    }

    /**
     * Kiểm tra User đã mua sản phẩm này chưa
     */
    suspend fun checkUserBoughtProduct(userId: String, productId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = postgrest["order_items"].select(Columns.raw("id, orders!inner(user_id)")) {
                filter {
                    eq("product_id", productId)
                    eq("orders.user_id", userId)
                }
            }.decodeList<Map<String, String>>()

            response.isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "checkUserBoughtProduct error", e)
            false
        }
    }

    /**
     * Gửi đánh giá mới
     */
    suspend fun submitReview(request: ReviewRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["reviews"].insert(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "submitReview error", e)
            Result.failure(e)
        }
    }
}