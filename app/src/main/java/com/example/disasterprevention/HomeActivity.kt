package com.example.disasterprevention

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val cardItems = mutableListOf<CardItem>()
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recycler_cards)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = CardAdapter(cardItems)
        recyclerView.adapter = adapter

        addEarthquakeCard()
        addLandslideCard()
        addWaterOutageCard()
    }

    private fun addEarthquakeCard() {
        val api = RetrofitClient.instance
        api.getEarthquakes(1).enqueue(object : Callback<EarthquakeResponse> {
            override fun onResponse(
                call: Call<EarthquakeResponse>,
                response: Response<EarthquakeResponse>
            ) {
                val latest = response.body()?.data?.firstOrNull()
                val subtitle = if (latest != null) {
                    "最新震央：${latest.epicenter}\n規模：${latest.magnitude}"
                } else "無資料"

                val item = CardItem(
                    title = "地震資訊",
                    subtitle = subtitle,
                    backgroundColor = Color.parseColor("#faebd7"),
                    titleColor = Color.parseColor("#191970"),
                    subtitleColor = Color.parseColor("#191970"),
                    iconResId = R.drawable.earthquake,
                    onClick = {
                        val intent = Intent(this@HomeActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                    }
                )

                cardItems.add(0, item)
                adapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)
            }

            override fun onFailure(call: Call<EarthquakeResponse>, t: Throwable) {
                val item = CardItem(
                    title = "地震資訊",
                    subtitle = "無法取得資料",
                    backgroundColor = Color.parseColor("#faebd7"),
                    titleColor = Color.parseColor("#191970"),
                    subtitleColor = Color.parseColor("#191970"),
                    iconResId = R.drawable.earthquake,
                            onClick = {
                        val intent = Intent(this@HomeActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                    }
                )

                cardItems.add(0, item)
                adapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)
            }
        })
    }

    private fun addLandslideCard() {
        val item = CardItem(
            title = "土石流資訊",
            subtitle = "",
            backgroundColor = Color.parseColor("#f5deb3"),
            titleColor = Color.parseColor("#4d1f00"),
            subtitleColor = Color.parseColor("#4d1f00"),
            iconResId = R.drawable.landslide,
            onClick = {
                // 先留空
            }
        )

        cardItems.add(item)
        adapter.notifyItemInserted(cardItems.size - 1)
    }
    // 卡3：停水資訊
    // 卡3：停水資訊
    // 卡3：停水資訊
    private fun addWaterOutageCard() {
        RetrofitClient.instance
            .getWaterOutages(county = "台中市")
            .enqueue(object : Callback<WaterOutagesResponse> {
                override fun onResponse(
                    call: Call<WaterOutagesResponse>,
                    response: Response<WaterOutagesResponse>
                ) {
                    val all: List<WaterOutage> = response.body()?.data ?: emptyList()
                    val first: WaterOutage? = all.firstOrNull()
                    val rest: List<WaterOutage> = if (all.size > 1) all.drop(1) else emptyList()

                    // ----- 格式化 for 卡片兩行摘要 (已修改) -----

                    // 1. 濃縮原因的函式
                    fun summarizeReason(fullReason: String?): String {
                        if (fullReason.isNullOrBlank()) {
                            return "原因未提供"
                        }

                        // 定義關鍵字和對應的簡短描述 (按優先級排序)
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

                        // 遍歷關鍵字列表，找到第一個匹配的關鍵字
                        for ((keyword, summary) in keywordMap) {
                            if (fullReason.contains(keyword)) {
                                return summary // 找到就回傳簡短描述
                            }
                        }

                        // 如果都沒找到，回傳原始原因的第一句話
                        return fullReason.split("，", "。", "、", " ").firstOrNull() ?: fullReason
                    }


                    // 2. 格式化時間的函式 (保留不動)
                    fun shortenTime(raw: String?): String {
                        if (raw.isNullOrBlank()) return "-"
                        val parts = raw.split(" ")
                        if (parts.size < 2) return raw
                        val datePart = parts[0] // e.g. 2025-10-08
                        val timePart = parts[1] // e.g. 10:14:00
                        val dateTokens = datePart.split("-")
                        val mmdd = if (dateTokens.size == 3) {
                            "${dateTokens[1]}/${dateTokens[2]}" // 10/08
                        } else {
                            datePart
                        }
                        val hhmm = timePart.substring(0, 5) // "10:14"
                        return "$mmdd $hhmm"
                    }

                    // 3. 取得濃縮後的原因和格式化後的時間
                    val reason = summarizeReason(first?.reason)
                    val startPretty = shortenTime(first?.start_time)
                    val endPretty   = shortenTime(first?.end_time)

                    // 4. 組合新的副標題文字
                    val subtitle = if (first != null) {
                        "原因：$reason\n時間：$startPretty ~ $endPretty"
                    } else {
                        "目前無台中市停水公告"
                    }

                    val firstOutageForClick = first
                    val restOutagesForClick = ArrayList(rest)

                    val item = CardItem(
                        title = "停水資訊",
                        subtitle = subtitle, // 使用新的副標題
                        backgroundColor = Color.parseColor("#e0f7fa"),
                        titleColor = Color.parseColor("#003b4a"),
                        subtitleColor = Color.parseColor("#003b4a"),
                        iconResId = R.drawable.wateroutage,
                        onClick = {
                            val intent = Intent(this@HomeActivity, WaterOutageActivity::class.java)
                            intent.putExtra("first_outage", firstOutageForClick)
                            intent.putParcelableArrayListExtra("more_outages", restOutagesForClick)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.fade_out
                            )
                        }
                    )

                    cardItems.add(item)
                    adapter.notifyItemInserted(cardItems.size - 1)
                }

                override fun onFailure(
                    call: Call<WaterOutagesResponse>,
                    t: Throwable
                ) {
                    val item = CardItem(
                        title = "停水資訊",
                        subtitle = "資料取得失敗",
                        backgroundColor = Color.parseColor("#e0f7fa"),
                        titleColor = Color.parseColor("#003b4a"),
                        subtitleColor = Color.parseColor("#003b4a"),
                        iconResId = R.drawable.wateroutage,
                        onClick = {
                            val intent = Intent(this@HomeActivity, WaterOutageActivity::class.java)
                            intent.putExtra("first_outage", null as WaterOutage?)
                            intent.putParcelableArrayListExtra("more_outages", ArrayList())
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.fade_out
                            )
                        }
                    )

                    cardItems.add(item)
                    adapter.notifyItemInserted(cardItems.size - 1)
                }
            })
    }

}
