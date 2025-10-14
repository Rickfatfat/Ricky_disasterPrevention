package com.example.disasterprevention

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainBrowseFragment : Fragment() {

    private lateinit var tvLatestInfo: TextView
    private lateinit var ivLatestImage: ImageView
    private lateinit var btnHistory: Button

    private val historyDataList = mutableListOf<Earthquake>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvLatestInfo = view.findViewById(R.id.tv_latest_info)
        ivLatestImage = view.findViewById(R.id.iv_latest_image)
        btnHistory = view.findViewById(R.id.btn_history)

        // --- 放大縮小動畫效果 ---
        btnHistory.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).start()
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
            }
        }

        btnHistory.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(80).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                }
            }
            false
        }

        // --- 點擊跳轉至歷史資料頁面 ---
        btnHistory.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            intent.putParcelableArrayListExtra("historyList", ArrayList(historyDataList))
            startActivity(intent)
        }
        view.findViewById<View>(R.id.container_latest).requestFocus()
        fetchEarthquakeData()
    }

    private fun fetchEarthquakeData() {
        val apiService = RetrofitClient.instance
        apiService.getEarthquakes(6).enqueue(object : Callback<EarthquakeResponse> {
            override fun onResponse(
                call: Call<EarthquakeResponse>,
                response: Response<EarthquakeResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()?.data ?: emptyList()
                    historyDataList.clear()
                    historyDataList.addAll(apiResponse)

                    val latest = historyDataList.firstOrNull()
                    latest?.let {
                        tvLatestInfo.text =
                            "時間：${it.time}\n地點：${it.epicenter}\n規模：${it.magnitude}\n本地：${it.taichung_intensity}"
                        Glide.with(this@MainBrowseFragment)
                            .load(it.shakemap_url)
                            .into(ivLatestImage)
                    }
                } else {
                    tvLatestInfo.text = "伺服器回傳錯誤"
                }
            }

            override fun onFailure(call: Call<EarthquakeResponse>, t: Throwable) {
                tvLatestInfo.text = "取得資料失敗"
            }
        })
    }
}
