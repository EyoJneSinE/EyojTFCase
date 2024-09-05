package com.eniskaner.eyojtfcase.firebase.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eniskaner.eyojtfcase.common.util.AuthUIState
import com.eniskaner.eyojtfcase.firebase.domain.repo.ODRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val odRepository: ODRepository
) : ViewModel() {

    private val _signInFirebaseState = MutableLiveData<AuthUIState<String>>()
    val signInFirebaseState: LiveData<AuthUIState<String>>
        get() = _signInFirebaseState

    var isUserLoggedIn = false

    init {
        checkIfUserLoggedIn()
    }

    fun signInFirebase(email: String, password: String) {
        _signInFirebaseState.value = AuthUIState.Loading
        odRepository.signInFirebase(email, password) {
            _signInFirebaseState.value = it
        }
    }

    private fun checkIfUserLoggedIn() {
        isUserLoggedIn = firebaseAuth.currentUser != null
    }
}