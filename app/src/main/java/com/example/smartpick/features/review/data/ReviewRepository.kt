package com.example.smartpick.features.review.data

import android.util.Log
import com.example.smartpick.core.data.dto.ReviewRequestDto
import com.example.smartpick.core.data.dto.ReviewResponseDto
import com.example.smartpick.core.data.dto.OrderItemWithProductDto
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
    private val TAG = "ReviewRepository_DEBUG" // Khớp với tag logcat của bạn

    /**
     * 1. Lấy danh sách đánh giá của sản phẩm
     */
    suspend fun getProductReviews(productId: String): List<Review> = withContext(Dispatchers.IO) {
        try {
            postgrest["reviews"].select(Columns.raw("*, users!user_id(id, full_name, avatar_url)")) {
                filter { eq("product_id", productId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<ReviewResponseDto>().map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "getProductReviews error: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * 2. Kiểm tra lượt mua khả dụng cho trang Chi tiết sản phẩm
     */
    suspend fun checkUserBoughtProduct(userId: String, productId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = postgrest["order_items"].select(Columns.raw("id, product_id, orders!inner(user_id)")) {
                filter {
                    eq("product_id", productId)
                    eq("orders.user_id", userId)
                }
            }.decodeList<OrderItemWithProductDto>()

            if (response.isEmpty()) return@withContext false

            val myReviewedItems = postgrest["reviews"].select(Columns.raw("order_item_id")) {
                filter { eq("user_id", userId) }
            }.decodeList<Map<String, String>>()

            val reviewedItemIds = myReviewedItems.mapNotNull { it["order_item_id"] }.toSet()
            response.any { !reviewedItemIds.contains(it.id) }
        } catch (e: Exception) {
            Log.e(TAG, "checkUserBoughtProduct error: ${e.message}", e)
            false
        }
    }

    /**
     * 3. Gửi đánh giá mới lên hệ thống
     */
    suspend fun submitReview(userId: String, productId: String, orderItemId: String, rating: Int, content: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val requestDto = ReviewRequestDto(
                userId = userId,
                productId = productId,
                orderItemId = orderItemId,
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
     * 4. Lấy danh sách chờ đánh giá (TRỌNG TÂM TRUY VẾT LỖI)
     */
    suspend fun getProductsToReview(userId: String): List<Product> = withContext(Dispatchers.IO) {
        Log.d(TAG, "\n========================================================")
        Log.d(TAG, "--- REPOSITORY: Bắt đầu quét danh sách chờ đánh giá cho user: $userId ---")
        try {
            // BƯỚC A: Lấy danh sách hóa đơn tổng
            Log.d(TAG, "[Bước A] Đang truy vấn bảng 'orders' với user_id = $userId...")
            val userOrdersResult = postgrest["orders"].select(Columns.raw("id")) {
                filter { eq("user_id", userId) }
            }
            Log.d(TAG, "[Bước A - Raw Response]: ${userOrdersResult.data}")

            val userOrders = userOrdersResult.decodeList<Map<String, String>>()
            val userOrderIds = userOrders.mapNotNull { it["id"] }
            Log.d(TAG, "[Bước A - Kết quả]: Tìm thấy ${userOrderIds.size} mã hóa đơn tổng: $userOrderIds")

            if (userOrderIds.isEmpty()) {
                Log.w(TAG, "[Bước A - CẢNH BÁO]: User chưa có hóa đơn nào trên hệ thống -> Trả về rỗng.")
                return@withContext emptyList()
            }

            // BƯỚC B: Lấy danh sách sản phẩm nằm trong hóa đơn
            Log.d(TAG, "[Bước B] Đang truy vấn bảng 'order_items' lồng bảng 'products' cho danh sách đơn: $userOrderIds...")
            val boughtItemsResult = postgrest["order_items"].select(Columns.raw("id, product_id, quantity, price_at_purchase, products(id, name, image_urls)")) {
                filter { isIn("order_id", userOrderIds) }
            }
            Log.d(TAG, "[Bước B - Raw Response]: ${boughtItemsResult.data}")

            val boughtItems = boughtItemsResult.decodeList<OrderItemWithProductDto>()
            Log.d(TAG, "[Bước B - Kết quả]: Giải mã thành công ${boughtItems.size} bản ghi từ order_items.")
            boughtItems.forEachIndexed { index, item ->
                Log.d(TAG, "   -> Bản ghi thứ $index: order_item_id thô = ${item.id} | productId = ${item.productId} | Dữ liệu sản phẩm nhúng = ${item.products}")
            }

            // BƯỚC C: Lấy danh sách đơn hàng chi tiết đã được viết review
            Log.d(TAG, "[Bước C] Đang truy vấn bảng 'reviews' để lấy các order_item_id đã được đánh giá...")
            val myReviewsResult = postgrest["reviews"].select(Columns.raw("order_item_id")) {
                filter { eq("user_id", userId) }
            }
            Log.d(TAG, "[Bước C - Raw Response]: ${myReviewsResult.data}")

            val myReviews = myReviewsResult.decodeList<Map<String, String?>>()
            val reviewedItemIds = myReviews.mapNotNull { it["order_item_id"] }.toSet()
            Log.d(TAG, "[Bước C - Kết quả]: Danh sách order_item_id đã khóa đánh giá: $reviewedItemIds")

            // BƯỚC D: Tiến hành lọc chặn trùng và ánh xạ dữ liệu đầu ra
            Log.d(TAG, "[Bước D] Bắt đầu chạy bộ lọc so sánh logic chặn trùng...")
            val finalList = boughtItems.filter { item ->
                val isReviewed = reviewedItemIds.contains(item.id)
                Log.d(TAG, "   -> Kiểm tra order_item_id [${item.id}]: trạng thái đã review = $isReviewed")
                !isReviewed
            }.mapNotNull { item ->
                val minProduct = item.products
                if (minProduct != null) {
                    Product(
                        id = minProduct.id,
                        name = minProduct.name,
                        imageUrls = minProduct.imageUrls,
                        ownerId = "",
                        postId = item.id // Lưu trữ mã UUID của order_items để luân chuyển
                    )
                } else {
                    Log.w(TAG, "   [CẢNH BÁO]: Bản ghi order_item_id [${item.id}] chứa object sản phẩm bị NULL sau giải mã.")
                    null
                }
            }

            Log.d(TAG, "=== HOÀN TẤT TRUY VẾT: Xuất ra danh sách giao diện gồm ${finalList.size} sản phẩm chờ. ===")
            Log.d(TAG, "========================================================\n")
            return@withContext finalList

        } catch (e: Exception) {
            Log.e(TAG, "!!!! [HỆ THỐNG GẶP LỖI SẬP LOGIC TRUY VẤN] !!!!")
            Log.e(TAG, "Chi tiết thông tin lỗi: ${e.message}", e)
            Log.d(TAG, "========================================================\n")
            emptyList()
        }
    }

    /**
     * 5. Lấy danh sách lịch sử đánh giá
     */
    suspend fun getMyReviewedProducts(userId: String): List<Review> = withContext(Dispatchers.IO) {
        try {
            postgrest["reviews"].select(Columns.raw("*, order_item_id, products(*)")) {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<ReviewResponseDto>().map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "getMyReviewedProducts error: ${e.message}", e)
            emptyList()
        }
    }
}