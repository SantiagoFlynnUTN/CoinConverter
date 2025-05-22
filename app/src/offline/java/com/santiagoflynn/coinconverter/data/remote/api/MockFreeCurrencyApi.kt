package com.santiagoflynn.coinconverter.data.remote.api

import com.santiagoflynn.coinconverter.data.remote.dto.CurrenciesResponse
import com.santiagoflynn.coinconverter.data.remote.dto.CurrencyDto
import com.santiagoflynn.coinconverter.data.remote.dto.LatestRatesResponse


class MockFreeCurrencyApi : FreeCurrencyApi {

    private val mockCurrencies = mapOf(
        "USD" to CurrencyDto(
            symbol = "$",
            name = "US Dollar",
            symbolNative = "$",
            decimalDigits = 2,
            rounding = 0,
            code = "USD",
            namePlural = "US dollars"
        ),
        "EUR" to CurrencyDto(
            symbol = "€",
            name = "Euro",
            symbolNative = "€",
            decimalDigits = 2,
            rounding = 0,
            code = "EUR",
            namePlural = "euros"
        ),
        "GBP" to CurrencyDto(
            symbol = "£",
            name = "British Pound",
            symbolNative = "£",
            decimalDigits = 2,
            rounding = 0,
            code = "GBP",
            namePlural = "British pounds"
        ),
        "JPY" to CurrencyDto(
            symbol = "¥",
            name = "Japanese Yen",
            symbolNative = "￥",
            decimalDigits = 0,
            rounding = 0,
            code = "JPY",
            namePlural = "Japanese yen"
        ),
        "CAD" to CurrencyDto(
            symbol = "CA$",
            name = "Canadian Dollar",
            symbolNative = "$",
            decimalDigits = 2,
            rounding = 0,
            code = "CAD",
            namePlural = "Canadian dollars"
        ),
        "AUD" to CurrencyDto(
            symbol = "A$",
            name = "Australian Dollar",
            symbolNative = "$",
            decimalDigits = 2,
            rounding = 0,
            code = "AUD",
            namePlural = "Australian dollars"
        ),
        "CHF" to CurrencyDto(
            symbol = "CHF",
            name = "Swiss Franc",
            symbolNative = "CHF",
            decimalDigits = 2,
            rounding = 0,
            code = "CHF",
            namePlural = "Swiss francs"
        ),
        "CNY" to CurrencyDto(
            symbol = "CN¥",
            name = "Chinese Yuan",
            symbolNative = "¥",
            decimalDigits = 2,
            rounding = 0,
            code = "CNY",
            namePlural = "Chinese yuan"
        )
    )

    private val mockRates = mapOf(
        "EUR" to mapOf(
            "USD" to 1.08,
            "GBP" to 0.85,
            "JPY" to 160.0,
            "CAD" to 1.46,
            "AUD" to 1.64,
            "CHF" to 0.98,
            "CNY" to 7.82
        ),
        "USD" to mapOf(
            "EUR" to 0.93,
            "GBP" to 0.79,
            "JPY" to 148.0,
            "CAD" to 1.35,
            "AUD" to 1.52,
            "CHF" to 0.91,
            "CNY" to 7.24
        ),
        "GBP" to mapOf(
            "EUR" to 1.17,
            "USD" to 1.27,
            "JPY" to 188.0,
            "CAD" to 1.71,
            "AUD" to 1.92,
            "CHF" to 1.15,
            "CNY" to 9.17
        ),
        "JPY" to mapOf(
            "EUR" to 0.00625,
            "USD" to 0.00676,
            "GBP" to 0.00532,
            "CAD" to 0.00912,
            "AUD" to 0.01022,
            "CHF" to 0.00612,
            "CNY" to 0.04883
        ),
        "CAD" to mapOf(
            "EUR" to 0.68,
            "USD" to 0.74,
            "GBP" to 0.58,
            "JPY" to 109.7,
            "AUD" to 1.12,
            "CHF" to 0.67,
            "CNY" to 5.36
        ),
        "AUD" to mapOf(
            "EUR" to 0.61,
            "USD" to 0.66,
            "GBP" to 0.52,
            "JPY" to 97.8,
            "CAD" to 0.89,
            "CHF" to 0.60,
            "CNY" to 4.76
        ),
        "CHF" to mapOf(
            "EUR" to 1.02,
            "USD" to 1.10,
            "GBP" to 0.87,
            "JPY" to 163.5,
            "CAD" to 1.48,
            "AUD" to 1.67,
            "CNY" to 7.96
        ),
        "CNY" to mapOf(
            "EUR" to 0.13,
            "USD" to 0.14,
            "GBP" to 0.11,
            "JPY" to 20.5,
            "CAD" to 0.19,
            "AUD" to 0.21,
            "CHF" to 0.13
        )
    )

    override suspend fun getCurrencies(apiKey: String, currencies: String?): CurrenciesResponse {
        val filteredCurrencies = if (currencies != null) {
            val requestedCurrencies = currencies.split(",")
            mockCurrencies.filterKeys { it in requestedCurrencies }
        } else {
            mockCurrencies
        }

        return CurrenciesResponse(filteredCurrencies)
    }

    override suspend fun getLatestRates(
        apiKey: String,
        baseCurrency: String?,
        currencies: String?
    ): LatestRatesResponse {
        val base = baseCurrency ?: "USD"

        if (base !in mockRates) {
            return LatestRatesResponse(emptyMap())
        }

        val rates = mockRates[base] ?: emptyMap()

        val filteredRates = if (currencies != null) {
            val requestedCurrencies = currencies.split(",")
            rates.filterKeys { it in requestedCurrencies }
        } else {
            rates
        }

        return LatestRatesResponse(filteredRates)
    }
}