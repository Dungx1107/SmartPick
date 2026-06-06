package com.example.smartpick.features.home.data

import android.util.Log
import com.example.smartpick.core.data.dto.CartItemDto
import com.example.smartpick.core.data.dto.ProductDto
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Product
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    val supabase: SupabaseClient
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
            }.decodeList<CartItemDto>()
            listDto.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "getCartItems error", e)
            emptyList()
        }
    }

    suspend fun addToCart(
        userId: String,
        productId: String,
        postId: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existingDto = postgrest["cart_items"].select {
                filter { eq("user_id", userId); eq("product_id", productId) }
            }.decodeSingleOrNull<CartItemDto>()
            if (existingDto != null) {
                val existing = existingDto.toDomain()
                postgrest["cart_items"].update({ set("quantity", existing.quantity + 1) }) { filter { eq("id", existing.id!!) } }
            } else {
                val newItem = CartItemDto(
                    userId = userId,
                    productId = productId,
                    quantity = 1,
                    postId = postId
                )
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

    suspend fun getProductById(productId: String): Product? = withContext(Dispatchers.IO) {
        try {
            val dto = postgrest["products"].select {
                filter { eq("id", productId) }
            }.decodeSingleOrNull<ProductDto>()
            dto?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "getProductById error", e)
            null
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
}