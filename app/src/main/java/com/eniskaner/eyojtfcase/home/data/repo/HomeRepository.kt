package com.eniskaner.eyojtfcase.home.data.repo

import com.eniskaner.eyojtfcase.home.domain.util.HomeUIState

interface HomeRepository {

    fun getSugarBeetCount(result: (HomeUIState<Int>) -> Unit)
}