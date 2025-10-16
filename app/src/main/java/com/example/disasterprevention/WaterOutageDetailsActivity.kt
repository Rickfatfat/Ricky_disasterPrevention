package com.example.disasterprevention

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class WaterOutageDetailsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 不使用自訂 layout，直接用系統提供的 content 容器
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, WaterOutageDetailsFragment())
                .commitNow()
        }
    }

    companion object {
        // 啟動詳情頁時用這個 key 放入 WaterOutage（Parcelable）
        const val EXTRA_WATER_OUTAGE = "extra_water_outage"
    }
}
