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
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository chịu trách nhiệm xử lý dữ liệu cho Home Feature.
 *
 * Chức năng chính:
 * - Lấy danh sách sản phẩm
 * - Quản lý giỏ hàng
 * - Query dữ liệu từ Supabase
 * - Mapping DTO -> Domain Model
 * - Xử lý exception khi gọi database
 *
 * Kiến trúc:
 * ViewModel -> Repository -> Supabase
 */
@Singleton
class HomeRepository @Inject constructor(
    supabase: SupabaseClient
) {

    /* Dùng để thao tác database */
    private val postgrest = supabase.postgrest

    /* TAG cho Logcat */
    private val TAG = "HomeRepository"

    /**
     * Lấy toàn bộ sản phẩm từ bảng products.
     *
     * Flow:
     * 1. Query bảng products
     * 2. Decode JSON -> ProductDto
     * 3. Mapping DTO -> Domain
     * 4. Trả về List<Product>
     *
     * Dùng cho:
     * - Home Screen
     * - Search
     * - Recommendation
     *
     * @return Danh sách sản phẩm
     */
    suspend fun getAllProducts(): List<Product> =
        withContext(Dispatchers.IO) {
            try {
                /* Query products */
                val listDto = postgrest["products"]
                    .select()
                    .decodeList<ProductDto>()
                /* DTO -> Domain */
                listDto.map { it.toDomain() }
            } catch (e: Exception) {
                Log.e(TAG, "getAllProducts error", e)
                emptyList()
            }
        }

    /**
     * Lấy postId tương ứng với productId.
     *
     * Mục đích:
     * - Điều hướng sang màn hình bài viết
     * - Liên kết product với post
     *
     * Flow:
     * 1. Query bảng posts
     * 2. Filter theo product_id
     * 3. Chỉ lấy field id
     *
     * @param productId ID sản phẩm
     * @return postId hoặc null
     */
    suspend fun getPostIdByProductId(productId: String): String? =
        withContext(Dispatchers.IO) {
            try {
                /* Query post */
                val response = postgrest["posts"]
                    .select(Columns.raw("id")) {
                        filter {
                            eq("product_id", productId)
                        }
                        limit(1)
                    }
                    .decodeSingleOrNull<Map<String, String>>()
                response?.get("id")
            } catch (e: Exception) {
                Log.e(TAG, "getPostIdByProductId error", e)
                null
            }
        }

    /**
     * Lấy danh sách sản phẩm trong giỏ hàng.
     *
     * Flow:
     * 1. Query bảng cart_items
     * 2. Filter theo user_id
     * 3. Join với bảng products
     * 4. Mapping DTO -> Domain
     *
     * Kết quả trả về gồm:
     * - quantity
     * - product info
     * - image
     * - price
     *
     * @param userId ID người dùng
     * @return Danh sách cart item
     */
    suspend fun getCartItems(userId: String): List<CartItem> =
        withContext(Dispatchers.IO) {
            try {
                /* Query cart + products */
                val listDto = postgrest["cart_items"]
                    .select(Columns.raw("*, products(*)")) {
                        filter {
                            eq("user_id", userId)
                        }
                    }
                    .decodeList<CartDto>()
                /* DTO -> Domain */
                listDto.map { it.toDomain() }
            } catch (e: Exception) {
                Log.e(TAG, "getCartItems error", e)
                emptyList()
            }
        }


    /**
     * Thêm sản phẩm vào giỏ hàng.
     *
     * Logic:
     * - Nếu sản phẩm đã tồn tại:
     *      -> tăng quantity +1
     *
     * - Nếu chưa tồn tại:
     *      -> tạo cart item mới
     *
     * Mục đích:
     * - Tránh duplicate item
     * - Đồng bộ logic ecommerce
     *
     * @param userId ID người dùng
     * @param productId ID sản phẩm
     * @return Result<Unit>
     */
    suspend fun addToCart(
        userId: String,
        productId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            /* Kiểm tra item tồn tại */
            val existingDto = postgrest["cart_items"]
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("product_id", productId)
                    }
                }
                .decodeSingleOrNull<CartDto>()
            if (existingDto != null) {
                /* Tăng quantity */
                val existing = existingDto.toDomain()
                postgrest["cart_items"].update({
                    set("quantity", existing.quantity + 1)
                }) {
                    filter {
                        eq("id", existing.id!!)
                    }
                }
            } else {
                /* Tạo item mới */
                val newItem = CartItemRequest(
                    userId = userId,
                    productId = productId,
                    quantity = 1
                )
                postgrest["cart_items"].insert(newItem)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "addToCart error", e)
            Result.failure(e)
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng.
     *
     * Flow:
     * 1. Tìm item theo id
     * 2. Delete khỏi database
     *
     * @param cartItemId ID cart item
     * @return Result<Unit>
     */
    suspend fun removeFromCart(
        cartItemId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            /* Delete item */
            postgrest["cart_items"].delete {
                filter {
                    eq("id", cartItemId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "removeFromCart error", e)
            Result.failure(e)
        }
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng.
     *
     * Logic:
     * - Nếu quantity <= 0:
     *      -> xóa item
     *
     * - Nếu quantity > 0:
     *      -> update quantity mới
     *
     * @param cartItemId ID cart item
     * @param newQuantity số lượng mới
     * @return Result<Unit>
     */
    suspend fun updateCartItemQuantity(
        cartItemId: String,
        newQuantity: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            /* Quantity <= 0 thì xóa */
            if (newQuantity <= 0) {
                return@withContext removeFromCart(cartItemId)
            }
            /* Update quantity */
            postgrest["cart_items"].update({
                set("quantity", newQuantity)
            }) {
                filter {
                    eq("id", cartItemId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "updateCartItemQuantity error", e)
            Result.failure(e)
        }
    }

    suspend fun checkout(userId: String, cartItems: List<CartItem>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (cartItems.isEmpty()) return@withContext Result.failure(Exception("Giỏ hàng trống"))
            val totalAmount = cartItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }
            val orderRequest = OrderRequest(userId = userId, totalAmount = totalAmount)
            val orderResponse = postgrest["orders"].insert(orderRequest) { select() }.decodeSingle<OrderResponse>()
            val orderItems = cartItems.map { item ->
                OrderItemRequest(
                    orderId = orderResponse.id,
                    productId = item.productId,
                    quantity = item.quantity,
                    priceAtPurchase = item.product?.price ?: 0.0
                )
            }
            postgrest["order_items"].insert(orderItems)
            postgrest["cart_items"].delete { filter { eq("user_id", userId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy danh sách lịch sử đơn hàng
     */
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
}