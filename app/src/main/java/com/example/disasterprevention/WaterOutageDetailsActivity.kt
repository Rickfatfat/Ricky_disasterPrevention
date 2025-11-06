package com.example.disasterprevention

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// ---------- 共用工具：與第二層一致 ----------
private fun String?.isNullOrBlankOrLiteralNull(): Boolean =
    this.isNullOrBlank() || this.equals("null", ignoreCase = true)

private fun titleFor(outage: WaterOutage): String {
    val hasWaterArea    = !outage.water_outage_areas.isNullOrBlankOrLiteralNull()
    val hasPressureArea = !outage.Buck_area.isNullOrBlankOrLiteralNull()

    if (hasWaterArea && hasPressureArea) return "停水及降壓資訊"
    if (hasWaterArea) return "停水資訊"
    if (hasPressureArea) return "降壓資訊"

    val r = outage.reason.orEmpty()
    val byOutage   = r.contains("停水") || r.contains("無水")
    val byPressure = r.contains("降壓")
    return when {
        byOutage && byPressure -> "停水及降壓資訊"
        byOutage              -> "停水資訊"
        byPressure            -> "降壓資訊"
        else                  -> "最新公告"
    }
}

private fun shortenTime(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"
    val parts = raw.split(" ")
    if (parts.size < 2) return raw
    val day = parts[0].split("-")
    val mmdd = if (day.size == 3) "${day[1]}/${day[2]}" else parts[0]
    val hhmm = parts[1].take(5)
    return "$mmdd $hhmm"
}

class WaterOutageDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_outage_details)

        val tvTitle        = findViewById<TextView>(R.id.tv_title)
        val tvReason       = findViewById<TextView>(R.id.tv_reason)
        val tvTime         = findViewById<TextView>(R.id.tv_time)

        val labelWater     = findViewById<TextView>(R.id.label_water_area)
        val tvWaterArea    = findViewById<TextView>(R.id.tv_water_area)
        val labelPressure  = findViewById<TextView>(R.id.label_pressure_area)
        val tvPressureArea = findViewById<TextView>(R.id.tv_pressure_area)
        val tvAreaEmpty    = findViewById<TextView>(R.id.tv_area_empty)

        // 支援多種 key，避免來源不一致
        val outage: WaterOutage? =
            intent.getParcelableExtra("outage") ?:
            intent.getParcelableExtra("outage_detail") ?:
            intent.getParcelableExtra("first_outage")

        if (outage == null) {
            tvTitle.text = "公告"
            tvReason.text = "未提供"
            tvTime.text   = "未提供"

            labelWater.visibility = View.GONE
            tvWaterArea.visibility = View.GONE
            labelPressure.visibility = View.GONE
            tvPressureArea.visibility = View.GONE
            tvAreaEmpty.visibility = View.VISIBLE
            return
        }

        // 標題
        tvTitle.text = titleFor(outage)

        // 原因（只塞內容）
        tvReason.text = if (!outage.reason.isNullOrBlankOrLiteralNull())
            outage.reason!!.trim() else "未提供"

        // 影響時間（只塞內容）
        val startPretty = shortenTime(outage.start_time)
        val endPretty   = shortenTime(outage.end_time)
        tvTime.text = if (startPretty != "-" || endPretty != "-")
            "$startPretty ~ $endPretty" else "未提供"

        // 影響區域（分開顯示）
        val waterArea = outage.water_outage_areas
            ?.takeUnless { it.isNullOrBlankOrLiteralNull() }
            ?.trim()
        val pressureArea = outage.Buck_area
            ?.takeUnless { it.isNullOrBlankOrLiteralNull() }
            ?.trim()

        if (waterArea != null) {
            labelWater.visibility = View.VISIBLE
            tvWaterArea.visibility = View.VISIBLE
            tvWaterArea.text = waterArea
        } else {
            labelWater.visibility = View.GONE
            tvWaterArea.visibility = View.GONE
        }

        if (pressureArea != null) {
            labelPressure.visibility = View.VISIBLE
            tvPressureArea.visibility = View.VISIBLE
            tvPressureArea.text = pressureArea
        } else {
            labelPressure.visibility = View.GONE
            tvPressureArea.visibility = View.GONE
        }

        // 兩者皆無 → 顯示備援
        tvAreaEmpty.visibility =
            if (waterArea == null && pressureArea == null) View.VISIBLE else View.GONE
    }
}