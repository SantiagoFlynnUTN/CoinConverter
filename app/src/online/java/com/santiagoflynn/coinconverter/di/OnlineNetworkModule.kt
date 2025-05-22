package com.santiagoflynn.coinconverter.di

import com.santiagoflynn.coinconverter.data.remote.api.FreeCurrencyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnlineNetworkModule {
    @Provides
    @Singleton
    fun provideFreeCurrencyApi(retrofit: Retrofit): FreeCurrencyApi {
        return retrofit.create(FreeCurrencyApi::class.java)
    }
}