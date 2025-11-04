package com.example.disasterprevention

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2

class WeatherDetailActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: WeatherPagerAdapter
    private lateinit var bgColors: List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_detail)

        // 從 Intent 取得資料
        val summaryList =
            intent.getSerializableExtra("weatherList") as? ArrayList<WeatherSummary> ?: arrayListOf()
        val currentTime = intent.getStringExtra("weatherCurrentTime") ?: ""
        val location = intent.getStringExtra("weatherLocation") ?: "天氣預報"

        // 判斷白天/夜晚
        val hour = currentTime.substringAfter("T").substringBefore(":").toIntOrNull() ?: 12
        val isNight = (hour >= 18 || hour < 6)

        viewPager = findViewById(R.id.viewPager)
        adapter = WeatherPagerAdapter(
            fa = this,
            summaries = summaryList,
            isNight = isNight,
            location = location
        )
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1

        //  取每一頁的背景顏色（根據天氣狀況）
        bgColors = summaryList.map { data ->
            val bgRes = WeatherArt.backgroundByCondition(
                precip = data.precipitationProbability,
                weatherIcon = data.weatherIcon,
                isNight = isNight
            )
            ContextCompat.getColor(this, bgResColor(bgRes))
        }

        //  設定滑動時背景顏色漸變動畫
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private val argbEvaluator = ArgbEvaluator()

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position < bgColors.size - 1 && position < bgColors.lastIndex) {
                    val colorUpdate = argbEvaluator.evaluate(
                        positionOffset,
                        bgColors[position],
                        bgColors[position + 1]
                    ) as Int
                    viewPager.setBackgroundColor(colorUpdate)
                } else {
                    viewPager.setBackgroundColor(bgColors.last())
                }
            }
        })

        //  預設顯示今日頁面
        val today = java.time.LocalDate.now().toString()
        val todayIndex = summaryList.indexOfFirst { it.date == today }
        if (todayIndex >= 0) viewPager.setCurrentItem(todayIndex, false)
    }

    /**  遙控器左右鍵切換 **/
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                val prev = viewPager.currentItem - 1
                if (prev >= 0) viewPager.currentItem = prev
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                val next = viewPager.currentItem + 1
                if (next < (viewPager.adapter?.itemCount ?: 0))
                    viewPager.currentItem = next
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    /** 將 drawable 轉換成顏色資源（用於漸變） **/
    private fun bgResColor(@androidx.annotation.DrawableRes res: Int): Int {
        return when (res) {
            R.drawable.bg_sunny -> R.color.bgsunnycolor
            R.drawable.bg_cloudy -> R.color.bgcloudycolor
            R.drawable.bg_rain -> R.color.bgraincolor
            R.drawable.bg_night -> R.color.bgnightcolor
            else -> R.color.background_black
        }
    }
}
