package com.example.disasterprevention

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class LatestDetailActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvEpicenter: TextView
    private lateinit var tvMagnitude: TextView
    private lateinit var tvIntensity: TextView
    private lateinit var ivShakemap: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_detail)

        // 取得 UI 元素
        tvTitle = findViewById(R.id.tv_title)
        tvTime = findViewById(R.id.tv_time)
        tvEpicenter = findViewById(R.id.tv_epicenter)
        tvMagnitude = findViewById(R.id.tv_magnitude)
        tvIntensity = findViewById(R.id.tv_intensity)
        ivShakemap = findViewById(R.id.iv_shakemap)

        // 取得 Intent 資料
        val title = intent.getStringExtra("TITLE") ?: "地震資訊"
        val time = intent.getStringExtra("TIME") ?: ""
        val epicenter = intent.getStringExtra("EPICENTER") ?: ""
        val magnitude = intent.getStringExtra("MAGNITUDE") ?: ""
        val intensity = intent.getStringExtra("INTENSITY") ?: ""
        val shakemapUrl = intent.getStringExtra("SHAKEMAP_URL") ?: ""

        // 填入 UI
        tvTitle.text = title
        tvTime.text = "時間：$time"
        tvEpicenter.text = "地點：$epicenter"
        tvMagnitude.text = "規模：$magnitude"
        tvIntensity.text = "本地：$intensity"

        // 載入圖片
        Glide.with(this)
            .load(shakemapUrl)
            .placeholder(R.drawable.default_background) // 可選佔位圖
            .into(ivShakemap)
    }
}
