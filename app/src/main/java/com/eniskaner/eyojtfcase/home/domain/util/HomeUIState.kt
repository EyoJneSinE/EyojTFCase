package com.eniskaner.eyojtfcase.home.domain.util

sealed class HomeUIState<out T> {
    data object Loading: HomeUIState<Nothing>()
    data class Success<out T>(val data: T): HomeUIState<T>()
    data class Failure(val error: String?): HomeUIState<Nothing>()
}