package com.denis.realtynova.core.di

import android.content.Context
import androidx.room.Room
import com.denis.realtynova.core.data.local.PropertyDao
import com.denis.realtynova.core.data.local.RealtyNovaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RealtyNovaDatabase {
        return Room.databaseBuilder(
            context,
            RealtyNovaDatabase::class.java,
            "realtynova.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun providePropertyDao(db: RealtyNovaDatabase): PropertyDao = db.propertyDao
}
