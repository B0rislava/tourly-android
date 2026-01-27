package com.tourly.app.notifications.di

import com.tourly.app.notifications.data.remote.NotificationApiService
import com.tourly.app.notifications.data.repository.NotificationRepositoryImpl
import com.tourly.app.notifications.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationApiService(client: HttpClient): NotificationApiService {
        return NotificationApiService(client)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(api: NotificationApiService): NotificationRepository {
        return NotificationRepositoryImpl(api)
    }
}
