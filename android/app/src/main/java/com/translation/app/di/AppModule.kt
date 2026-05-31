package com.translation.app.di

import android.content.Context
import com.translation.app.data.local.PreferencesManager
import com.translation.app.util.PaymentManager
import com.translation.app.util.SocialLoginManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun providePaymentManager(@ApplicationContext context: Context): PaymentManager {
        return PaymentManager(context)
    }

    @Provides
    @Singleton
    fun provideSocialLoginManager(@ApplicationContext context: Context): SocialLoginManager {
        return SocialLoginManager(context)
    }
}
