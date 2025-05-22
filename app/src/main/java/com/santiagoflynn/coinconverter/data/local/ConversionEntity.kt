package com.santiagoflynn.coinconverter.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversions")
data class ConversionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromCurrency: String,
    val toCurrency: String,
    val fromAmount: Double,
    val toAmount: Double,
    val rate: Double,
    val date: Long
)