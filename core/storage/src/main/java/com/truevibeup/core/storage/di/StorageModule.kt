package com.truevibeup.core.storage.di

import android.content.Context
import com.google.gson.Gson
import com.truevibeup.core.storage.SecureStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideSecureStorage(
        @ApplicationContext context: Context,
        gson: Gson
    ): SecureStorage = SecureStorage(context, gson)
}
