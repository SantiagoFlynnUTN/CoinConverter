package com.santiagoflynn.coinconverter.data.remote

import com.santiagoflynn.coinconverter.data.remote.api.FreeCurrencyApi
import javax.inject.Inject
import javax.inject.Named

class RemoteDataSource @Inject constructor(
    private val api: FreeCurrencyApi,
    @Named("ApiKey") private val apiKey: String
) {
    suspend fun getCurrencies(currencyCodes: List<String>? = null) =
        api.getCurrencies(
            apiKey = apiKey,
            currencies = currencyCodes?.joinToString(",")
        )

    suspend fun getExchangeRate(
        fromCurrency: String,
        toCurrency: String
    ): Double {
        val response = api.getLatestRates(
            apiKey = apiKey,
            baseCurrency = fromCurrency,
            currencies = toCurrency
        )
        return response.rates[toCurrency] ?: throw NoSuchElementException("Exchange rate not found")
    }
}