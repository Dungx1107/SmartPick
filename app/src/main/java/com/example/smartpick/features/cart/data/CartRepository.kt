package com.example.smartpick.features.cart.data

import android.util.Log
import com.example.smartpick.core.data.dto.CartDto
import com.example.smartpick.core.data.dto.CartItemRequest
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.model.CartItem
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    private val postgrest = supabase.postgrest
    private val TAG = "CartRepository"

    // Bộ lưu trữ trạng thái giỏ hàng tập trung để đồng bộ UI toàn ứng dụng
    private val _cartItemsFlow = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItemsFlow: StateFlow<List<CartItem>> = _cartItemsFlow.asStateFlow()

    /**
     * Tải danh sách sản phẩm trong giỏ hàng và cập nhật vào Flow trung tâm
     */
    suspend fun fetchCartItems(userId: String): List<CartItem> = withContext(Dispatchers.IO) {
        try {
            val listDto = postgrest["cart_items"].select(Columns.raw("*, products(*)")) {
                filter { eq("user_id", userId) }
            }.decodeList<CartDto>()

            val items = listDto.map { it.toDomain() }
            _cartItemsFlow.value = items // Phát dữ liệu mới về Flow
            items
        } catch (e: Exception) {
            Log.e(TAG, "getCartItems error", e)
            emptyList()
        }
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    suspend fun addToCart(userId: String, productId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existingDto = postgrest["cart_items"].select {
                filter { eq("user_id", userId); eq("product_id", productId) }
            }.decodeSingleOrNull<CartDto>()

            if (existingDto != null) {
                val existing = existingDto.toDomain()
                postgrest["cart_items"].update({ set("quantity", existing.quantity + 1) }) {
                    filter { eq("id", existing.id!!) }
                }
            } else {
                val newItem = CartItemRequest(userId = userId, productId = productId, quantity = 1)
                postgrest["cart_items"].insert(newItem)
            }

            fetchCartItems(userId) // Tải lại để đồng bộ Flow công khai
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cập nhật số lượng của một Item cụ thể
     */
    suspend fun updateCartItemQuantity(userId: String, cartItemId: String, newQuantity: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (newQuantity <= 0) {
                postgrest["cart_items"].delete { filter { eq("id", cartItemId) } }
            } else {
                postgrest["cart_items"].update({ set("quantity", newQuantity) }) {
                    filter { eq("id", cartItemId) }
                }
            }
            fetchCartItems(userId) // Tải lại để đồng bộ Flow công khai
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Xóa sạch trạng thái Giỏ hàng local (dùng sau khi Checkout thành công)
     */
    fun clearLocalCart() {
        _cartItemsFlow.value = emptyList()
    }
}