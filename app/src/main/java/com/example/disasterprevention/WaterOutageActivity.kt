package com.example.disasterprevention

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WaterOutageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_outage) // 這要對應你第二層的 XML

        // 1. 拿到第一筆資料（主卡片要顯示的）
        val outage: WaterOutage? =
            intent.getParcelableExtra("first_outage")

        // 2. 拿到「更多公告」列表，準備傳到第三層
        val moreList: ArrayList<WaterOutage>? =
            intent.getParcelableArrayListExtra("more_outages")

        // 3. 把畫面上的元件找出來
        val tvArea = findViewById<TextView>(R.id.tv_area)
        val tvTime = findViewById<TextView>(R.id.tv_time)
        val tvReason = findViewById<TextView>(R.id.tv_reason)
        val btnMore = findViewById<Button>(R.id.btn_more)

        // 4. 塞資料進畫面（第一筆）
        if (outage != null) {
            tvArea.text = outage.water_outage_areas ?: "無資料"
            val startStr = outage.start_time ?: "?"
            val endStr = outage.end_time ?: "?"
            tvTime.text = "$startStr ~ $endStr"
            tvReason.text = outage.reason ?: "無原因說明"
        } else {
            tvArea.text = "無資料"
            tvTime.text = "-"
            tvReason.text = "-"
        }

        // 5. 設定「更多公告」按鈕
        if (moreList.isNullOrEmpty()) {
            // 沒資料就停用按鈕
            btnMore.isEnabled = false
            btnMore.alpha = 0.3f
        } else {
            btnMore.isEnabled = true
            btnMore.alpha = 1f

            btnMore.setOnClickListener {
                // 這裡一定要啟動 WaterOutageMoreActivity 不是別的
                val intent = Intent(
                    this,
                    WaterOutageMoreActivity::class.java
                )

                // 把整份列表往第三層丟
                intent.putParcelableArrayListExtra(
                    "more_outages",
                    moreList
                )

                startActivity(intent)

                // 動畫可留可拿掉
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.fade_out
                )
            }
        }
    }
}
