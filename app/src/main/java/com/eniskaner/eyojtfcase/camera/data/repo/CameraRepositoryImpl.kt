package com.eniskaner.eyojtfcase.camera.data.repo

import com.eniskaner.eyojtfcase.camera.domain.repo.CameraRepository
import com.eniskaner.eyojtfcase.camera.domain.util.CameraUIState
import com.eniskaner.eyojtfcase.common.util.Constants.SUGAR_BEET_COUNT
import com.eniskaner.eyojtfcase.common.util.Constants.SUGAR_BEET_COUNT_UPDATED
import com.eniskaner.eyojtfcase.common.util.Constants.USER
import com.eniskaner.eyojtfcase.common.util.Constants.USER_DATA_CANT_FOUND
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CameraRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseFirestore
): CameraRepository {
    override fun updateSugarBeetCountOnFireBase(
        count: Int,
        result: (CameraUIState<String>) -> Unit
    ) {
        val uid = firebaseAuth.currentUser?.uid
        val userRef = uid?.let {
            database.collection(USER).document(it)
        }

        userRef?.let {
            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val currentCount = documentSnapshot.getLong(SUGAR_BEET_COUNT) ?: 0

                        val updatedCount = currentCount + count

                        userRef.update(SUGAR_BEET_COUNT, updatedCount)
                            .addOnSuccessListener {
                                result.invoke(CameraUIState.Success(SUGAR_BEET_COUNT_UPDATED))
                            }
                            .addOnFailureListener { exception ->
                                result.invoke(CameraUIState.Failure(exception.localizedMessage))
                            }
                    } else {
                        result.invoke(CameraUIState.Failure(USER_DATA_CANT_FOUND))
                    }
                }
                ?.addOnFailureListener { exception ->
                    result.invoke(CameraUIState.Failure(exception.localizedMessage))
                }
        }
    }
}