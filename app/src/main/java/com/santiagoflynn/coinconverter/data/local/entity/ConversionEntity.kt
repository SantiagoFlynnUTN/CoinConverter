package com.santiagoflynn.coinconverter.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.santiagoflynn.coinconverter.domain.model.ConversionResult
import java.util.Date

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
) {
    fun toConversionResult(): ConversionResult {
        return ConversionResult(
            id = id,
            fromCurrency = fromCurrency,
            toCurrency = toCurrency,
            fromAmount = fromAmount,
            toAmount = toAmount,
            rate = rate,
            date = Date(date)
        )
    }

    companion object {
        fun fromConversionResult(result: ConversionResult): ConversionEntity {
            return ConversionEntity(
                id = result.id,
                fromCurrency = result.fromCurrency,
                toCurrency = result.toCurrency,
                fromAmount = result.fromAmount,
                toAmount = result.toAmount,
                rate = result.rate,
                date = result.date.time
            )
        }
    }
}