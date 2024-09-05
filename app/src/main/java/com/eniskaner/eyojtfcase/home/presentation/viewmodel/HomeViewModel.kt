package com.eniskaner.eyojtfcase.home.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eniskaner.eyojtfcase.home.domain.util.HomeUIState
import com.eniskaner.eyojtfcase.home.data.repo.HomeRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel() {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _sugarBeetCountState = MutableLiveData<HomeUIState<Int>>()
    val sugarBeetCountState: LiveData<HomeUIState<Int>>
        get() = _sugarBeetCountState

    init {
        getSugarBeetCountFromFireStore()
    }

    fun getSugarBeetCountFromFireStore() {
        _sugarBeetCountState.value = HomeUIState.Loading
        homeRepository.getSugarBeetCount {
            _sugarBeetCountState.value = it
        }
    }
}