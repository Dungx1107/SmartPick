// File: app/src/main/java/com/example/smartpick/features/home/data/HomeRepository.kt
package com.example.smartpick.features.home.data

import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Product
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class CartItemRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    val quantity: Int = 1
)

@Singleton
class HomeRepository @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            postgrest["products"]
                .select()
                .decodeList<Product>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPostIdByProductId(productId: String): String? = withContext(Dispatchers.IO) {
        try {
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
            null
        }
    }

    suspend fun getCartItems(userId: String): List<CartItem> = withContext(Dispatchers.IO) {
        try {
            postgrest["cart_items"]
                .select(Columns.raw("*, products(*)")) {
                    filter { eq("user_id", userId) }
                }
                .decodeList<CartItem>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addToCart(userId: String, productId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = postgrest["cart_items"]
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("product_id", productId)
                    }
                }.decodeSingleOrNull<CartItem>()

            if (existing != null) {
                postgrest["cart_items"].update({
                    set("quantity", existing.quantity + 1)
                }) {
                    filter { eq("id", existing.id!!) }
                }
            } else {
                // FIX: Dùng class CartItemRequest để Insert, tránh việc Kotlin nhét thuộc tính `products` vào JSON
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
            postgrest["cart_items"].delete {
                filter { eq("id", cartItemId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCartItemQuantity(cartItemId: String, newQuantity: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["cart_items"].update({
                set("quantity", newQuantity)
            }) {
                filter { eq("id", cartItemId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}