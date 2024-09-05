package com.eniskaner.eyojtfcase.firebase.domain.repo

import com.eniskaner.eyojtfcase.common.util.Resource
import com.eniskaner.eyojtfcase.firebase.presentation.state.ODUIModelState

interface ODRepository {

    fun signIn(email: String, password: String): Resource<ODUIModelState>
    fun signUp(email: String, password: String): Resource<ODUIModelState>
}