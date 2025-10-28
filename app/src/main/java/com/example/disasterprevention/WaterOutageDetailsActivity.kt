package com.example.disasterprevention

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class WaterOutageDetailsActivity : AppCompatActivity() {

    private fun shortenTime(raw: String?): String {
        if (raw.isNullOrBlank()) return "-"
        val parts = raw.split(" ")
        if (parts.size < 2) return raw
        val datePart = parts[0] // yyyy-MM-dd
        val timePart = parts[1] // HH:mm:ss
        val dateTokens = datePart.split("-")
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
        // 你可以沿用 activity_water_outage.xml ，或做一個專門layout。下面我先用 activity_water_outage_details.xml
        setContentView(R.layout.activity_water_outage_details)

        val outage = intent.getParcelableExtra<WaterOutage>("outage")

        val tvTitle   = findViewById<TextView>(R.id.tv_title)
        val tvArea    = findViewById<TextView>(R.id.tv_area)
        val tvTime    = findViewById<TextView>(R.id.tv_time)
        val tvReason  = findViewById<TextView>(R.id.tv_reason)

        tvTitle.text = "停水資訊"

        if (outage != null) {
            val startPretty = shortenTime(outage.start_time)
            val endPretty   = shortenTime(outage.end_time)

            tvArea.text = "影響區域：\n${outage.water_outage_areas ?: "未提供"}"
            tvTime.text = "停水時間：\n${startPretty} ~ ${endPretty}"
            tvReason.text = "原因：\n${outage.reason ?: "未提供"}"
        } else {
            tvArea.text   = "影響區域：\n-"
            tvTime.text   = "停水時間：\n-"
            tvReason.text = "原因：\n-"
        }
    }
}
