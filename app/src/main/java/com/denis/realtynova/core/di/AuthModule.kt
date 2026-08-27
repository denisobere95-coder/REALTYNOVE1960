package com.denis.realtynova.core.di

import android.content.Context
import com.denis.realtynova.core.data.repository.AuthRepositoryImpl
import com.denis.realtynova.core.data.repository.ChatRepositoryImpl
import com.denis.realtynova.core.domain.repository.AuthRepository
import com.denis.realtynova.core.domain.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        @Provides
        @Singleton
        fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

        @Provides
        @Singleton
        fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

        @Provides
        @Singleton
        fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

        @Provides
        @Singleton
        fun provideFirebaseMessaging(): com.google.firebase.messaging.FirebaseMessaging = 
            com.google.firebase.messaging.FirebaseMessaging.getInstance()

        @Provides
        @Singleton
        fun provideFirebaseAnalytics(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): com.google.firebase.analytics.FirebaseAnalytics = 
            com.google.firebase.analytics.FirebaseAnalytics.getInstance(context)

        @Provides
        @Singleton
        fun provideFirebaseRemoteConfig(): com.google.firebase.remoteconfig.FirebaseRemoteConfig = 
            com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance()
    }
}
