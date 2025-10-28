package com.example.disasterprevention.ui.weather

import java.io.Serializable

data class WeatherSummary(
    val date: String,
    val maxTemperature: Int,
    val minTemperature: Int,
    val precipitationProbability: Int,
    val weatherIcon: String,
    val advice: String
) : Serializable
