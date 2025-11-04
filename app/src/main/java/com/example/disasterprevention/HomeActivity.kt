package com.example.disasterprevention

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val cardItems = mutableListOf<CardItem>()
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recycler_cards)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = CardAdapter(cardItems)
        recyclerView.adapter = adapter

        addWeatherCard()
        addEarthquakeCard()
    }

    /** 天氣 **/
    private fun addWeatherCard() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instance
                val resp = api.getWeatherSummary(location = "大里區", days = 5)

                // 用 API 回傳時間判斷白天或夜晚
                val serverTime = resp.currentTime
                val hour = serverTime.substringAfter("T").substringBefore(":").toIntOrNull() ?: 12
                val isNight = hour >= 18 || hour < 6

                // 取出今日資料
                val todayDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val today = resp.dailySummary.find { it.date == todayDate }
                    ?: resp.dailySummary.firstOrNull()

                if (today != null) {
                    // 天氣動畫與背景
                    val animRes = WeatherArt.lottieByPrecip(today.precipitationProbability, isNight)
                    val bgDrawable = WeatherArt.backgroundByCondition(
                        today.precipitationProbability,
                        today.weatherIcon,
                        isNight
                    )

                    val subtitle = "最高溫：${today.maxTemperature}°\n" +
                            "最低溫：${today.minTemperature}°\n" +
                            "降雨機率：${today.precipitationProbability}%"

                    val item = CardItem(
                        title = "今日天氣",
                        subtitle = subtitle,
                        backgroundResId = bgDrawable,
                        titleColor = Color.WHITE,
                        subtitleColor = Color.WHITE,
                        iconLottieResId = animRes,
                        onClick = {
                            val intent = Intent(this@HomeActivity, WeatherDetailActivity::class.java)
                            intent.putExtra("weatherList", ArrayList(resp.dailySummary))
                            intent.putExtra("weatherCurrentTime", resp.currentTime)
                            intent.putExtra("weatherLocation", resp.location)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                        }
                    )
                    cardItems.add(0, item)
                    adapter.notifyItemInserted(0)
                    recyclerView.scrollToPosition(0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val item = CardItem(
                    title = "天氣資訊",
                    subtitle = "無法取得天氣資料",
                    backgroundResId = R.drawable.bg_weather_card_day,
                    titleColor = Color.WHITE,
                    subtitleColor = Color.WHITE,
                    iconResId = R.drawable.cloud
                )
                cardItems.add(0, item)
                adapter.notifyItemInserted(0)
            }
        }
    }

    /** 地震 **/
    private fun addEarthquakeCard() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instance
                val response = api.getEarthquakes(1)
                val latest = response.data.firstOrNull()

                val subtitle = if (latest != null) {
                    "最新震央：${latest.epicenter}\n規模：${latest.magnitude}"
                } else "無資料"

                val item = CardItem(
                    title = "地震資訊",
                    subtitle = subtitle,
                    backgroundResId = R.drawable.bg_card_normal,
                    backgroundTint = Color.parseColor("#FAEBD7"),
                    titleColor = Color.parseColor("#191970"),
                    subtitleColor = Color.parseColor("#191970"),
                    iconResId = R.drawable.earthquake,
                    onClick = {
                        val intent = Intent(this@HomeActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                    }
                )
                cardItems.add(item)
                adapter.notifyItemInserted(cardItems.size - 1)
            } catch (e: Exception) {
                val item = CardItem(
                    title = "地震資訊",
                    subtitle = "無法取得資料",
                    backgroundResId = R.drawable.bg_card_normal,
                    titleColor = Color.parseColor("#191970"),
                    subtitleColor = Color.parseColor("#191970"),
                    iconResId = R.drawable.earthquake
                )
                cardItems.add(item)
                adapter.notifyItemInserted(cardItems.size - 1)
            }
        }
    }
}
