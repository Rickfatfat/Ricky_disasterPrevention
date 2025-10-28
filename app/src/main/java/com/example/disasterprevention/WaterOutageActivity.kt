package com.example.disasterprevention

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class WaterOutageActivity : AppCompatActivity() {

    // 小工具：把 "2025-10-08 10:14:00" → "10/08 10:14"
    private fun shortenTime(raw: String?): String {
        if (raw.isNullOrBlank()) return "-"
        val parts = raw.split(" ")
        if (parts.size < 2) return raw

        val datePart = parts[0] // e.g. 2025-10-08
        val timePart = parts[1] // e.g. 10:14:00

        val dateTokens = datePart.split("-") // [2025, 10, 08]
        val mmdd = if (dateTokens.size == 3) {
            "${dateTokens[1]}/${dateTokens[2]}" // 10/08
        } else {
            datePart
        }

        val hhmm = timePart.substring(0, 5) // "10:14"
        return "$mmdd $hhmm"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_outage)

        // 1. 從 Intent 取出剛剛傳進來的停水資料
        val outage = intent.getParcelableExtra<WaterOutage>("outage")

        // 2. 找到畫面上的 TextView
        val tvTitle = findViewById<TextView>(R.id.tv_title)
        val tvArea = findViewById<TextView>(R.id.tv_area)
        val tvTime = findViewById<TextView>(R.id.tv_time)
        val tvReason = findViewById<TextView>(R.id.tv_reason)

        if (outage != null) {
            val startPretty = shortenTime(outage.start_time)
            val endPretty = shortenTime(outage.end_time)

            tvTitle.text = "停水資訊"
            tvArea.text = "影響區域：\n${outage.water_outage_areas ?: "未提供"}"
            tvTime.text = "停水時間：\n${startPretty} ~ ${endPretty}"
            tvReason.text = "原因：\n${outage.reason ?: "未提供"}"
        } else {
            // 理論上不會 null，除非點擊時沒塞資料
            tvTitle.text = "停水資訊"
            tvArea.text = "無資料"
            tvTime.text = ""
            tvReason.text = ""
        }
    }
}
