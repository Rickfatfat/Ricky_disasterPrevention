package com.example.disasterprevention.ui.weather

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WeatherPagerAdapter(
    fa: FragmentActivity,
    private val summaries: List<WeatherSummary>
) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = summaries.size

    override fun createFragment(position: Int): Fragment {
        return WeatherDayFragment.newInstance(summaries[position])
    }
}
