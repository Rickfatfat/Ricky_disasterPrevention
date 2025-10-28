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

        // 拿到剛剛點的那筆
        val outage: WaterOutage? = intent.getParcelableExtra("outage_detail")

        if (outage == null) {
            tvTitle.text = "停水資訊"
            tvArea.text = "無資料"
            tvTime.text = "無資料"
            tvReason.text = "無資料"
            return
        }

        tvTitle.text = "停水資訊"

        tvArea.text = "影響區域：\n" +
                (outage.water_outage_areas ?: "-")

        tvTime.text = "停水時間：\n" +
                "${outage.start_time ?: "-"} ~ ${outage.end_time ?: "-"}"

        tvReason.text = "原因：\n" +
                (outage.reason ?: "-")
    }

    override fun finish() {
        super.finish()
        // 返回時的反向動畫
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.slide_out_right
        )
    }
}
