package com.eniskaner.eyojtfcase.firebase.domain.repo

import com.eniskaner.eyojtfcase.common.util.AuthUIState
import com.eniskaner.eyojtfcase.common.util.Resource
import com.eniskaner.eyojtfcase.firebase.presentation.state.ODUIModelState
import kotlinx.coroutines.flow.Flow

interface ODRepository {

    fun signInFirebase(email: String, password: String, response: (AuthUIState<String>) -> Unit)
    fun signUpFirebase(email: String, password: String, response: (AuthUIState<String>) -> Unit)
}