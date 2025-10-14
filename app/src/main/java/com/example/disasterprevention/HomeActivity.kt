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
}
