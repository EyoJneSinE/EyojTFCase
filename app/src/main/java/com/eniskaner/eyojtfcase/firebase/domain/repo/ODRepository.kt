package com.eniskaner.eyojtfcase.firebase.domain.repo

import com.eniskaner.eyojtfcase.firebase.domain.util.AuthUIState

interface ODRepository {

    fun signInFirebase(email: String, password: String, response: (AuthUIState<String>) -> Unit)
    fun signUpFirebase(email: String, password: String, response: (AuthUIState<String>) -> Unit)
}