package com.eniskaner.eyojtfcase.camera.domain.di

import com.eniskaner.eyojtfcase.camera.data.repo.CameraRepositoryImpl
import com.eniskaner.eyojtfcase.camera.domain.repo.CameraRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CameraRepositoryModule {

    @Provides
    @Singleton
    fun provideCameraRepository(
        firebaseAuth: FirebaseAuth,
        database: FirebaseFirestore
    ): CameraRepository {
        return CameraRepositoryImpl(
            firebaseAuth = firebaseAuth,
            database = database
        )
    }
}