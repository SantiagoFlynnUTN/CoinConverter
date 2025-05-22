package com.santiagoflynn.coinconverter.data.remote.api

import com.santiagoflynn.coinconverter.data.remote.dto.CurrenciesResponse
import com.santiagoflynn.coinconverter.data.remote.dto.LatestRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FreeCurrencyApi {
    
    @GET("v1/currencies")
    suspend fun getCurrencies(
        @Query("apikey") apiKey: String,
        @Query("currencies") currencies: String? = null
    ): CurrenciesResponse
    
    @GET("v1/latest")
    suspend fun getLatestRates(
        @Query("apikey") apiKey: String,
        @Query("base_currency") baseCurrency: String? = null,
        @Query("currencies") currencies: String? = null
    ): LatestRatesResponse
}