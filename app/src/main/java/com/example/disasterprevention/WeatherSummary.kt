package com.example.disasterprevention

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WeatherSummary(
    val date: String,
    @SerializedName("max_temperature") val maxTemperature: Int,
    @SerializedName("min_temperature") val minTemperature: Int,
    @SerializedName("precipitation_probability") val precipitationProbability: Int,
    @SerializedName("weather_icon") val weatherIcon: String,
    val advice: String
) : Serializable
