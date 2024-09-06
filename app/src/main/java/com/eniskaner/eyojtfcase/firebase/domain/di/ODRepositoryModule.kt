package com.eniskaner.eyojtfcase.firebase.domain.di

import com.eniskaner.eyojtfcase.firebase.data.repo.ODRepositoryImpl
import com.eniskaner.eyojtfcase.firebase.domain.repo.ODRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ODRepositoryModule {

    @Provides
    @Singleton
    fun provideODRepository(
        firebaseAuth: FirebaseAuth,
        database: FirebaseFirestore
    ): ODRepository {
        return ODRepositoryImpl(firebaseAuth = firebaseAuth, database = database)
    }
}