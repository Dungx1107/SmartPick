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
    // BỔ SUNG: DTO nhỏ để bóc tách cột status từ bảng orders dưới Database
    @Serializable
    data class OrderStatusDto(
        val status: String = ""
    )

    // DTO để lấy thông tin đơn hàng đã bán (Đã bổ sung trường orders để ánh xạ dữ liệu)
    @Serializable
    data class SoldOrderItemDto(
        val id: String,
        @SerialName("order_id") val orderId: String,
        @SerialName("product_id") val productId: String,
        val quantity: Int,
        @SerialName("price_at_purchase") val priceAtPurchase: Double,
        @SerialName("created_at") val createdAt: String? = null,
        val products: ProductDto? = null,
        // BỔ SUNG TRƯỜNG NÀY: Khớp với cấu trúc dữ liệu trả về khi nhúng orders!inner(*)
        val orders: OrderStatusDto? = null
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

    suspend fun getSoldOrders(sellerId: String): List<SoldOrderItemDto> = withContext(Dispatchers.IO) {
        try {
            Log.d("SELLER_DEBUG", "--- BẮT ĐẦU TRUY VẤN DOANH THU ---")
            Log.d("SELLER_DEBUG", "Mã người bán (SellerID): $sellerId")

            val response = supabase.postgrest["order_items"]
                .select(columns = Columns.raw("*, products!inner(*), orders!inner(*)")) {
                    filter {
                        eq("products.owner_id", sellerId)
//                        eq("orders.status", "completed")
                    }
                    order("created_at", Order.DESCENDING)
                }

            val rawJson = response.data
            Log.d("SELLER_DEBUG", "JSON thô từ Database: $rawJson")

            val decodedList = response.decodeList<SoldOrderItemDto>()
            Log.d("SELLER_DEBUG", "Số lượng dòng đơn hàng parse thành công: ${decodedList.size}")

            decodedList
        } catch (e: Exception) {
            Log.e("SELLER_DEBUG", "CRITICAL ERROR tại SellerRepository: ${e.message}", e)
            emptyList()
        }
    }
}