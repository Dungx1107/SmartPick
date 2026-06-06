// FILE: com/example/smartpick/features/cart/data/CartRepository.kt
package com.example.smartpick.features.cart.data

import android.util.Log
import com.example.smartpick.core.data.dto.CartItemDto
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
// BẮT BUỘC IMPORT ĐỂ BUILD JSON THÔ
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.slf4j.MDC.put

@Singleton
class CartRepository @Inject constructor(
    supabase: SupabaseClient
) {
    private val postgrest = supabase.postgrest
    private val tag = "CartRepository"

    private val _cartItemsFlow = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItemsFlow: StateFlow<List<CartItem>> = _cartItemsFlow.asStateFlow()

    suspend fun fetchCartItems(userId: String): List<CartItem> = withContext(Dispatchers.IO) {
        Log.d("SMARTPICK_DEBUG", "=== BẮT ĐẦU FETCH_CART_ITEMS ===")
        try {
            val response = postgrest["cart_items"]
                .select(Columns.raw("*, products!product_id(*)")) {
                    filter { eq("user_id", userId) }
                }

            val listDto = response.decodeList<CartItemDto>()
            val items = listDto.map { it.toDomain() }
            _cartItemsFlow.value = items
            items
        } catch (e: Exception) {
            Log.e(tag, "fetchCartItems error: ${e.localizedMessage}", e)
            emptyList()
        }
    }
    suspend fun addToCart(userId: String, productId: String, postId: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Log cấu trúc tham số đầu vào nhận từ ViewModel để kiểm tra độ trễ Coroutine
            Log.d("SMARTPICK_DEBUG", "=== BẮT ĐẦU ADD_TO_CART ===")
            Log.d("SMARTPICK_DEBUG", "THAM SỐ NHẬN ĐƯỢC TẠI REPOSITORY -> userId: $userId | productId: $productId | postId: $postId")

            // 2. Truy vấn kiểm tra trùng lặp trên danh sách cột thô phẳng
            val existingDto = postgrest["cart_items"].select(Columns.list("id", "user_id", "product_id", "quantity", "post_id")) {
                filter { eq("user_id", userId); eq("product_id", productId) }
            }.decodeSingleOrNull<CartItemDto>()

            if (existingDto != null) {
                Log.d("SMARTPICK_DEBUG", "Sản phẩm đã tồn tại trong giỏ. Tiến hành tăng số lượng và cập nhật bổ sung postId.")

                // KHẮC PHỤC: Xây dựng JsonObject cập nhật đồng thời cả số lượng và post_id để xóa bỏ trạng thái NULL cũ
                val updateData = buildJsonObject {
                    put("quantity", existingDto.quantity + 1)
                    if (postId != null) {
                        put("post_id", postId)
                    }
                }

                Log.d("SMARTPICK_DEBUG", "GÓI TIN UPDATE ĐẨY LÊN SUPABASE: $updateData")
                postgrest["cart_items"].update(updateData) {
                    filter { eq("id", existingDto.id!!) }
                }
            } else {
                Log.d("SMARTPICK_DEBUG", "Sản phẩm chưa có trong giỏ. Khởi tạo JSON chèn mới bản ghi.")

                val jsonToInsert = buildJsonObject {
                    put("user_id", userId)
                    put("product_id", productId)
                    put("quantity", 1)

                    if (postId != null) {
                        put("post_id", postId)
                        Log.d("SMARTPICK_DEBUG", "--> ĐÃ ĐÓNG GÓI 'post_id' VÀO JSON INSERT THÀNH CÔNG: $postId")
                    } else {
                        Log.w("SMARTPICK_DEBUG", "--> CẢNH BÁO: 'postId' BỊ NULL KHI BUILD JSON INSERT!")
                    }
                }

                Log.d("SMARTPICK_DEBUG", "GÓI TIN INSERT ĐẨY LÊN SUPABASE: $jsonToInsert")
                postgrest["cart_items"].insert(jsonToInsert)
            }

            Log.d("SMARTPICK_DEBUG", "Ghi nhận dữ liệu thành công. Gọi fetchCartItems để cập nhật UI.")
            fetchCartItems(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SMARTPICK_DEBUG", "Lỗi nghiêm trọng xảy ra tại addToCart: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun updateCartItemQuantity(
        userId: String,
        cartItemId: String,
        newQuantity: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("SMARTPICK_DEBUG", "=== BẮT ĐẦU UPDATE_CART_ITEM_QUANTITY ===")
            Log.d("SMARTPICK_DEBUG", "Tham số - cartItemId: $cartItemId, newQuantity: $newQuantity")

            if (newQuantity <= 0) {
                Log.d("SMARTPICK_DEBUG", "Số lượng <= 0. Tiến hành xóa vật phẩm khỏi giỏ hàng.")
                postgrest["cart_items"].delete {
                    filter { eq("id", cartItemId) }
                }
            } else {
                Log.d(
                    "SMARTPICK_DEBUG",
                    "Tiến hành cập nhật số lượng thô phẳng để tránh lỗi Schema."
                )

                // CHUẨN HÓA: Tạo JsonObject chứa duy nhất trường dữ liệu cần cập nhật
                val updateData = buildJsonObject {
                    put("quantity", newQuantity)
                }

                postgrest["cart_items"].update(updateData) {
                    filter { eq("id", cartItemId) }
                }
            }

            Log.d("SMARTPICK_DEBUG", "Cập nhật dữ liệu số lượng thành công. Gọi fetchCartItems.")
            fetchCartItems(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SMARTPICK_DEBUG", "Lỗi tại updateCartItemQuantity: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    fun clearLocalCart() {
        _cartItemsFlow.value = emptyList()
    }
}