package com.example.disasterprevention

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WaterOutageMoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_outage_more)

        // 1. RecyclerView
        val rv = findViewById<RecyclerView>(R.id.rv_more_list)
        rv.layoutManager = LinearLayoutManager(this)

        // 2. 取得上一層傳來的公告列表
        val moreList: ArrayList<WaterOutage>? =
            intent.getParcelableArrayListExtra("more_outages")

        // 3. 防呆：如果 null 就用空陣列
        val listForUI: List<WaterOutage> = moreList ?: emptyList()

        // 4. 設定 Adapter，並把「點到一列的行為」傳進去
        rv.adapter = WaterOutageMoreAdapter(
            listForUI
        ) { outageItem ->
            // 使用者真的按下某一列了 -> 開詳細頁
            val detailIntent = Intent(
                this,
                WaterOutageMoreDetailsActivity::class.java
            )
            detailIntent.putExtra("outage_detail", outageItem)
            startActivity(detailIntent)

            // 保留下頁切換動畫（可選）
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.fade_out
            )
        }
    }
}