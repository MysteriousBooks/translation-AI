package com.translation.app.di

import com.translation.app.data.api.ApiClient
import com.translation.app.data.api.services.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthApiService(apiClient: ApiClient): AuthApiService = apiClient.createService()

    @Provides
    @Singleton
    fun provideUserApiService(apiClient: ApiClient): UserApiService = apiClient.createService()

    @Provides
    @Singleton
    fun provideTranslateApiService(apiClient: ApiClient): TranslateApiService = apiClient.createService()

    @Provides
    @Singleton
    fun provideWalletApiService(apiClient: ApiClient): WalletApiService = apiClient.createService()

    @Provides
    @Singleton
    fun provideAnnouncementApiService(apiClient: ApiClient): AnnouncementApiService = apiClient.createService()

    @Provides
    @Singleton
    fun provideFeedbackApiService(apiClient: ApiClient): FeedbackApiService = apiClient.createService()

    @Provides
    @Singleton
    fun provideRefundApiService(apiClient: ApiClient): RefundApiService = apiClient.createService()
}
