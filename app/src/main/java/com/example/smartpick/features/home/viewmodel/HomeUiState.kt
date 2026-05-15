package com.example.smartpick.features.home.viewmodel

import com.example.smartpick.core.model.Product

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val products: List<Product>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}