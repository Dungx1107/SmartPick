package com.example.smartpick.features.review.data

import android.util.Log
import com.example.smartpick.core.data.mapper.toDomain
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
class ReviewRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    private val postgrest = supabase.postgrest
    private val TAG = "ReviewRepository"

    /**
     * Lấy danh sách đánh giá của một sản phẩm cụ thể
     */
    suspend fun getProductReviews(productId: String): List<ReviewResponse> = withContext(Dispatchers.IO) {
        try {
            postgrest["reviews"].select(Columns.raw("*, users(id, full_name, avatar_url)")) {
                filter { eq("product_id", productId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<ReviewResponse>()
        } catch (e: Exception) {
            Log.e(TAG, "getProductReviews error: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Kiểm tra xem người dùng đã mua sản phẩm này chưa để cấp quyền đánh giá
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
            Log.e(TAG, "checkUserBoughtProduct error: ${e.message}", e)
            false
        }
    }

    /**
     * Gửi đánh giá mới lên Supabase
     */
    suspend fun submitReview(request: ReviewRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["reviews"].insert(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "submitReview error: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Bổ sung các data class DTO cần thiết ở cuối file nếu chưa có
    @kotlinx.serialization.Serializable
    data class BoughtProductDto(
        @kotlinx.serialization.SerialName("product_id") val productId: String,
        @kotlinx.serialization.SerialName("products") val product: com.example.smartpick.core.data.dto.ProductDto? = null
    )

    // Thêm 2 hàm này vào bên trong class ReviewRepository
    suspend fun getProductsToReview(userId: String): List<Product> = withContext(Dispatchers.IO) {
        try {
            val userOrders = postgrest["orders"].select(Columns.raw("id")) {
                filter { eq("user_id", userId) }
            }.decodeList<Map<String, String>>()

            val userOrderIds = userOrders.mapNotNull { it["id"] }
            if (userOrderIds.isEmpty()) return@withContext emptyList()

            val boughtItems = postgrest["order_items"].select(Columns.raw("product_id, products(*)")) {
                filter { isIn("order_id", userOrderIds) }
            }.decodeList<BoughtProductDto>()

            val myReviews = postgrest["reviews"].select(Columns.raw("product_id")) {
                filter { eq("user_id", userId) }
            }.decodeList<Map<String, String>>()

            val reviewedProductIds = myReviews.mapNotNull { it["product_id"] }.toSet()

            boughtItems.filter { !reviewedProductIds.contains(it.productId) }
                .mapNotNull { it.product?.toDomain() }
                .distinctBy { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMyReviewedProducts(userId: String): List<ReviewResponse> = withContext(Dispatchers.IO) {
        try {
            postgrest["reviews"].select(Columns.raw("*, products(*)")) {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<ReviewResponse>()
        } catch (e: Exception) {
            emptyList()
        }
    }
}