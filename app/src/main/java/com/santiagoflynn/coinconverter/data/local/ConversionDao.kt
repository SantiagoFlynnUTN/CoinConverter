package com.santiagoflynn.coinconverter.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversion(conversionEntity: ConversionEntity): Long

    @Query("SELECT * FROM conversions ORDER BY date DESC")
    fun getConversionHistory(): Flow<List<ConversionEntity>>

    @Query("SELECT * FROM conversions WHERE id = :id")
    suspend fun getConversionById(id: Long): ConversionEntity
}