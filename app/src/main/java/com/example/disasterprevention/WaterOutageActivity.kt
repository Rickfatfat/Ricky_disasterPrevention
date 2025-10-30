package com.example.disasterprevention

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// ---------- 共用工具（檔案頂部；第二層/第四層皆可重用） ----------

// 把 "null" 字串與空白都視為無效
private fun String?.isNullOrBlankOrLiteralNull(): Boolean =
    this.isNullOrBlank() || this.equals("null", ignoreCase = true)

// 依「欄位優先於 reason 關鍵字」決定頁面標題
private fun titleFor(outage: WaterOutage): String {
    val hasWaterArea    = !outage.water_outage_areas.isNullOrBlankOrLiteralNull()
    val hasPressureArea = !outage.Buck_area.isNullOrBlankOrLiteralNull()

    // 先看實際欄位（有則以此為準）
    if (hasWaterArea && hasPressureArea) return "停水及降壓資訊"
    if (hasWaterArea) return "停水資訊"
    if (hasPressureArea) return "降壓資訊"

    // 欄位都沒有再看理由關鍵字
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

class WaterOutageActivity : AppCompatActivity() {

    private val TAG = "WaterOutageActivity"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_outage)

        // 1) 取得上一層傳來的資料
        val outage: WaterOutage? =
            intent.getParcelableExtra("first_outage")
        val moreList: ArrayList<WaterOutage>? =
            intent.getParcelableArrayListExtra("more_outages")

        // 2) 除錯用 Log
        Log.d(TAG, "===================== 收到資料 =====================")
        Log.d(TAG, "第一筆停水資料 (outage): $outage")
        Log.d(TAG, "更多公告列表 (moreList): $moreList")
        Log.d(TAG, "====================================================")

        // 3) findViewById（這些 TextView 在 XML 已有大標題，這裡只塞內容）
        val tvTitle  = findViewById<TextView>(R.id.tv_title)
        val tvReason = findViewById<TextView>(R.id.tv_reason)           // 只放「原因內容」
        val tvTime   = findViewById<TextView>(R.id.tv_time)             // 只放「時間內容」

        // 新版版面：分開顯示「停水區域 / 降壓區域」，各自有標題，無資料就隱藏
        val labelWater     = findViewById<TextView>(R.id.label_water_area)
        val tvWaterArea    = findViewById<TextView>(R.id.tv_water_area)
        val labelPressure  = findViewById<TextView>(R.id.label_pressure_area)
        val tvPressureArea = findViewById<TextView>(R.id.tv_pressure_area)
        val tvAreaEmpty    = findViewById<TextView>(R.id.tv_area_empty) // 兩者皆無時顯示

        val btnMore  = findViewById<Button>(R.id.btn_more)

        // 4) 塞畫面資料
        if (outage != null) {

            // ---- A. 標題（統一用共用邏輯）----
            tvTitle.text = titleFor(outage)

            // ---- B. 原因（只塞內容）----
            val reasonText = if (!outage.reason.isNullOrBlankOrLiteralNull())
                outage.reason!!.trim()
            else
                "未提供"
            tvReason.text = reasonText

            // ---- C. 影響時間（只塞內容）----
            val startPretty = shortenTime(outage.start_time)
            val endPretty   = shortenTime(outage.end_time)
            tvTime.text = if (startPretty != "-" || endPretty != "-")
                "$startPretty ~ $endPretty" else "未提供"

            // ---- D. 影響區域：分開顯示「停水」與「降壓」 ----
            val waterArea = outage.water_outage_areas
                ?.takeUnless { it.isNullOrBlankOrLiteralNull() }
                ?.trim()

            val pressureArea = outage.Buck_area   // 注意：模型欄位 B 大寫
                ?.takeUnless { it.isNullOrBlankOrLiteralNull() }
                ?.trim()

            // 停水區域
            if (waterArea != null) {
                labelWater.visibility = View.VISIBLE
                tvWaterArea.visibility = View.VISIBLE
                tvWaterArea.text = waterArea
            } else {
                labelWater.visibility = View.GONE
                tvWaterArea.visibility = View.GONE
            }

            // 降壓區域
            if (pressureArea != null) {
                labelPressure.visibility = View.VISIBLE
                tvPressureArea.visibility = View.VISIBLE
                tvPressureArea.text = pressureArea
            } else {
                labelPressure.visibility = View.GONE
                tvPressureArea.visibility = View.GONE
            }

            // 兩者都沒有 → 顯示備援訊息
            tvAreaEmpty.visibility =
                if (waterArea == null && pressureArea == null) View.VISIBLE else View.GONE

        } else {
            // 無資料時的保底（只放內容，不含標題字樣）
            tvTitle.text  = "公告"
            tvReason.text = "未提供"
            tvTime.text   = "未提供"

            labelWater.visibility = View.GONE
            tvWaterArea.visibility = View.GONE
            labelPressure.visibility = View.GONE
            tvPressureArea.visibility = View.GONE
            tvAreaEmpty.visibility = View.VISIBLE
        }

        // 5) 更多公告按鈕
        if (moreList.isNullOrEmpty()) {
            btnMore.isEnabled = false
            btnMore.alpha = 0.3f
        } else {
            btnMore.isEnabled = true
            btnMore.alpha = 1f
            btnMore.setOnClickListener {
                val intent = Intent(this, WaterOutageMoreActivity::class.java)
                intent.putParcelableArrayListExtra("more_outages", moreList)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
            }
        }
    }
}
