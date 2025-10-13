package com.example.disasterprevention

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AnimationUtils
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

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        recyclerView = findViewById(R.id.recycler_cards)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = CardAdapter(cardItems)
        recyclerView.adapter = adapter

        recyclerView.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in)

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
                    onClick = {
                        val intent = Intent(this@HomeActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                    }
                )
                cardItems.add(item)
                adapter.notifyDataSetChanged()
                recyclerView.scheduleLayoutAnimation()
            }

            override fun onFailure(call: Call<EarthquakeResponse>, t: Throwable) {
                val item = CardItem(
                    title = "地震資訊",
                    subtitle = "無法取得資料",
                    backgroundColor = Color.parseColor("#faebd7"),
                    titleColor = Color.parseColor("#191970"),
                    subtitleColor = Color.parseColor("#191970"),
                    onClick = {
                        val intent = Intent(this@HomeActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                    }
                )
                cardItems.add(item)
                adapter.notifyDataSetChanged()
                recyclerView.scheduleLayoutAnimation()
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
            onClick = {
                // 先留空
            }
        )
        cardItems.add(item)
        adapter.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }
}
