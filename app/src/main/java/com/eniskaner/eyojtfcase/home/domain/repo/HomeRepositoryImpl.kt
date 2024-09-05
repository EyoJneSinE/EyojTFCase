package com.eniskaner.eyojtfcase.home.domain.repo

import com.eniskaner.eyojtfcase.common.util.Constants.SUGAR_BEET_COUNT
import com.eniskaner.eyojtfcase.common.util.Constants.USER
import com.eniskaner.eyojtfcase.common.util.Constants.USER_DATA_CANT_FOUND
import com.eniskaner.eyojtfcase.home.domain.util.HomeUIState
import com.eniskaner.eyojtfcase.home.data.repo.HomeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseFirestore
) : HomeRepository {
    override fun getSugarBeetCount(result: (HomeUIState<Int>) -> Unit) {

        val uid = firebaseAuth.currentUser?.uid

        val userRef = uid?.let {
            database.collection(USER).document(it)
        }

        userRef?.let {
            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val sugarBeetCount =
                            documentSnapshot.getLong(SUGAR_BEET_COUNT)?.toInt() ?: 0
                        result.invoke(HomeUIState.Success(sugarBeetCount))
                    } else {
                        result.invoke(HomeUIState.Failure(USER_DATA_CANT_FOUND))
                    }
                }
                .addOnFailureListener { exception ->
                    result.invoke(HomeUIState.Failure(exception.localizedMessage))
                }
        }
    }
}
