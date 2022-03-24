package com.funnygaytest.di

import android.content.Context
import android.content.res.Resources
import com.funnygaytest.data.room.QuestionsRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRoom(@ApplicationContext context: Context): QuestionsRoomDatabase {
        return QuestionsRoomDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }
}