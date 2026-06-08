package com.example.smartpick.features.post_detail.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.data.mapper.toDomain
import com.example.smartpick.core.model.Post
import com.example.smartpick.features.post_detail.data.PostDetailRepository
import com.example.smartpick.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel quản lý dữ liệu màn hình chi tiết bài viết.
 *
 * Nhiệm vụ chính:
 * - Lấy postId từ navigation
 * - Tải dữ liệu chi tiết bài viết
 * - Quản lý trạng thái UI
 */
@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val repository: PostDetailRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    /* State quản lý UI màn hình Post Detail. */
    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState = _uiState.asStateFlow() /* State public cho UI observe. */

    /**
     * Lấy postId từ Navigation argument.
     * ARG_POST_ID được truyền từ màn hình trước.
     */
    private val postId: String? =
        savedStateHandle[Routes.PostDetail.ARG_POST_ID]

    /**
     * Tự động load dữ liệu khi ViewModel được tạo.
     */
    init {
        postId?.let { loadPostDetail(it) }
    }

    /**
     * Hàm tải dữ liệu chi tiết bài viết.
     *
     * Flow xử lý:
     * 1. Chuyển UI sang loading
     * 2. Gọi repository lấy dữ liệu
     * 3. Mapping response -> UI State
     * 4. Xử lý lỗi nếu có
     */
    fun loadPostDetail(id: String) {

        viewModelScope.launch {

            /* Hiển thị trạng thái loading. */
            _uiState.update {
                it.copy(isLoading = true)
            }

            try {

                /* Gọi repository lấy dữ liệu bài viết. */
                val response = repository.getPostDetail(id)

                /**
                 * Nếu tìm thấy bài viết:
                 * - Mapping dữ liệu response
                 * - Cập nhật UI state
                 */
                if (response != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            post = Post(
                                id = response.id,
                                userId = response.user.id,
                                content = response.content,
                                mediaUrls = response.mediaUrls,
                                createdAt = response.createdAt,

                                // ÁNH XẠ CHÍNH XÁC: Chuyển đổi các trường tương tác từ DTO sang Model Post
                                reactionCount = response.likesCount,
                                currentUserReaction = if (response.isLiked) com.example.smartpick.core.model.ReactionType.LIKE else null,
                                reactionBreakdown = if (response.isLiked) mapOf(com.example.smartpick.core.model.ReactionType.LIKE to response.likesCount) else emptyMap(),
                                sharedPostId = response.sharedPostId
                            ),
                            user = response.user.toDomain(),
                            product = response.product?.toDomain()
                        )
                    }
                }else {
                    /* Không tìm thấy bài viết. */
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Không tìm thấy bài viết"
                        )
                    }
                }

            } catch (e: Exception) {

                /**
                 * Nếu có lỗi:
                 * - Tắt loading
                 * - Hiển thị message lỗi
                 */
                Log.e(
                    "POST_DETAIL",
                    "Lỗi tải chi tiết bài viết: ${e.message}",
                    e
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage
                    )
                }
            }
        }
    }
}