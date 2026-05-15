package com.example.smartpick.features.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.home.data.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * HomeViewModel quản lý state và business logic cho Home Screen.
 *
 * Chức năng:
 * - Load danh sách sản phẩm
 * - Search sản phẩm
 * - Quản lý giỏ hàng
 * - Đồng bộ dữ liệu giữa UI và Repository
 *
 * Flow:
 * UI -> ViewModel -> Repository -> Supabase
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    /* State quản lý danh sách sản phẩm */
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /* State quản lý cart items */
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    /* Cache toàn bộ products dùng cho search local */
    private var allProductsList: List<Product> = emptyList()

    /**
     * Tự động load dữ liệu khi ViewModel khởi tạo.
     *
     * Bao gồm:
     * - Load products
     * - Load cart items
     */
    init {
        fetchProducts()
        fetchCartItems()
    }

    /**
     * Lấy toàn bộ sản phẩm từ database.
     *
     * Flow:
     * 1. Chuyển UI sang Loading
     * 2. Query products từ repository
     * 3. Cache local list
     * 4. Update uiState cho UI
     */
    fun fetchProducts() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                /* Query products */
                val products = repository.getAllProducts()
                /* Cache products */
                allProductsList = products
                /* Update UI */
                _uiState.value = HomeUiState.Success(products)
            } catch (e: Exception) {
                Log.e("SupabaseCart", "Lỗi fetchProducts: ${e.message}", e)
                _uiState.value =
                    HomeUiState.Error(e.message ?: "Lỗi tải dữ liệu")
            }
        }
    }

    /**
     * Tìm kiếm sản phẩm theo tên.
     *
     * Logic:
     * - Query rỗng -> hiển thị toàn bộ
     * - Có query -> filter theo tên sản phẩm
     *
     * Search hiện tại:
     * - Search local
     * - Realtime khi nhập text
     *
     * @param query từ khóa tìm kiếm
     */
    fun searchProducts(query: String) {
        val filtered = if (query.isBlank()) {
            /* Hiển thị full products */
            allProductsList
        } else {
            /* Filter theo product name */
            allProductsList.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        /* Update UI với danh sách mới */
        _uiState.value = HomeUiState.Success(filtered)
    }

    /**
     * Load danh sách sản phẩm trong giỏ hàng.
     *
     * Flow:
     * 1. Lấy user hiện tại
     * 2. Query cart theo userId
     * 3. Update cartItems state
     */
    fun fetchCartItems() {
        viewModelScope.launch {
            try {
                /* Lấy current user */
                val user = authRepository.getCurrentUser()
                user?.id?.let { uid ->
                    /* Load cart */
                    _cartItems.value =
                        repository.getCartItems(uid)
                }
            } catch (e: Exception) {
                Log.e(
                    "SupabaseCart",
                    "Lỗi fetchCartItems: ${e.message}",
                    e
                )
            }
        }
    }

    /**
     * Thêm sản phẩm vào giỏ hàng.
     *
     * Logic:
     * 1. Kiểm tra login
     * 2. Validate product id
     * 3. Add vào cart
     * 4. Reload cart nếu thành công
     *
     * Callback:
     * - onSuccess -> thêm thành công
     * - onError -> có lỗi
     */
    fun addToCart(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser() /* Kiểm tra user login */
                if (user == null) {
                    onError("Bạn cần đăng nhập để thực hiện tính năng này")
                    return@launch
                }
                val uid = user.id
                if (product.id != null) {
                    /* Add product vào cart */
                    val result =
                        repository.addToCart(uid, product.id)
                    if (result.isSuccess) {
                        /* Reload cart */
                        fetchCartItems()
                        onSuccess()
                    } else {
                        val errorMsg =
                            result.exceptionOrNull()?.message
                                ?: "Lỗi không xác định"
                        Log.e(
                            "SupabaseCart",
                            "Lỗi addToCart API: $errorMsg"
                        )
                        onError(errorMsg)
                    }
                } else {
                    onError("ID sản phẩm không hợp lệ")
                }
            } catch (e: Exception) {
                Log.e(
                    "SupabaseCart",
                    "Crash addToCart: ${e.message}",
                    e
                )
                onError("Đã xảy ra lỗi hệ thống: ${e.message}")
            }
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng.
     *
     * Flow:
     * 1. Delete item
     * 2. Reload cart nếu thành công
     */
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                if (cartItem.id != null) {
                    val result =
                        repository.removeFromCart(cartItem.id)
                    if (result.isSuccess) {
                        /* Reload cart */
                        fetchCartItems()
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    "SupabaseCart",
                    "Lỗi removeFromCart: ${e.message}",
                    e
                )
            }
        }
    }

    /**
     * Tăng số lượng sản phẩm trong giỏ hàng.
     *
     * Logic:
     * - quantity + 1
     * - update database
     * - reload cart
     */
    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            try {
                val result =
                    repository.updateCartItemQuantity(
                        item.id!!,
                        item.quantity + 1
                    )
                if (result.isSuccess) {
                    /* Reload cart */
                    fetchCartItems()
                }
            } catch (e: Exception) {
                Log.e(
                    "SupabaseCart",
                    "Lỗi increaseQuantity: ${e.message}",
                    e
                )
            }
        }
    }

    /**
     * Giảm số lượng sản phẩm trong giỏ hàng.
     *
     * Logic:
     * - quantity > 1 -> giảm quantity
     * - quantity == 1 -> xóa item
     */
    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            try {
                if (item.quantity > 1) {
                    val result =
                        repository.updateCartItemQuantity(
                            item.id!!,
                            item.quantity - 1
                        )
                    if (result.isSuccess) {
                        /* Reload cart */
                        fetchCartItems()
                    }
                } else {
                    /* Quantity = 1 -> remove */
                    removeFromCart(item)
                }
            } catch (e: Exception) {
                Log.e(
                    "SupabaseCart",
                    "Lỗi decreaseQuantity: ${e.message}",
                    e
                )
            }
        }
    }

    /**
     * Lấy postId tương ứng với productId.
     *
     * Dùng để:
     * - Điều hướng sang Post Detail
     * - Liên kết product với bài viết
     */
    suspend fun getPostId(productId: String): String? {
        return repository.getPostIdByProductId(productId)
    }
}
