package com.example.disasterprevention

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.disasterprevention.ui.weather.WeatherPagerAdapter
import com.example.disasterprevention.ui.weather.WeatherSummary

class WeatherDetailActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: WeatherPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_detail)

        val summaryList = intent.getSerializableExtra("weatherList") as ArrayList<WeatherSummary>

        adapter = WeatherPagerAdapter(this, summaryList)
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = adapter
    }
}
