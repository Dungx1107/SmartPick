package com.example.smartpick.features.seller.data

import android.util.Log
import com.example.smartpick.core.data.dto.ProductDto
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.model.Product
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SellerRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    // DTO để lấy thông tin đơn hàng đã bán (Join bảng order_items và products)
    @Serializable
    data class SoldOrderItemDto(
        val id: String,
        @SerialName("order_id") val orderId: String,
        @SerialName("product_id") val productId: String,
        val quantity: Int,
        @SerialName("price_at_purchase") val priceAtPurchase: Double,
        @SerialName("created_at") val createdAt: String? = null,
        val products: ProductDto? = null
    )

    // Lấy danh sách sản phẩm người dùng ĐANG BÁN
    suspend fun getSellerProducts(ownerId: String): List<Product> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest["products"]
                .select {
                    filter { eq("owner_id", ownerId) }
                    order("created_at", Order.DESCENDING)
                }.decodeList<ProductDto>()
            response.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("SELLER_REPO", "Lỗi lấy sản phẩm: ${e.message}")
            emptyList()
        }
    }

    // Lấy danh sách đơn hàng KHÁCH ĐÃ MUA của người bán này
    suspend fun getSoldOrders(sellerId: String): List<SoldOrderItemDto> = withContext(Dispatchers.IO) {
        try {
            // Dùng inner join để chỉ lấy các order_items thuộc về product do seller này làm chủ
            val response = supabase.postgrest["order_items"]
                .select(columns = Columns.raw("*, products!inner(*)")) {
                    filter { eq("products.owner_id", sellerId) }
                    order("created_at", Order.DESCENDING)
                }
            response.decodeList<SoldOrderItemDto>()
        } catch (e: Exception) {
            Log.e("SELLER_REPO", "Lỗi lấy đơn đã bán: ${e.message}")
            emptyList()
        }
    }
}