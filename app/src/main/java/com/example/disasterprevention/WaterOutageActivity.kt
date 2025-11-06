package com.example.disasterprevention

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// ---------- 共用工具 ----------
private fun String?.isNullOrBlankOrLiteralNull(): Boolean =
    this.isNullOrBlank() || this.equals("null", ignoreCase = true)

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

class WaterOutageActivity : AppCompatActivity() {

    private val TAG = "WaterOutageActivity"

    private fun shortenTime(raw: String?): String {
        if (raw.isNullOrBlank()) return "-"
        val parts = raw.split(" ")
        if (parts.size < 2) return raw
        val datePart = parts[0]
        val timePart = parts[1]
        val dateTokens = datePart.split("-")
        val mmdd = if (dateTokens.size == 3) "${dateTokens[1]}/${dateTokens[2]}" else datePart
        val hhmm = timePart.take(5)
        return "$mmdd $hhmm"
    }

    private lateinit var sectionMain: View
    private lateinit var btnMore: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_outage)

        val outage: WaterOutage? = intent.getParcelableExtra("first_outage")
        val moreList: ArrayList<WaterOutage>? =
            intent.getParcelableArrayListExtra("more_outages")

        Log.d(TAG, "===================== 收到資料 =====================")
        Log.d(TAG, "第一筆停水資料 (outage): $outage")
        Log.d(TAG, "更多公告列表 (moreList): $moreList")
        Log.d(TAG, "====================================================")

        val tvTitle  = findViewById<TextView>(R.id.tv_title)
        val tvReason = findViewById<TextView>(R.id.tv_reason)
        val tvTime   = findViewById<TextView>(R.id.tv_time)
        val labelWater     = findViewById<TextView>(R.id.label_water_area)
        val tvWaterArea    = findViewById<TextView>(R.id.tv_water_area)
        val labelPressure  = findViewById<TextView>(R.id.label_pressure_area)
        val tvPressureArea = findViewById<TextView>(R.id.tv_pressure_area)
        val tvAreaEmpty    = findViewById<TextView>(R.id.tv_area_empty)

        // 髮絲線分隔
        val divAfterReason = findViewById<View>(R.id.div_after_reason)
        val divAfterTime   = findViewById<View>(R.id.div_after_time)
        val divAfterWater  = findViewById<View>(R.id.div_after_water)

        sectionMain = findViewById(R.id.section_main)
        btnMore     = findViewById(R.id.btn_more)

        // 確保兩個節點可聚焦（TV）
        sectionMain.isFocusable = true
        sectionMain.isFocusableInTouchMode = true
        btnMore.isFocusable = true
        btnMore.isFocusableInTouchMode = true

        // 明確指定上下導焦目標（再保險一次）
        sectionMain.nextFocusDownId = R.id.btn_more
        btnMore.nextFocusUpId = R.id.section_main

        // 進場先把焦點放到紅框（等 layout 完成後）
        sectionMain.post { sectionMain.requestFocus() }

        // --- 資料繫結 ---
        if (outage != null) {
            tvTitle.text = titleFor(outage)

            val reasonText = if (!outage.reason.isNullOrBlankOrLiteralNull())
                outage.reason!!.trim() else "未提供"
            tvReason.text = reasonText

            val startPretty = shortenTime(outage.start_time)
            val endPretty   = shortenTime(outage.end_time)
            tvTime.text = if (startPretty != "-" || endPretty != "-")
                "$startPretty ~ $endPretty" else "未提供"

            val waterArea = outage.water_outage_areas
                ?.takeUnless { it.isNullOrBlankOrLiteralNull() }?.trim()
            val pressureArea = outage.Buck_area
                ?.takeUnless { it.isNullOrBlankOrLiteralNull() }?.trim()

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

            tvAreaEmpty.visibility =
                if (waterArea == null && pressureArea == null) View.VISIBLE else View.GONE

            // 髮絲線顯示邏輯
            divAfterReason.visibility = View.VISIBLE
            divAfterTime.visibility   = View.VISIBLE
            divAfterWater.visibility  =
                if (waterArea != null && pressureArea != null) View.VISIBLE else View.GONE

        } else {
            tvTitle.text  = "公告"
            tvReason.text = "未提供"
            tvTime.text   = "未提供"
            labelWater.visibility = View.GONE
            tvWaterArea.visibility = View.GONE
            labelPressure.visibility = View.GONE
            tvPressureArea.visibility = View.GONE
            tvAreaEmpty.visibility = View.VISIBLE

            // 無資料時的分隔線：只保留前兩條
            divAfterReason.visibility = View.VISIBLE
            divAfterTime.visibility   = View.VISIBLE
            divAfterWater.visibility  = View.GONE
        }

        // 更多公告按鈕（只有有資料時才可聚焦）
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

        // ====== 核心：在 View 上直接攔鍵，保證上下切換 ======
        sectionMain.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                Log.d(TAG, "DPAD_DOWN on sectionMain, try focus btnMore (enabled=${btnMore.isEnabled}, vis=${btnMore.visibility})")
                if (btnMore.isEnabled && btnMore.visibility == View.VISIBLE) {
                    btnMore.requestFocus()
                    return@setOnKeyListener true
                }
            }
            false
        }
        btnMore.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                Log.d(TAG, "DPAD_UP on btnMore, back to sectionMain")
                sectionMain.requestFocus()
                return@setOnKeyListener true
            }
            false
        }
        // ================================================
    }

    // 進一步保險：在 Activity 層也攔一次（避免被 ScrollView 吃掉）
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val focusedId = currentFocus?.id
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (focusedId == R.id.section_main &&
                    btnMore.isEnabled && btnMore.visibility == View.VISIBLE
                ) {
                    Log.d(TAG, "onKeyDown DOWN: force focus btnMore")
                    btnMore.requestFocus()
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (focusedId == R.id.btn_more) {
                    Log.d(TAG, "onKeyDown UP: back to sectionMain")
                    sectionMain.requestFocus()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}