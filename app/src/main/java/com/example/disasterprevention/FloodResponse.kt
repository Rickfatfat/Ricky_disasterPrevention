package com.example.disasterprevention

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 淹水感測器 API 的最外層回應 Model
 */
@Parcelize
data class FloodResponse(
    @SerializedName("status_code")
    val statusCode: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<FloodStation>
) : Parcelable

/**
 * 代表單一淹水感測站的資料 Model
 */
@Parcelize
data class FloodStation(
    @SerializedName("station_name")
    val stationName: String, // 測站名稱

    @SerializedName("current_level")
    val currentLevel: Double, // 目前水位

    @SerializedName("yellow_level")
    val yellowLevel: Double, // 二級警戒水位

    @SerializedName("red_level")
    val redLevel: Double, // 一級警戒水位

    @SerializedName("alert_status")
    val alertStatus: String, // 警戒狀態 (正常, 注意, 警戒)

    @SerializedName("image_url")
    val imageUrl: String, // 影像 URL

    @SerializedName("update_time")
    val updateTime: String // 資料更新時間
) : Parcelable