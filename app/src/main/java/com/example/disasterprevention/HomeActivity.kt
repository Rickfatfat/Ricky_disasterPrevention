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
        addHeavyRainCard()
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

    /** 豪雨特報 **/
    private fun addHeavyRainCard() {
        // --- 模擬模式開關 ---
        val mockStatus = 0 // 0 = 真實API, 1~3 = 模擬情境

        if (mockStatus > 0) {
            println("🌧️ Running in Mock Mode: $mockStatus")

            val fakeAlert: Heavy_Rain_Alert? = when (mockStatus) {
                1 -> Heavy_Rain_Alert(
                    headline = "豪雨特報",
                    description = "受強烈對流雲系發展影響，今日臺中市有局部大雨或豪雨發生的機率。",
                    effectiveTime = "2025-10-30T14:30:00+08:00",
                    expiresTime = "2025-10-30T20:00:00+08:00",
                    severity = "Severe",
                    areaDesc = "臺中市",
                    urgency = "Immediate"
                )
                2 -> Heavy_Rain_Alert(
                    headline = "解除大雨特報",
                    description = "降雨趨緩，解除大雨特報。",
                    effectiveTime = "2025-10-30T13:00:00+08:00",
                    expiresTime = "2025-10-30T14:00:00+08:00",
                    severity = "Minor",
                    areaDesc = "臺中市",
                    urgency = "Past"
                )
                else -> null
            }

            var cardTitle = "豪雨特報"
            var cardSubtitle = "目前沒有豪大雨特報"
            var alertStatus = 0

            if (fakeAlert != null) {
                val headline = fakeAlert.headline
                if (headline.contains("特報") && !headline.contains("解除")) {
                    alertStatus = 1
                    cardTitle = headline
                    val expiresTime = fakeAlert.expiresTime.split("T").getOrNull(1)?.substring(0, 5) ?: ""
                    cardSubtitle = "即將到來\n預計時間：$expiresTime"
                } else if (headline.contains("解除")) {
                    alertStatus = 2
                    cardTitle = "豪雨特報"
                    cardSubtitle = headline
                }
            }

            val item = CardItem(
                title = cardTitle,
                subtitle = cardSubtitle,
                backgroundColor = when (alertStatus) {
                    1 -> Color.parseColor("#cc5f5a") // 生效中
                    2 -> Color.parseColor("#678f74") // 已解除
                    else -> Color.parseColor("#004B97") // 無特報
                },
                titleColor = Color.WHITE,
                subtitleColor = Color.WHITE,
                iconResId = R.drawable.heavyrain,
                onClick = {
                    val intent = Intent(this@HomeActivity, HeavyRainAlertActivity::class.java)
                    if (alertStatus > 0) {
                        intent.putExtra("heavy_rain_alert_data", fakeAlert)
                    }
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                }
            )
            cardItems.add(item)
            adapter.notifyItemInserted(cardItems.size - 1)
            return
        }

        // --- 真實 API ---
        RetrofitClient.instance.getHeavyRainAlerts()
            .enqueue(object : retrofit2.Callback<Heavy_Rain_Response> {
                override fun onResponse(
                    call: retrofit2.Call<Heavy_Rain_Response>,
                    response: retrofit2.Response<Heavy_Rain_Response>
                ) {
                    val latestAlert = response.body()?.data?.firstOrNull()
                    var cardTitle = "豪雨特報"
                    var cardSubtitle = "目前沒有豪大雨特報"
                    var alertStatus = 0

                    if (latestAlert != null) {
                        val headline = latestAlert.headline
                        if (headline.contains("特報") && !headline.contains("解除")) {
                            alertStatus = 1
                            cardTitle = headline
                            val expiresTime = latestAlert.expiresTime.split("T").getOrNull(1)?.substring(0, 5) ?: ""
                            cardSubtitle = "即將到來\n預計時間：$expiresTime"
                        } else if (headline.contains("解除")) {
                            alertStatus = 2
                            cardSubtitle = headline
                        }
                    }

                    val item = CardItem(
                        title = cardTitle,
                        subtitle = cardSubtitle,
                        backgroundColor = Color.parseColor("#4682B4"),
                        titleColor = Color.WHITE,
                        subtitleColor = Color.WHITE,
                        iconResId = R.drawable.heavyrain,
                        onClick = {
                            val intent = Intent(this@HomeActivity, HeavyRainAlertActivity::class.java)
                            if (alertStatus > 0 && latestAlert != null) {
                                intent.putExtra("heavy_rain_alert_data", latestAlert)
                            }
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                        }
                    )
                    cardItems.add(item)
                    adapter.notifyItemInserted(cardItems.size - 1)
                }

                override fun onFailure(call: retrofit2.Call<Heavy_Rain_Response>, t: Throwable) {
                    val item = CardItem(
                        title = "豪雨特報",
                        subtitle = "資料取得失敗",
                        backgroundColor = Color.parseColor("#4682B4"),
                        titleColor = Color.WHITE,
                        subtitleColor = Color.WHITE,
                        iconResId = R.drawable.heavyrain,
                        onClick = {
                            val intent = Intent(this@HomeActivity, HeavyRainAlertActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                        }
                    )
                    cardItems.add(item)
                    adapter.notifyItemInserted(cardItems.size - 1)
                }
            })
    }

}
