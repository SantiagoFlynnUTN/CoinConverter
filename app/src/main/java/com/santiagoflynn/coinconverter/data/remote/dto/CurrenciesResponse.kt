package com.santiagoflynn.coinconverter.data.remote.dto

import com.santiagoflynn.coinconverter.domain.model.Currency
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrenciesResponse(
    @Json(name = "data")
    val data: Map<String, CurrencyDto>
)

@JsonClass(generateAdapter = true)
data class CurrencyDto(
    @Json(name = "symbol")
    val symbol: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "symbol_native")
    val symbolNative: String,
    @Json(name = "decimal_digits")
    val decimalDigits: Int,
    @Json(name = "rounding")
    val rounding: Int,
    @Json(name = "code")
    val code: String,
    @Json(name = "name_plural")
    val namePlural: String
) {
    fun toCurrency(): Currency {
        return Currency(
            code = code,
            name = name
        )
    }
}