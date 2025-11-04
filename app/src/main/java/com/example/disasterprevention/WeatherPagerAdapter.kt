package com.example.disasterprevention

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class WeatherPagerAdapter(
    private val fa: WeatherDetailActivity,
    private val summaries: List<WeatherSummary>,
    private val isNight: Boolean,
    private val location: String
) : RecyclerView.Adapter<WeatherPagerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: View = view.findViewById(R.id.weather_root)
        val lottie: LottieAnimationView = view.findViewById(R.id.lottieWeather)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val tvLocation: TextView = view.findViewById(R.id.tv_location)
        val tvTemp: TextView = view.findViewById(R.id.tv_temp)
        val tvRain: TextView = view.findViewById(R.id.tv_rain)
        val tvAdvice: TextView = view.findViewById(R.id.tv_advice)
        val ivLeft: ImageView = view.findViewById(R.id.iv_left)
        val ivRight: ImageView = view.findViewById(R.id.iv_right)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather_page, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = summaries[position]

        // 取得背景與
        val bgRes = WeatherArt.backgroundByCondition(
            precip = data.precipitationProbability,
            weatherIcon = data.weatherIcon,
            isNight = isNight
        )
        val animRes = WeatherArt.lottieByPrecip(data.precipitationProbability, isNight)

        // 整體淡出
        val fadeOut = AlphaAnimation(1f, 0f).apply {
            duration = 250
            fillAfter = true
        }

        //  整體淡入
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 600
            startOffset = 200
            fillAfter = true
        }

        // 當舊畫面淡出結束後，更新背景與內容再淡入
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // 更新背景與內容
                holder.root.setBackgroundResource(bgRes)

                holder.tvDate.text = data.date
                holder.tvLocation.text = location
                holder.tvTemp.text = "${data.maxTemperature}° / ${data.minTemperature}°"
                holder.tvRain.text = "降雨機率 ${data.precipitationProbability}%"
                holder.tvAdvice.text = data.advice

                // 更新 Lottie
                holder.lottie.setAnimation(animRes)
                holder.lottie.playAnimation()

                // 淡入所有元素
                holder.root.startAnimation(fadeIn)
                holder.lottie.startAnimation(
                    AnimationUtils.loadAnimation(holder.root.context, R.anim.fade_in)
                )
                fadeText(holder)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        // 開始淡出舊頁
        holder.root.startAnimation(fadeOut)

        // 控制左右箭頭顯示
        holder.ivLeft.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        holder.ivRight.visibility =
            if (position == summaries.lastIndex) View.INVISIBLE else View.VISIBLE
    }

    override fun getItemCount(): Int = summaries.size

    // 讓所有文字同步淡入
    private fun fadeText(holder: ViewHolder) {
        val fadeText = AlphaAnimation(0f, 1f).apply {
            duration = 700
            startOffset = 150
            fillAfter = true
        }
        holder.tvDate.startAnimation(fadeText)
        holder.tvLocation.startAnimation(fadeText)
        holder.tvTemp.startAnimation(fadeText)
        holder.tvRain.startAnimation(fadeText)
        holder.tvAdvice.startAnimation(fadeText)
    }
}
