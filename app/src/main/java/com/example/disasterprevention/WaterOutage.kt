package com.example.disasterprevention

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WaterOutage(
    val reason: String?,
    val start_time: String?,
    val end_time: String?,
    val time_duration: String?,
    val water_outage_areas: String?,
    val Buck_area: String?

) : Parcelable