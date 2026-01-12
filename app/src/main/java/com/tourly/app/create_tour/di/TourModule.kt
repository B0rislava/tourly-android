package com.tourly.app.create_tour.di

import com.tourly.app.create_tour.data.repository.TourRepositoryImpl
import com.tourly.app.create_tour.domain.repository.TourRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class TourModule {

    @Binds
    abstract fun bindTourRepository(
        tourRepositoryImpl: TourRepositoryImpl
    ): TourRepository
}
