package com.example.smartpick.features.home.data

import com.example.smartpick.core.model.Product
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val postgrest: Postgrest
) {
    /**
     * Lấy TOÀN BỘ danh sách sản phẩm (không lọc status)
     */
    suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            postgrest["products"]
                .select()
                .decodeList<Product>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Tìm post_id chứa product_id tương ứng để điều hướng
     */
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
}