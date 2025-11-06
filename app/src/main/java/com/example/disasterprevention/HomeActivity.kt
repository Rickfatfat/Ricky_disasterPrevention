package com.example.disasterprevention

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.util.Log
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.flexbox.AlignItems


class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val cardItems = mutableListOf<CardItem>()
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recycler_cards)

        //  FlexboxLayoutManager：每列三張、置中對齊
        val layoutManager = FlexboxLayoutManager(this).apply {
            flexDirection = FlexDirection.ROW          // 橫向排列
            flexWrap = FlexWrap.WRAP                   // 超過三張換行
            justifyContent = JustifyContent.CENTER     // 水平置中
            alignItems = AlignItems.CENTER             // 垂直置中
        }
        recyclerView.layoutManager = layoutManager


        adapter = CardAdapter(cardItems)
        recyclerView.adapter = adapter

        // 動態 padding（依螢幕大小）
        recyclerView.post {
            val screenHeight = resources.displayMetrics.heightPixels
            val screenWidth = resources.displayMetrics.widthPixels
            val paddingHorizontal = (screenWidth * 0.01).toInt()
            val paddingVertical = (screenHeight * 0.12).toInt()
            recyclerView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)

            //  預設聚焦第一張
            if (recyclerView.childCount > 0) {
                recyclerView.getChildAt(0)?.requestFocus()
            }
        }

        // 加入卡片
        addWeatherCard()
        addHeavyRainCard()
        addEarthquakeCard()
        addWaterOutageCard()
        addEarthquakeCard()
        addWaterOutageCard()
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
                } else "無法取得最新資料"

                val item = CardItem(
                    title = "地震資訊",
                    subtitle = subtitle,
                    backgroundColor = Color.parseColor("#FAEBD7"),
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
                    backgroundColor = Color.parseColor("#FAEBD7"),
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

    /** 停水 **/
    private fun addWaterOutageCard() {
        RetrofitClient.instance
            .getWaterOutages(county = "台中市")
            .enqueue(object : Callback<WaterOutagesResponse> {
                override fun onResponse(
                    call: Call<WaterOutagesResponse>,
                    response: Response<WaterOutagesResponse>
                ) {
                    Log.d("WATER_DEBUG", "======== 停水 API 測試 ========")
                    Log.d("WATER_DEBUG", "URL = ${response.raw().request.url}")
                    Log.d("WATER_DEBUG", "Code = ${response.code()}")
                    Log.d("WATER_DEBUG", "Body = ${response.body()}")
                    Log.d("WATER_DEBUG", "=============================")
                    val all: List<WaterOutage> = response.body()?.data ?: emptyList()
                    val first: WaterOutage? = all.firstOrNull()
                    val rest: List<WaterOutage> = if (all.size > 1) all.drop(1) else emptyList()

                    // --- 濃縮原因 ---
                    fun summarizeReason(fullReason: String?): String {
                        if (fullReason.isNullOrBlank()) return "原因未提供"
                        val keywordMap = linkedMapOf(
                            "施工" to "管線施工",
                            "工程" to "工程施工",
                            "維修" to "管線維修",
                            "搶修" to "緊急搶修",
                            "修復" to "設備修復",
                            "汰換" to "設備汰換",
                            "改接" to "管線改接",
                            "清洗" to "水池清洗",
                            "新裝" to "新裝工程",
                            "停電" to "配合停電"
                        )
                        for ((keyword, summary) in keywordMap) {
                            if (fullReason.contains(keyword)) return summary
                        }
                        return fullReason.split("，", "。", "、", " ").firstOrNull() ?: fullReason
                    }

                    fun shortenTime(raw: String?): String {
                        if (raw.isNullOrBlank()) return "-"
                        val parts = raw.split(" ")
                        if (parts.size < 2) return raw
                        val datePart = parts[0]
                        val timePart = parts[1]
                        val dateTokens = datePart.split("-")
                        val mmdd = if (dateTokens.size == 3) "${dateTokens[1]}/${dateTokens[2]}" else datePart
                        val hhmm = timePart.substring(0, 5)
                        return "$mmdd $hhmm"
                    }

                    val reason = summarizeReason(first?.reason)
                    val startPretty = shortenTime(first?.start_time)
                    val endPretty = shortenTime(first?.end_time)
                    val subtitle = if (first != null) {
                        "原因：$reason\n時間：$startPretty ~ $endPretty"
                    } else "目前無台中市停水公告"

                    val firstOutageForClick = first
                    val restOutagesForClick = ArrayList(rest)

                    val item = CardItem(
                        title = "停水資訊",
                        subtitle = subtitle,
                        backgroundColor = Color.parseColor("#e0f7fa"),
                        titleColor = Color.parseColor("#003b4a"),
                        subtitleColor = Color.parseColor("#003b4a"),
                        iconResId = R.drawable.wateroutage_playstore,
                        onClick = {
                            val intent = Intent(this@HomeActivity, WaterOutageActivity::class.java)
                            intent.putExtra("first_outage", firstOutageForClick)
                            intent.putParcelableArrayListExtra("more_outages", restOutagesForClick)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                        }
                    )

                    cardItems.add(item)
                    adapter.notifyItemInserted(cardItems.size - 1)
                }

                override fun onFailure(call: Call<WaterOutagesResponse>, t: Throwable) {
                    val item = CardItem(
                        title = "停水資訊",
                        subtitle = "資料取得失敗",
                        backgroundColor = Color.parseColor("#e0f7fa"),
                        titleColor = Color.parseColor("#003b4a"),
                        subtitleColor = Color.parseColor("#003b4a"),
                        iconResId = R.drawable.wateroutage_playstore
                    )
                    cardItems.add(item)
                    adapter.notifyItemInserted(cardItems.size - 1)
                }
            })
    }

}
