package com.santiagoflynn.coinconverter.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.santiagoflynn.coinconverter.data.local.dao.ConversionDao
import com.santiagoflynn.coinconverter.data.local.entity.ConversionEntity

@Database(
    entities = [ConversionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CoinConverterDatabase : RoomDatabase() {

    abstract fun conversionDao(): ConversionDao
}