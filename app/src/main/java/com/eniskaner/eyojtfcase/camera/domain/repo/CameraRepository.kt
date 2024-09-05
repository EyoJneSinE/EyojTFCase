package com.eniskaner.eyojtfcase.camera.domain.repo

import com.eniskaner.eyojtfcase.camera.domain.util.CameraUIState

interface CameraRepository {

    fun updateSugarBeetCountOnFireBase(count: Int, result: (CameraUIState<String>) -> Unit)
}