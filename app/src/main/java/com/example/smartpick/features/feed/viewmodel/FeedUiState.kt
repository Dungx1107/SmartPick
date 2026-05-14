package com.example.smartpick.features.feed.viewmodel

import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User


/**
 * Đại diện cho trạng thái UI của màn hình Feed.
 *
 * Sử dụng sealed class để quản lý các trạng thái:
 * - Loading: đang tải dữ liệu
 * - Success: tải thành công và có dữ liệu
 * - Error: xảy ra lỗi khi tải dữ liệu
 */
sealed class FeedUiState {

    /* Trạng thái đang tải dữ liệu. */
    data object Loading : FeedUiState()

    /**
     * Trạng thái tải dữ liệu thành công.
     *
     * @property posts
     * Danh sách bài viết.
     *
     * Mỗi phần tử là Triple gồm:
     * - Post: thông tin bài viết
     * - User: người đăng bài
     * - Product?: sản phẩm được gắn vào bài viết (có thể null)
     */
    data class Success(
        val posts: List<Triple<Post, User, Product?>>
    ) : FeedUiState()

    /**
     * Trạng thái xảy ra lỗi.
     *
     * @property message Nội dung lỗi hiển thị cho UI
     */
    data class Error(
        val message: String
    ) : FeedUiState()
}