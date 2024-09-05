package com.eniskaner.eyojtfcase.firebase.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eniskaner.eyojtfcase.common.util.AuthUIState
import com.eniskaner.eyojtfcase.firebase.domain.repo.ODRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: ODRepository
): ViewModel() {

    private val _signUpState = MutableLiveData<AuthUIState<String>>()
    val signUpState : LiveData<AuthUIState<String>>
        get() = _signUpState

    fun signUpFirebase(email: String, password: String) {
        _signUpState.value = AuthUIState.Loading
        repository.signUpFirebase(email = email, password = password) {
            _signUpState.value = it
        }
    }
}