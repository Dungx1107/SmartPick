package com.example.smartpick.features.review.data

import android.util.Log
import com.example.smartpick.core.data.dto.BoughtProductDto
import com.example.smartpick.core.data.dto.CheckBoughtResponseDto
import com.example.smartpick.core.data.dto.ReviewRequestDto
import com.example.smartpick.core.data.dto.ReviewResponseDto
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.Review
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
    suspend fun getProductReviews(productId: String): List<Review> = withContext(Dispatchers.IO) {
        try {
            postgrest["reviews"].select(Columns.raw("*, users(id, full_name, avatar_url)")) {
                filter { eq("product_id", productId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<ReviewResponseDto>().map { it.toDomain() }
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
            }.decodeList<CheckBoughtResponseDto>()

            response.isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "checkUserBoughtProduct error: ${e.message}", e)
            false
        }
    }

    /**
     * Gửi đánh giá mới lên Supabase
     */
    suspend fun submitReview(userId: String, productId: String, rating: Int, content: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val requestDto = ReviewRequestDto(
                userId = userId,
                productId = productId,
                rating = rating,
                content = content
            )
            postgrest["reviews"].insert(requestDto)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "submitReview error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Lấy danh sách các sản phẩm user đã mua nhưng chưa thực hiện đánh giá
     */
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
            Log.e(TAG, "getProductsToReview error: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Lấy danh sách các sản phẩm mà user hiện tại đã đánh giá xong
     */
    suspend fun getMyReviewedProducts(userId: String): List<Review> = withContext(Dispatchers.IO) {
        try {
            postgrest["reviews"].select(Columns.raw("*, products(*)")) {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<ReviewResponseDto>().map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "getMyReviewedProducts error: ${e.message}", e)
            emptyList()
        }
    }
}