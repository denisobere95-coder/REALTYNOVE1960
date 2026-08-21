package com.denis.realtynova.core.di

import com.denis.realtynova.core.data.repository.PropertyRepositoryImpl
import com.denis.realtynova.core.data.repository.SavedRepositoryImpl
import com.denis.realtynova.core.domain.repository.PropertyRepository
import com.denis.realtynova.features.saved.SavedRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPropertyRepository(
        propertyRepositoryImpl: PropertyRepositoryImpl
    ): PropertyRepository

    @Binds
    @Singleton
    abstract fun bindSavedRepository(
        savedRepositoryImpl: SavedRepositoryImpl
    ): SavedRepository
}
