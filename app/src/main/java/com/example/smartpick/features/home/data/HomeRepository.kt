package com.example.smartpick.features.home.data

import android.util.Log
import com.example.smartpick.core.data.dto.PostDto
import com.example.smartpick.core.data.dto.ProductDto
import com.example.smartpick.features.feed.data.dto.FullPostResponse
import com.example.smartpick.core.data.mapper.toDomain
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

    /**
     * Lấy toàn bộ sản phẩm hiển thị trên trang chủ
     * Giải pháp độc lập: JOIN ngược từ bảng posts để lấy kèm chính xác postId gốc, chặn đứng dữ liệu null
     */
    suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "--- START: getAllProducts kết hợp JOIN posts ---")

            val response = postgrest["posts"]
                .select(columns = Columns.raw("id, user_id, products(*)"))

            val postListDto = response.decodeList<FullPostResponse>()

            val productsWithPostId = postListDto.mapNotNull { postDto ->
                val productDomain = postDto.products?.toDomain()
                productDomain?.copy(postId = postDto.id)
            }

            Log.d(TAG, "--- SUCCESS: Đã tải ${productsWithPostId.size} sản phẩm sạch kèm postId ---")
            productsWithPostId
        } catch (e: Exception) {
            Log.e(TAG, "!!!! LỖI nghiêm trọng tại getAllProducts: ${e.localizedMessage}", e)
            emptyList()
        }
    }

    suspend fun getPostIdByProductId(productId: String): String? = withContext(Dispatchers.IO) {
        Log.d("SMARTPICK_DEBUG", "--- REPOSITORY: Bắt đầu tìm kiếm postId cho productId: $productId ---")
        try {
            val response = postgrest["posts"].select(Columns.list("id", "user_id")) {
                filter { eq("product_id", productId) }
            }

            Log.d("SMARTPICK_DEBUG", "--- REPOSITORY: Phản hồi thô từ bảng posts: $response")

            val postDto = response.decodeSingleOrNull<PostDto>()
            Log.d("SMARTPICK_DEBUG", "--- REPOSITORY: Kết quả giải mã PostDto: $postDto | ID bài viết thu được: ${postDto?.id}")

            return@withContext postDto?.id
        } catch (e: Exception) {
            Log.e("SMARTPICK_DEBUG", "--- REPOSITORY LỖI tại getPostIdByProductId: ${e.localizedMessage}", e)
            return@withContext null
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
}