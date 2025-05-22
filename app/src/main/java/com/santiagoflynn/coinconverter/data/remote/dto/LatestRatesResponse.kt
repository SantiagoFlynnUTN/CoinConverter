package com.santiagoflynn.coinconverter.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LatestRatesResponse(
    @Json(name = "data")
    val rates: Map<String, Double>
)