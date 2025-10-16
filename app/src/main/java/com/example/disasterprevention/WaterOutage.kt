package com.example.disasterprevention

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WaterOutage(
    val reason: String,               // 原因
    val start_time: String,          // 開始時間
    val end_time: String,          // 結束時間
    val time_duration: Double, // 影響時長
    val water_outage_areas: String,// 停水區域
    val Buck_area: String, // 降壓區域

) : Parcelable
