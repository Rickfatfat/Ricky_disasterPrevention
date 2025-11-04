package com.example.disasterprevention

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

object WeatherArt {

    data class Art(@DrawableRes val bg: Int, @RawRes val lottie: Int)

    fun artFor(weatherIcon: String?, precipitationProbability: Int, isNight: Boolean): Art {
        val lottie = lottieByIcon(weatherIcon, isNight)
            ?: lottieByPrecip(precipitationProbability, isNight)

        val bg = backgroundByCondition(
            precip = precipitationProbability,
            weatherIcon = weatherIcon ?: "",
            isNight = isNight
        )
        return Art(bg = bg, lottie = lottie)
    }
    @RawRes
    fun lottieByIcon(iconRaw: String?, isNight: Boolean): Int? {
        val icon = iconRaw?.lowercase()?.trim() ?: return null
        return when {
            // 晴
            icon.contains("sunny") || icon.contains("clear") ->
                if (isNight) R.raw.night else R.raw.sunny
            // 多雲／陰
            icon.contains("partly") && icon.contains("cloud") ->
                if (isNight) R.raw.cloudynight else R.raw.partlycloudy
            icon.contains("cloud") || icon.contains("overcast") ->
                if (isNight) R.raw.cloudynight else R.raw.partlycloudy
            // 風／雨／暴風
            icon.contains("storm") -> R.raw.storm
            icon.contains("thunder") -> R.raw.storm
            icon.contains("rain") || icon.contains("shower") ->
                if (isNight) R.raw.rainynight else R.raw.partlyshower
            icon.contains("wind") -> R.raw.windy
            else -> null
        }
    }

    /** 依降雨機率 + 晝夜判斷  */
    @RawRes
    fun lottieByPrecip(precipitationProbability: Int, isNight: Boolean): Int {
        val p = precipitationProbability.coerceIn(0, 100)
        return when {
            p >= 90 -> R.raw.storm
            p >= 65 -> if (isNight) R.raw.rainynight else R.raw.partlyshower
            p >= 50 -> R.raw.windy
            p >= 11 -> if (isNight) R.raw.cloudynight else R.raw.partlycloudy
            else    -> if (isNight) R.raw.night else R.raw.sunny
        }
    }

    /** 依 icon + 降雨機率 + 晝夜決定背景 */
    @DrawableRes
    fun backgroundByCondition(precip: Int, weatherIcon: String, isNight: Boolean): Int {
        val icon = weatherIcon.lowercase()
        return when {
            icon.contains("rain") || icon.contains("storm") || icon.contains("shower") || precip >= 60 ->
                R.drawable.bg_rain
            icon.contains("cloud") || icon.contains("overcast") ->
                R.drawable.bg_cloudy
            icon.contains("clear") || icon.contains("sunny") ->
                if (isNight) R.drawable.bg_night else R.drawable.bg_sunny
            else ->
                if (isNight) R.drawable.bg_night else R.drawable.bg_sunny
        }
    }
}
