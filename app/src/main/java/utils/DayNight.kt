package com.example.disasterprevention.utils

import java.time.ZoneId
import java.time.ZonedDateTime

object DayNight {
    fun isNightNow(): Boolean {
        val now = ZonedDateTime.now(ZoneId.of("Asia/Taipei"))
        val h = now.hour
        return h >= 18 || h < 6
    }
}
