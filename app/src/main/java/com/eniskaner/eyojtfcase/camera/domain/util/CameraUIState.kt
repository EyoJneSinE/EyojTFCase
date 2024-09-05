package com.eniskaner.eyojtfcase.camera.domain.util

sealed class CameraUIState<out T> {
    data object Loading: CameraUIState<Nothing>()
    data class Success<out T>(val data: T): CameraUIState<T>()
    data class Failure(val error: String?): CameraUIState<Nothing>()
}