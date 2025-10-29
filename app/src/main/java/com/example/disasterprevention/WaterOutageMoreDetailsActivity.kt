package com.example.disasterprevention

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WaterOutageMoreDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_outage_detail)

        val tvTitle  = findViewById<TextView>(R.id.tv_detail_title)
        val tvArea   = findViewById<TextView>(R.id.tv_detail_area)
        val tvTime   = findViewById<TextView>(R.id.tv_detail_time)
        val tvReason = findViewById<TextView>(R.id.tv_detail_reason)

        // 1. 拿到剛剛點的那筆
        val outage: WaterOutage? = intent.getParcelableExtra("outage_detail")

        if (outage == null) {
            tvTitle.text = "停水資訊"
            tvArea.text = "無資料"
            tvTime.text = "無資料"
            tvReason.text = "無資料"
            return
        }

        // --- (舊的邏輯已被刪除) ---

        // <--- START: 複製貼上我們最新的動態邏輯 ---

        // --- A. 處理「影響區域」顯示 ---
        // 1. 取得兩個區域欄位，並過濾掉 "null" 字串
        val areaOutage = outage.water_outage_areas.takeIf { it != null && it != "null" }
        val areaPressure = outage.Buck_area.takeIf { it != null && it != "null" } // <-- 必須用 Buck_area

        // 2. 使用 StringBuilder 組合要顯示的區域文字
        val areaDisplayBuilder = StringBuilder()

        if (areaOutage != null) {
            areaDisplayBuilder.append("停水區域：\n") // 配合你 XML 的排版
            areaDisplayBuilder.append(areaOutage.trim())
        }
        if (areaPressure != null) {
            if (areaDisplayBuilder.isNotEmpty()) areaDisplayBuilder.append("\n\n")
            areaDisplayBuilder.append("降壓區域：\n")
            areaDisplayBuilder.append(areaPressure.trim())
        }

        // 3. 設定 tvArea 的文字
        if (areaDisplayBuilder.isEmpty()) {
            tvArea.text = "影響區域：N/A"
        } else {
            tvArea.text = areaDisplayBuilder.toString()
        }

        // --- B. 處理「動態標題」 ---
        val checkText = outage.reason ?: ""
        val hasOutage = checkText.contains("停水") || checkText.contains("無水")
        val hasPressureDrop = checkText.contains("降壓")

        val titleTypes = mutableListOf<String>()
        if (hasOutage) titleTypes.add("停水")
        if (hasPressureDrop) titleTypes.add("降壓")

        if (titleTypes.isEmpty()) {
            if (areaOutage != null) titleTypes.add("停水")
            if (areaPressure != null) titleTypes.add("降壓")
        }

        if (titleTypes.isEmpty()) {
            tvTitle.text = "最新公告"
        } else {
            tvTitle.text = titleTypes.distinct().joinToString("及") + "資訊"
        }

        // --- C. 處理時間和原因 (保留你原本的格式) ---
        tvTime.text = "影響時間：\n" +
                "${outage.start_time ?: "-"} ~ ${outage.end_time ?: "-"}"

        tvReason.text = "原因：\n" +
                (outage.reason ?: "-")

        // <--- END: 動態邏輯結束 ---
    }

    override fun finish() {
        super.finish()
        // 返回時的反向動畫 (保留)
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.slide_out_right
        )
    }
}