package com.santiagoflynn.coinconverter.domain.repository

import com.santiagoflynn.coinconverter.domain.model.ConversionResult
import com.santiagoflynn.coinconverter.domain.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlin.Result

interface CurrencyRepository {
    suspend fun getCurrencies(): Result<List<Currency>>
    suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): Result<Double>
    suspend fun saveConversion(conversion: ConversionResult): Result<Long>
    fun getConversionHistory(): Flow<Result<List<ConversionResult>>>
    suspend fun getConversionById(id: Long): Result<ConversionResult>
}