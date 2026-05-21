package com.example.smartpick.features.profile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.features.feed.data.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val feedRepository: FeedRepository // Sử dụng chung FeedRepository làm Single Source of Truth
) : ViewModel() {

    private val _userPosts = MutableStateFlow<List<Triple<Post, User, Product?>>>(emptyList())
    val userPosts: StateFlow<List<Triple<Post, User, Product?>>> = _userPosts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadUserPosts(profileUserId: String, currentUserId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val posts = feedRepository.getUserPosts(profileUserId, currentUserId)
                _userPosts.value = posts
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Lỗi tải bài viết trang cá nhân: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}