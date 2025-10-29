package com.example.disasterprevention

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WaterOutageActivity : AppCompatActivity() {

    private val TAG = "WaterOutageActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_outage)

        // 1. 拿到資料
        val outage: WaterOutage? =
            intent.getParcelableExtra("first_outage")
        val moreList: ArrayList<WaterOutage>? =
            intent.getParcelableArrayListExtra("more_outages")

        // 2.5 Log (保留，每次除錯都看)
        Log.d(TAG, "===================== 收到資料 =====================")
        Log.d(TAG, "第一筆停水資料 (outage): $outage")
        Log.d(TAG, "更多公告列表 (moreList): $moreList")
        Log.d(TAG, "===================================================")

        // 3. 把畫面上的元件找出來
        val tvTitle = findViewById<TextView>(R.id.tv_title)
        val tvArea = findViewById<TextView>(R.id.tv_area)       // 這個 TextView 將用來顯示「區域」
        val tvTime = findViewById<TextView>(R.id.tv_time)       // 這個 TextView 將用來顯示「時間」
        val tvReason = findViewById<TextView>(R.id.tv_reason)   // 這個 TextView 將用來顯示「原因」
        val btnMore = findViewById<Button>(R.id.btn_more)

        // 4. 塞資料進畫面（第一筆）
        if (outage != null) {

            // --- A. 處理「原因」和「時間」(將顯示在畫面上方) ---

            // --- 修改點 1: 加上 "原因：\n" 標題 ---
            tvReason.text = "原因：\n" + (outage.reason ?: "無原因說明")

            // --- 修改點 2: 加上 "影響時間：\n" 標題 ---
            val startStr = outage.start_time ?: "-"
            val endStr = outage.end_time ?: "-"
            tvTime.text = "影響時間：\n$startStr ~ $endStr"

            // --- B. 處理「影響區域」顯示 (將顯示在畫面下方, 這裡的邏輯已經包含標題了, 所以不需修改) ---
            val areaOutage = outage.water_outage_areas.takeIf { it != null && it != "null" }
            val areaPressure = outage.Buck_area.takeIf { it != null && it != "null" }

            val areaDisplayBuilder = StringBuilder()

            if (areaOutage != null) {
                areaDisplayBuilder.append("停水區域：\n")
                areaDisplayBuilder.append(areaOutage.trim())
            }

            if (areaPressure != null) {
                if (areaDisplayBuilder.isNotEmpty()) {
                    areaDisplayBuilder.append("\n\n")
                }
                areaDisplayBuilder.append("降壓區域：\n")
                areaDisplayBuilder.append(areaPressure.trim())
            }

            if (areaDisplayBuilder.isEmpty()) {
                // 由於之前的對調, tvArea 現在用來顯示區域
                tvArea.text = "影響區域：N/A"
            } else {
                tvArea.text = areaDisplayBuilder.toString()
            }


            // --- C. 處理「動態標題」 (邏輯不變) ---
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
                tvTitle.text = titleTypes.joinToString("及") + "資訊"
            }

        } else {
            // 資料為空的情況
            tvTitle.text = "公告"
            tvReason.text = "原因：\n無資料"
            tvTime.text = "影響時間：\n-"
            tvArea.text = "影響區域：\n-"
        }

        // 5. 設定「更多公告」按鈕 (保持不變)
        if (moreList.isNullOrEmpty()) {
            btnMore.isEnabled = false
            btnMore.alpha = 0.3f
        } else {
            btnMore.isEnabled = true
            btnMore.alpha = 1f

            btnMore.setOnClickListener {
                val intent = Intent(
                    this,
                    WaterOutageMoreActivity::class.java
                )
                intent.putParcelableArrayListExtra(
                    "more_outages",
                    moreList
                )
                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.fade_out
                )
            }
        }
    }
}
