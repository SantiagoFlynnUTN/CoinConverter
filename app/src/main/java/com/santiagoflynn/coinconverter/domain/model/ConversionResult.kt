package com.santiagoflynn.coinconverter.domain.model

import java.util.Date

data class ConversionResult(
    val id: Long = 0,
    val fromCurrency: String,
    val toCurrency: String,
    val fromAmount: Double,
    val toAmount: Double,
    val rate: Double,
    val date: Date = Date()
)