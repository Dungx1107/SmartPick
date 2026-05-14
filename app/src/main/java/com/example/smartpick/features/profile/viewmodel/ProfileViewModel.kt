package com.example.smartpick.features.profile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.features.post_detail.data.dto.PostDetailResponse
import com.example.smartpick.features.profile.data.UserPostRepository
import com.example.smartpick.features.profile.data.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPostRepository: UserPostRepository
) : ViewModel() {
    private val _userPosts = MutableStateFlow<List<PostDetailResponse>>(emptyList())
    val userPosts = _userPosts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadUserPosts(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _userPosts.value = userPostRepository.getUserPosts(userId)
            } catch (e: Exception) {
                /* Bỏ qua lỗi hủy coroutine. */
                if (e !is kotlinx.coroutines.CancellationException) {
                    Log.e(
                        "PROFILE_VIEWMODEL",
                        "Lỗi tải bài viết người dùng: ${e.message}",
                        e
                    )
                }

            } finally {
                _isLoading.value = false/* Tắt loading sau khi xử lý xong. */
            }

        }
    }
}