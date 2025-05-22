package com.santiagoflynn.coinconverter.data.repository

import com.santiagoflynn.coinconverter.data.local.dao.ConversionDao
import com.santiagoflynn.coinconverter.data.local.entity.ConversionEntity
import com.santiagoflynn.coinconverter.data.remote.RemoteDataSource
import com.santiagoflynn.coinconverter.data.remote.dto.CurrencyDto
import com.santiagoflynn.coinconverter.domain.model.ConversionResult
import com.santiagoflynn.coinconverter.domain.model.Currency
import com.santiagoflynn.coinconverter.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Result

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val conversionDao: ConversionDao,
    private val remoteDataSource: RemoteDataSource
) : CurrencyRepository {

    override suspend fun getCurrencies(): Result<List<Currency>> {
        return runCatching {
            val response = remoteDataSource.getCurrencies()
            response.data.values.map { dto: CurrencyDto -> dto.toCurrency() }
        }
    }

    override suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): Result<Double> {
        return runCatching {
            remoteDataSource.getExchangeRate(fromCurrency, toCurrency)
        }
    }

    override suspend fun saveConversion(conversion: ConversionResult): Result<Long> {
        return runCatching {
            val conversionEntity = ConversionEntity.fromConversionResult(conversion)
            conversionDao.insertConversion(conversionEntity)
        }
    }

    override fun getConversionHistory(): Flow<Result<List<ConversionResult>>> {
        return conversionDao.getAllConversions()
            .map { entities ->
                runCatching {
                    entities.map { it.toConversionResult() }
                }
            }
    }

    override suspend fun getConversionById(id: Long): Result<ConversionResult> {
        return runCatching {
            conversionDao.getConversionById(id)?.toConversionResult()
                ?: throw IllegalArgumentException("Conversion with id $id not found")
        }
    }
}