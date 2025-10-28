package com.example.disasterprevention.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.example.disasterprevention.R

class WeatherDayFragment : Fragment() {

    companion object {
        private const val ARG_SUMMARY = "summary"

        fun newInstance(summary: WeatherSummary): WeatherDayFragment {
            val fragment = WeatherDayFragment()
            val args = Bundle()
            args.putSerializable(ARG_SUMMARY, summary)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var summary: WeatherSummary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        summary = arguments?.getSerializable(ARG_SUMMARY) as WeatherSummary
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather_day, container, false)

        val lottie = view.findViewById<LottieAnimationView>(R.id.lottieWeather)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvTemp = view.findViewById<TextView>(R.id.tvTemp)
        val tvRain = view.findViewById<TextView>(R.id.tvRain)
        val tvAdvice = view.findViewById<TextView>(R.id.tvAdvice)

        tvDate.text = summary.date
        tvTemp.text = "${summary.maxTemperature}° / ${summary.minTemperature}°"
        tvRain.text = "降雨機率 ${summary.precipitationProbability}%"
        tvAdvice.text = summary.advice

        val animRes = when (summary.weatherIcon) {
            "☀️" -> R.raw.sunny
            "🌧️" -> R.raw.rainy
            "⛅" -> R.raw.cloudy
            else -> R.raw.sunny
        }
        lottie.setAnimation(animRes)
        lottie.playAnimation()

        return view
    }
}
