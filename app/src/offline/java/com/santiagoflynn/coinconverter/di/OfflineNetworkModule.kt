package com.santiagoflynn.coinconverter.di

import com.santiagoflynn.coinconverter.data.remote.api.MockFreeCurrencyApi
import com.santiagoflynn.coinconverter.data.remote.api.FreeCurrencyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OfflineNetworkModule {

    @Provides
    @Singleton
    fun provideFreeCurrencyApi(): FreeCurrencyApi {
        return MockFreeCurrencyApi()
    }
}