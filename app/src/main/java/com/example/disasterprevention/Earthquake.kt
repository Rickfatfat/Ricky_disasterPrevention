package com.example.disasterprevention

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Earthquake(
    val time: String,               // 時間
    val epicenter: String,          // 震央
    val magnitude: Double,          // 規模
    val taichung_intensity: String, // 當地影響級數
    val shakemap_url: String? = null // 右側圖片
) : Parcelable
