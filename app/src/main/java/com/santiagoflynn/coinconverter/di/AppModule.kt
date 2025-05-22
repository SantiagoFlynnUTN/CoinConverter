package com.santiagoflynn.coinconverter.di

import com.santiagoflynn.coinconverter.data.repository.CurrencyRepositoryImpl
import com.santiagoflynn.coinconverter.domain.repository.CurrencyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        repositoryImpl: CurrencyRepositoryImpl
    ): CurrencyRepository
}