package com.eniskaner.eyojtfcase.home.data.repo

import com.eniskaner.eyojtfcase.common.util.HomeUIState

interface HomeRepository {

    fun getSugarBeetCount(result: (HomeUIState<Int>) -> Unit)
}