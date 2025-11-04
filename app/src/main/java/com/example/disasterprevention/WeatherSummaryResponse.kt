package com.example.disasterprevention

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WeatherSummaryResponse(
    val location: String,
    @SerializedName("current_time") val currentTime: String,
    @SerializedName("daily_summary") val dailySummary: List<WeatherSummary>
) : Serializable
