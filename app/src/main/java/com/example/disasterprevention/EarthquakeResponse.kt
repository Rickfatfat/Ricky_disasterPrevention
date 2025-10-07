package com.example.disasterprevention

data class EarthquakeResponse(
    val status_code: Int,
    val message: String,
    val data: List<Earthquake>
)
