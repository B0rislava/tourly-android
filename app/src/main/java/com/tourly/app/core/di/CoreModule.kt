package com.tourly.app.core.di

import com.tourly.app.core.domain.repository.ThemeRepository
import com.tourly.app.core.data.repository.ThemeRepositoryImpl
import com.tourly.app.core.domain.repository.LocationRepository
import com.tourly.app.core.data.repository.LocationRepositoryImpl
import com.tourly.app.core.domain.repository.LanguageRepository
import com.tourly.app.core.data.repository.LanguageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    @Binds
    @Singleton
    abstract fun bindThemeRepository(
        themeRepositoryImpl: ThemeRepositoryImpl
    ): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(
        languageRepositoryImpl: LanguageRepositoryImpl
    ): LanguageRepository
}
