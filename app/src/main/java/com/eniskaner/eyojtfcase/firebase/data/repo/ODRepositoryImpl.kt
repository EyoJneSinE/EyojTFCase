package com.eniskaner.eyojtfcase.firebase.data.repo

import com.eniskaner.eyojtfcase.firebase.domain.util.AuthUIState
import com.eniskaner.eyojtfcase.common.util.Constants.USER
import com.eniskaner.eyojtfcase.common.util.Constants.USER_ALREADY_EXIST
import com.eniskaner.eyojtfcase.common.util.Constants.USER_DATA_CANT_FOUND
import com.eniskaner.eyojtfcase.common.util.Constants.USER_NOT_FOUND_OR_PASSWORD_WRONG
import com.eniskaner.eyojtfcase.firebase.data.model.ODUserModel
import com.eniskaner.eyojtfcase.firebase.domain.repo.ODRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ODRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseFirestore
) : ODRepository {
    override fun signInFirebase(
        email: String,
        password: String,
        response: (AuthUIState<String>) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                response.invoke(
                    AuthUIState.Success(it.result.toString())
                )
            } else {
                response.invoke(
                    AuthUIState.Failure(USER_NOT_FOUND_OR_PASSWORD_WRONG)
                )
            }
        }
    }

    override fun signUpFirebase(
        email: String,
        password: String,
        response: (AuthUIState<String>) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    val uid = user.uid
                    database.collection(USER).document(uid)
                        .set(ODUserModel(email, password))
                        .addOnSuccessListener { response.invoke(AuthUIState.Success(uid)) }
                        .addOnFailureListener { exception ->
                            response.invoke(AuthUIState.Failure(exception.localizedMessage))
                        }
                } else {
                    response.invoke(AuthUIState.Failure(USER_DATA_CANT_FOUND))
                }
            } else {
                response.invoke(AuthUIState.Failure(USER_ALREADY_EXIST))
            }
        }
    }

}