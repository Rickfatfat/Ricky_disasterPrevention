package com.example.disasterprevention

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recycler_cards)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CardAdapter(cardItems)

        // 新增地震卡片
        addEarthquakeCard()

        // 可新增其他功能卡片
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
                    "最新震央：${latest.epicenter}  " + "\n規模：${latest.magnitude}"
                } else "無資料"

                val item = CardItem(
                    title = "地震資訊",
                    subtitle = subtitle,
                    onClick = {
                        startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                    }
                )
                cardItems.add(item)
                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<EarthquakeResponse>, t: Throwable) {
                val item = CardItem(
                    title = "地震資訊",
                    subtitle = "無法取得資料",
                    onClick = {
                        startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                    }
                )
                cardItems.add(item)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        })
    }
}
