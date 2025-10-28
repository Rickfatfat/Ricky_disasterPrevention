package com.example.disasterprevention

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.disasterprevention.R


class WaterOutageMoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 這要對到 "第三層" 的版面
        setContentView(R.layout.activity_water_outage_more)

        // 這個 TextView 一定要在 activity_water_outage_more.xml 裡面存在
        val tvList = findViewById<TextView>(R.id.tv_more_list)

        // 從 Intent 拿到第二層傳來的列表
        val moreList: ArrayList<WaterOutage>? =
            intent.getParcelableArrayListExtra("more_outages")

        if (moreList.isNullOrEmpty()) {
            tvList.text = "無其他公告"
            return
        }

        // 把每則公告整理成可讀文字
        val builder = StringBuilder()
        moreList.forEachIndexed { idx, o ->
            builder.appendLine("【公告 ${idx + 1}】")
            builder.appendLine("區域：${o.water_outage_areas ?: "無資料"}")
            builder.appendLine("時間：${o.start_time ?: "?"} ~ ${o.end_time ?: "?"}")
            builder.appendLine("原因：${o.reason ?: "無資料"}")
            builder.appendLine()
        }

        tvList.text = builder.toString()
    }
}
