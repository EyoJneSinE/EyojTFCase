package com.eniskaner.eyojtfcase.home.domain.di

import com.eniskaner.eyojtfcase.home.data.repo.HomeRepository
import com.eniskaner.eyojtfcase.home.domain.repo.HomeRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeRepositoryModule {

    @Provides
    @Singleton
    fun provideHomeRepository(
        firebaseAuth: FirebaseAuth,
        database: FirebaseFirestore
    ): HomeRepository {
        return HomeRepositoryImpl(
            firebaseAuth = firebaseAuth,
            database = database
        )
    }
}