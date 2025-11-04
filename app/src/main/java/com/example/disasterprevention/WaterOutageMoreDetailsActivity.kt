package com.example.disasterprevention

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// ---------- 共用工具（與第二層相同） ----------

// 把 "null" 字串與空白都視為無效
private fun String?.isNullOrBlankOrLiteralNull(): Boolean =
    this.isNullOrBlank() || this.equals("null", ignoreCase = true)

// 依「欄位優先於 reason 關鍵字」決定頁面標題
private fun titleFor(outage: WaterOutage): String {
    val hasWaterArea    = !outage.water_outage_areas.isNullOrBlankOrLiteralNull()
    val hasPressureArea = !outage.Buck_area.isNullOrBlankOrLiteralNull()

    if (hasWaterArea && hasPressureArea) return "停水及降壓資訊"
    if (hasWaterArea) return "停水資訊"
    if (hasPressureArea) return "降壓資訊"

    val reason = outage.reason.orEmpty()
    val hasOutageByReason   = reason.contains("停水") || reason.contains("無水")
    val hasPressureByReason = reason.contains("降壓")
    return when {
        hasOutageByReason && hasPressureByReason -> "停水及降壓資訊"
        hasOutageByReason                        -> "停水資訊"
        hasPressureByReason                      -> "降壓資訊"
        else                                     -> "最新公告"
    }
}

// yyyy-MM-dd HH:mm:ss -> MM/dd HH:mm；失敗就回原字串或 "-"
private fun shortenTime(raw: String?): String {
    if (raw.isNullOrBlank()) return "-"
    val parts = raw.split(" ")
    if (parts.size < 2) return raw
    val datePart = parts[0]              // yyyy-MM-dd
    val timePart = parts[1]              // HH:mm:ss
    val dateTokens = datePart.split("-")
    val mmdd = if (dateTokens.size == 3) {
        "${dateTokens[1]}/${dateTokens[2]}" // 10/29
    } else datePart
    val hhmm = timePart.take(5)          // 09:30
    return "$mmdd $hhmm"
}

class WaterOutageMoreDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_outage_details)

        val tvTitle     = findViewById<TextView>(R.id.tv_title)
        val tvReason    = findViewById<TextView>(R.id.tv_reason)
        val tvTime      = findViewById<TextView>(R.id.tv_time)

        // 重點：抓「整個區塊容器」，用 GONE/ VISIBLE 控制，避免雙線
        val sectionWater    = findViewById<View>(R.id.section_water)
        val sectionPressure = findViewById<View>(R.id.section_pressure)

        val tvWaterArea     = findViewById<TextView>(R.id.tv_water_area)
        val tvPressureArea  = findViewById<TextView>(R.id.tv_pressure_area)
        val tvAreaEmpty     = findViewById<TextView>(R.id.tv_area_empty)

        // 取資料（相容兩種 key）
        val outage: WaterOutage? =
            intent.getParcelableExtra("outage_detail")
                ?: intent.getParcelableExtra("first_outage")

        if (outage == null) {
            tvTitle.text  = "公告"
            tvReason.text = "未提供"
            tvTime.text   = "未提供"

            // 兩區塊都隱藏，僅顯示備援
            sectionWater.visibility = View.GONE
            sectionPressure.visibility = View.GONE
            tvAreaEmpty.visibility = View.VISIBLE
            return
        }

        // ---- 標題 ----
        tvTitle.text = titleFor(outage)

        // ---- 原因 ----
        val reasonText = if (!outage.reason.isNullOrBlankOrLiteralNull())
            outage.reason!!.trim() else "未提供"
        tvReason.text = reasonText

        // ---- 影響時間 ----
        val startPretty = shortenTime(outage.start_time)
        val endPretty   = shortenTime(outage.end_time)
        tvTime.text = if (startPretty != "-" || endPretty != "-")
            "$startPretty ~ $endPretty" else "未提供"

        // ---- 區域（整個容器顯示/隱藏）----
        val waterArea = outage.water_outage_areas
            ?.takeUnless { it.isNullOrBlankOrLiteralNull() }
            ?.trim()
        val pressureArea = outage.Buck_area
            ?.takeUnless { it.isNullOrBlankOrLiteralNull() }
            ?.trim()

        if (waterArea != null) {
            sectionWater.visibility = View.VISIBLE
            tvWaterArea.text = waterArea
        } else {
            sectionWater.visibility = View.GONE
        }

        if (pressureArea != null) {
            sectionPressure.visibility = View.VISIBLE
            tvPressureArea.text = pressureArea
        } else {
            sectionPressure.visibility = View.GONE
        }

        // 兩者皆無 → 顯示備援
        tvAreaEmpty.visibility =
            if (waterArea == null && pressureArea == null) View.VISIBLE else View.GONE
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.slide_out_right
        )
    }
}
