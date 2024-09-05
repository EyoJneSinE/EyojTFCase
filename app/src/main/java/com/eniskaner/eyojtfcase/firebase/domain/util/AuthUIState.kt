package com.eniskaner.eyojtfcase.firebase.domain.util

sealed class AuthUIState<out T> {
    data object Loading: AuthUIState<Nothing>()
    data class Success<out T>(val data: T): AuthUIState<T>()
    data class Failure(val error: String?): AuthUIState<Nothing>()
}