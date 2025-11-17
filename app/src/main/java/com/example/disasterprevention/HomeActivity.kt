package com.example.disasterprevention

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

        // 置中對齊
        val layoutManager = FlexboxLayoutManager(this).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.CENTER
            alignItems = AlignItems.CENTER
        }
        recyclerView.layoutManager = layoutManager

        adapter = CardAdapter(cardItems)
        recyclerView.adapter = adapter

        //  響應式 padding
        recyclerView.post {
            val screenHeight = resources.displayMetrics.heightPixels
            val screenWidth = resources.displayMetrics.widthPixels
            val paddingHorizontal = (screenWidth * 0.01).toInt()
            val paddingVertical = (screenHeight * 0.12).toInt()
            recyclerView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)

            if (recyclerView.childCount > 0) recyclerView.getChildAt(0)?.requestFocus()
        }

        //  加入卡片
        addWeatherCard()
        addHeavyRainCard()
        addEarthquakeCard()
        addPowerOutageCard()
        addFloodCard()
        addWaterOutageCard()
    }

    /** 天氣 **/
    private fun addWeatherCard() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instance
                val resp = api.getWeatherSummary(location = "大里區", days = 5)

                val hour = resp.currentTime.substringAfter("T").substringBefore(":").toIntOrNull() ?: 12
                val isNight = hour >= 18 || hour < 6
                val todayDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val today = resp.dailySummary.find { it.date == todayDate } ?: resp.dailySummary.firstOrNull()

                if (today != null) {
                    val animRes = WeatherArt.lottieByPrecip(today.precipitationProbability, isNight)
                    val bgDrawable = WeatherArt.backgroundByCondition(today.precipitationProbability, today.weatherIcon, isNight)
                    val subtitle = "最高溫：${today.maxTemperature}°\n最低溫：${today.minTemperature}°\n降雨機率：${today.precipitationProbability}%"

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
                }
            } catch (e: Exception) {
                e.printStackTrace()
                cardItems.add(CardItem(
                    title = "天氣資訊",
                    subtitle = "無法取得天氣資料",
                    backgroundResId = R.drawable.bg_weather_card_day,
                    titleColor = Color.WHITE,
                    subtitleColor = Color.WHITE,
                    iconResId = R.drawable.cloud
                ))
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
                val subtitle = latest?.let { "最新震央：${it.epicenter}\n規模：${it.magnitude}" } ?: "無法取得最新資料"

                val item = CardItem(
                    title = "地震資訊",
                    subtitle = subtitle,
                    backgroundColor = Color.parseColor("#FAEBD7"),
                    titleColor = Color.parseColor("#191970"),
                    subtitleColor = Color.parseColor("#191970"),
                    iconResId = R.drawable.earthquake,
                    onClick = {
                        startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                    }
                )
                cardItems.add(item)
                adapter.notifyItemInserted(cardItems.size - 1)
            } catch (e: Exception) {
                cardItems.add(CardItem(
                    title = "地震資訊",
                    subtitle = "無法取得資料",
                    backgroundColor = Color.parseColor("#FAEBD7"),
                    titleColor = Color.parseColor("#191970"),
                    subtitleColor = Color.parseColor("#191970"),
                    iconResId = R.drawable.earthquake
                ))
                adapter.notifyItemInserted(cardItems.size - 1)
            }
        }
    }

    /** 豪雨特報 **/
    private fun addHeavyRainCard() {
        RetrofitClient.instance.getHeavyRainAlerts()
            .enqueue(object : Callback<Heavy_Rain_Response> {
                override fun onResponse(call: Call<Heavy_Rain_Response>, response: Response<Heavy_Rain_Response>) {
                    val latest = response.body()?.data?.firstOrNull()

                    var cardTitle = "豪雨特報"
                    var cardSubtitle = "目前沒有豪大雨特報"

                    // 無特報預設顏色：深藍色
                    var backgroundColor = Color.parseColor("#4682B4")

                    if (latest != null) {
                        val headline = latest.headline

                        // 豪雨特報（發布）
                        if (headline.contains("特報") && !headline.contains("解除")) {
                            backgroundColor = Color.parseColor("#C62828")
                            val expiresTime = latest.expiresTime.split("T")
                                .getOrNull(1)?.substring(0, 5) ?: ""
                            cardTitle = headline
                            cardSubtitle = "即將到來\n預計時間：$expiresTime"

                            // 豪雨特報（解除）
                        } else if (headline.contains("解除")) {
                            backgroundColor = Color.parseColor("#4CAF50")
                            cardSubtitle = headline
                        }
                    }

                    val item = CardItem(
                        title = cardTitle,
                        subtitle = cardSubtitle,
                        backgroundColor = backgroundColor,
                        titleColor = Color.WHITE,
                        subtitleColor = Color.WHITE,
                        iconResId = R.drawable.heavyrain,
                        onClick = {
                            val intent = Intent(this@HomeActivity, HeavyRainAlertActivity::class.java)
                            if (latest != null)
                                intent.putExtra("heavy_rain_alert_data", latest)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                        }
                    )

                    cardItems.add(item)
                    adapter.notifyItemInserted(cardItems.size - 1)
                }

                override fun onFailure(call: Call<Heavy_Rain_Response>, t: Throwable) {
                    cardItems.add(
                        CardItem(
                            title = "豪雨特報",
                            subtitle = "資料取得失敗",
                            backgroundColor = Color.parseColor("#4682B4"), // 藍色維持
                            titleColor = Color.WHITE,
                            subtitleColor = Color.WHITE,
                            iconResId = R.drawable.heavyrain
                        )
                    )
                    adapter.notifyItemInserted(cardItems.size - 1)
                }
            })
    }

    /** 停電資訊 **/
    private fun addPowerOutageCard() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instance
//                val address = "台中市大里區國光路一段68號"
                val address = "台中市北屯區南興路1050號"


                val response = api.getPowerOutageInfo(address)
                val affectedCount = response.affectedCount ?: 0

                val subtitle: String
                val backgroundColor: Int
                if (affectedCount > 0) {
                    subtitle = "您附近有 $affectedCount 件停電事件"
                    backgroundColor = Color.parseColor("#F3B431")
                } else {
                    subtitle = "您的地區目前無停電"
                    backgroundColor = Color.parseColor("#4C8A64")
                }

                val item = CardItem(
                    title = "停電資訊",
                    subtitle = subtitle,
                    backgroundColor = backgroundColor,
                    titleColor = Color.WHITE,
                    subtitleColor = Color.WHITE,
                    iconResId = R.drawable.poweroutage,
                    onClick = {
                        val intent = Intent(this@HomeActivity, PowerOutageDetailActivity::class.java)
                        intent.putExtra("power_outage_data", response)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                    }
                )
                cardItems.add(item)
                adapter.notifyItemInserted(cardItems.size - 1)
            } catch (e: Exception) {
                e.printStackTrace()
                cardItems.add(CardItem(
                    title = "停電資訊",
                    subtitle = "無法取得資料",
                    backgroundColor = Color.GRAY,
                    titleColor = Color.WHITE,
                    subtitleColor = Color.WHITE,
                    iconResId = R.drawable.poweroutage
                ))
                adapter.notifyItemInserted(cardItems.size - 1)
            }
        }
    }

    /** 淹水警報 **/
    private fun addFloodCard() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.instance
                val response = api.getFloodInfo()
                val alertStationsCount = response.data.count { it.alertStatus != "正常" }
                val hasAlert = alertStationsCount > 0
                val subtitle = if (hasAlert)
                    "有 $alertStationsCount 個測站水位過高"
                else "各地區水位正常"

                val backgroundColor = if (hasAlert)
                    Color.parseColor("#E57373") else Color.parseColor("#66BB6A")

                val item = CardItem(
                    title = "淹水警報",
                    subtitle = subtitle,
                    backgroundColor = backgroundColor,
                    titleColor = Color.WHITE,
                    subtitleColor = Color.WHITE,
                    iconResId = R.drawable.flood,
                    onClick = {
                        val intent = Intent(this@HomeActivity, FloodDetailActivity::class.java)
                        intent.putExtra("flood_data", response)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                    }
                )
                cardItems.add(item)
                adapter.notifyItemInserted(cardItems.size - 1)
            } catch (e: Exception) {
                e.printStackTrace()
                cardItems.add(CardItem(
                    title = "淹水警報",
                    subtitle = "無法取得資料",
                    backgroundColor = Color.GRAY,
                    titleColor = Color.WHITE,
                    subtitleColor = Color.WHITE,
                    iconResId = R.drawable.flood
                ))
                adapter.notifyItemInserted(cardItems.size - 1)
            }
        }
    }

    /** 停水 **/
    private fun addWaterOutageCard() {
        RetrofitClient.instance.getWaterOutages(county = "台中市")
            .enqueue(object : Callback<WaterOutagesResponse> {
                override fun onResponse(call: Call<WaterOutagesResponse>, response: Response<WaterOutagesResponse>) {
                    Log.d("WATER_DEBUG", "Code=${response.code()}, Body=${response.body()}")
                    val all = response.body()?.data ?: emptyList()
                    val first = all.firstOrNull()
                    val rest = if (all.size > 1) all.drop(1) else emptyList()

                    val reason = first?.reason ?: "原因未提供"
                    val subtitle = if (first != null)
                        "原因：$reason\n時間：${first.start_time} ~ ${first.end_time}"
                    else "目前無台中市停水公告"

                    val item = CardItem(
                        title = "停水資訊",
                        subtitle = subtitle,
                        backgroundColor = Color.parseColor("#e0f7fa"),
                        titleColor = Color.parseColor("#003b4a"),
                        subtitleColor = Color.parseColor("#003b4a"),
                        iconResId = R.drawable.wateroutage_playstore,
                        onClick = {
                            val intent = Intent(this@HomeActivity, WaterOutageActivity::class.java)
                            intent.putExtra("first_outage", first)
                            intent.putParcelableArrayListExtra("more_outages", ArrayList(rest))
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                        }
                    )
                    cardItems.add(item)
                    adapter.notifyItemInserted(cardItems.size - 1)
                }

                override fun onFailure(call: Call<WaterOutagesResponse>, t: Throwable) {
                    cardItems.add(CardItem(
                        title = "停水資訊",
                        subtitle = "資料取得失敗",
                        backgroundColor = Color.parseColor("#e0f7fa"),
                        titleColor = Color.parseColor("#003b4a"),
                        subtitleColor = Color.parseColor("#003b4a"),
                        iconResId = R.drawable.wateroutage_playstore
                    ))
                    adapter.notifyItemInserted(cardItems.size - 1)
                }
            })
    }
}
