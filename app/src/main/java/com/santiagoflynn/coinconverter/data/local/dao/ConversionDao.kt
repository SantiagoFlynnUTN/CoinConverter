package com.santiagoflynn.coinconverter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.santiagoflynn.coinconverter.data.local.entity.ConversionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversion(conversion: ConversionEntity): Long

    @Query("SELECT * FROM conversions ORDER BY date DESC")
    fun getAllConversions(): Flow<List<ConversionEntity>>

    @Query("SELECT * FROM conversions WHERE id = :id")
    suspend fun getConversionById(id: Long): ConversionEntity?
}