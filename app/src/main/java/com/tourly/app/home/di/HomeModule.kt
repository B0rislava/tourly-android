package com.tourly.app.home.di

import com.tourly.app.home.data.repository.HomeToursRepositoryImpl
import com.tourly.app.home.domain.repository.HomeToursRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    @Singleton
    abstract fun bindHomeToursRepository(
        impl: HomeToursRepositoryImpl
    ): HomeToursRepository
}
