package com.platform.sdk.app.di

import com.platform.sdk.app.data.PigmentRepositoryImpl
import com.platform.sdk.app.data.dataSource.PigmentLocalDataSource
import com.platform.sdk.app.data.dataSource.local.PigmentLocalDataSourceImpl
import com.platform.sdk.app.domain.PigmentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object PigmentModule {

    @Singleton
    @Provides
    fun provideRepository(
        localDataSource: PigmentLocalDataSource
    ): PigmentRepository = PigmentRepositoryImpl(localDataSource = localDataSource)

    @Singleton
    @Provides
    fun provideLocalDataSource(): PigmentLocalDataSource = PigmentLocalDataSourceImpl()
}
