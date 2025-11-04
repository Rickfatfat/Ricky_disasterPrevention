package com.example.disasterprevention

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.launch

class HeavyRainAlertActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heavy_rain_alert)

        // 1️⃣ 取得從 HomeActivity 傳來的資料
        val alertData: Heavy_Rain_Alert? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("heavy_rain_alert_data", Heavy_Rain_Alert::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra("heavy_rain_alert_data")
            }

        // 2️⃣ 綁定元件
        val layoutAlertDetails = findViewById<CardView>(R.id.layout_alert_details)
        val layoutNoAlert = findViewById<LinearLayout>(R.id.layout_no_alert)
        val btnToWeather = findViewById<Button>(R.id.btn_to_weather)
        val btnToWeatherAlert = findViewById<Button>(R.id.btn_to_weather_alert)
        val lottieView = findViewById<LottieAnimationView>(R.id.lottie_heavy_rain)

        // 3️⃣ 動態建立半透明載入框
        val loadingOverlay = FrameLayout(this).apply {
            setBackgroundColor(0x88000000.toInt()) // 半透明黑背景
            visibility = View.GONE
            isClickable = true
            foregroundGravity = Gravity.CENTER
        }

        val loadingCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(60, 60, 60, 60)
            background = resources.getDrawable(R.drawable.bg_loading_card, theme)
        }

        val progress = ProgressBar(this).apply { isIndeterminate = true }
        val loadingText = TextView(this).apply {
            text = "載入中..."
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 0)
        }

        loadingCard.addView(progress)
        loadingCard.addView(loadingText)
        loadingOverlay.addView(loadingCard)
        addContentView(
            loadingOverlay,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        // 4️⃣ 根據資料狀態顯示畫面
        when {
            alertData == null -> showGoodWeatherInfo(layoutAlertDetails, layoutNoAlert, lottieView)
            alertData.headline.contains("特報") && !alertData.headline.contains("解除") ->
                showActiveAlertDetails(alertData, layoutAlertDetails, layoutNoAlert, lottieView)
            else ->
                showCancelledAlertInfo(alertData, layoutAlertDetails, layoutNoAlert, lottieView)
        }

        // 5️⃣ 定義共用的跳轉動作
        val goWeatherPage: (View) -> Unit = {
            btnToWeather.isEnabled = false
            btnToWeatherAlert?.isEnabled = false
            loadingOverlay.visibility = View.VISIBLE

            lifecycleScope.launch {
                try {
                    val api = RetrofitClient.instance
                    val resp = api.getWeatherSummary(location = "大里區", days = 5)

                    val intent = Intent(this@HeavyRainAlertActivity, WeatherDetailActivity::class.java)
                    intent.putExtra("weatherList", ArrayList(resp.dailySummary))
                    intent.putExtra("weatherCurrentTime", resp.currentTime)
                    intent.putExtra("weatherLocation", resp.location)

                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@HeavyRainAlertActivity,
                        "無法載入天氣資料，請稍後再試",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    loadingOverlay.visibility = View.GONE
                    btnToWeather.isEnabled = true
                    btnToWeatherAlert?.isEnabled = true
                }
            }
        }

        // 6️⃣ 套用共用動作到兩個按鈕
        btnToWeather.setOnClickListener(goWeatherPage)
        btnToWeatherAlert?.setOnClickListener(goWeatherPage)
    }

    /** 🌧️ 生效中警報 → 暴雨動畫 */
    private fun showActiveAlertDetails(
        alertData: Heavy_Rain_Alert,
        layoutAlertDetails: CardView,
        layoutNoAlert: LinearLayout,
        lottie: LottieAnimationView
    ) {
        lottie.setAnimation(R.raw.storm)
        lottie.playAnimation()

        layoutAlertDetails.visibility = View.VISIBLE
        layoutNoAlert.visibility = View.GONE

        findViewById<TextView>(R.id.tv_alert_description).text =
            "資訊概要：${alertData.description}"
        findViewById<TextView>(R.id.tv_alert_effective).text =
            "預計開始時間：${alertData.effectiveTime}"
        findViewById<TextView>(R.id.tv_alert_expires).text =
            "預計結束時間：${alertData.expiresTime}"
        findViewById<TextView>(R.id.tv_alert_area).text =
            "影響區域：${alertData.areaDesc}"
        title = alertData.headline
    }

    /** 💨 已解除特報 → windy 動畫 */
    private fun showCancelledAlertInfo(
        alertData: Heavy_Rain_Alert,
        layoutAlertDetails: CardView,
        layoutNoAlert: LinearLayout,
        lottie: LottieAnimationView
    ) {
        lottie.setAnimation(R.raw.windy)
        lottie.playAnimation()

        layoutAlertDetails.visibility = View.GONE
        layoutNoAlert.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_no_alert_message).text = alertData.description
        title = alertData.headline
    }

    /** ☀️ 好天氣 → sunny 動畫 */
    private fun showGoodWeatherInfo(
        layoutAlertDetails: CardView,
        layoutNoAlert: LinearLayout,
        lottie: LottieAnimationView
    ) {
        lottie.setAnimation(R.raw.sunny)
        lottie.playAnimation()

        layoutAlertDetails.visibility = View.GONE
        layoutNoAlert.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_no_alert_message).text = "今天是個好天氣 ☀️"
        title = "天氣資訊"
    }
}
