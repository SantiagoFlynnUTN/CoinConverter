package com.santiagoflynn.coinconverter.data.local.di

import android.content.Context
import androidx.room.Room
import com.santiagoflynn.coinconverter.data.local.CoinConverterDatabase
import com.santiagoflynn.coinconverter.data.local.dao.ConversionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CoinConverterDatabase {
        return Room.databaseBuilder(
            context,
            CoinConverterDatabase::class.java,
            "coin_converter_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideConversionDao(database: CoinConverterDatabase): ConversionDao {
        return database.conversionDao()
    }
}