package com.funnygaytest.di

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.InstallIn
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

}