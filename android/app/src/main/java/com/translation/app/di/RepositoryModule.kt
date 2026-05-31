package com.translation.app.di

import com.translation.app.domain.repository.*
import com.translation.app.repository.impl.*
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
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): IAuthRepository
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): IUserRepository
    @Binds
    @Singleton
    abstract fun bindTranslateRepository(impl: TranslateRepositoryImpl): ITranslateRepository
    @Binds
    @Singleton
    abstract fun bindWalletRepository(impl: WalletRepositoryImpl): IWalletRepository
    @Binds
    @Singleton
    abstract fun bindAnnouncementRepository(impl: AnnouncementRepositoryImpl): IAnnouncementRepository
    @Binds
    @Singleton
    abstract fun bindFeedbackRepository(impl: FeedbackRepositoryImpl): IFeedbackRepository
    @Binds
    @Singleton
    abstract fun bindRefundRepository(impl: RefundRepositoryImpl): IRefundRepository
}
