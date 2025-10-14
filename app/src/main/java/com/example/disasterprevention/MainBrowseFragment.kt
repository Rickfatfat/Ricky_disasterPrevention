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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainBrowseFragment : Fragment() {

    // ===== 既有：地震區塊 =====
    private lateinit var tvLatestInfo: TextView
    private lateinit var ivLatestImage: ImageView
    private lateinit var btnHistory: Button
    private val historyDataList = mutableListOf<Earthquake>()

    // ===== 新增：停水 / 降壓 區塊 =====
    private lateinit var tvWaterHeader: TextView
    private lateinit var rvWater: RecyclerView
    private lateinit var waterAdapter: WaterOutageAdapter
    private val waterList = mutableListOf<WaterOutage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main_browse, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // ---- 地震區塊 ----
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

    // ===== 新增：停水 / 降壓 API 呼叫 =====
    // ===== 停水 / 降壓：一次抓「台中市」+「臺中市」，合併並去重 =====
    private fun fetchWaterOutages() {
        val api = RetrofitClient.instance
        val wantCounties = listOf("台中市", "臺中市")

        tvWaterHeader.text = "停水 / 降壓（載入中…）"

        val all = mutableListOf<WaterOutage>()
        var remaining = wantCounties.size
        var ok = 0

        fun finishOnce() {
            remaining--
            if (remaining == 0) {
                // 去重：依你資料調整 key
                val dedup = all.distinctBy {
                    "${it.start_time}|${it.end_time}|${it.water_outage_areas}|${it.Buck_area}|${it.reason}"
                }
                if (ok == 0) {
                    tvWaterHeader.text = "停水 / 降壓（連線失敗）"
                    waterAdapter.reset(emptyList()); return
                }
                if (dedup.isEmpty()) {
                    tvWaterHeader.text = "停水 / 降壓（目前無資料）"
                } else {
                    tvWaterHeader.text = "停水 / 降壓"
                }
                waterAdapter.reset(dedup)
            }
        }

        wantCounties.forEach { cty ->
            api.getWaterOutages(cty).enqueue(object : retrofit2.Callback<WaterOutagesResponse> {
                override fun onResponse(
                    call: retrofit2.Call<WaterOutagesResponse>,
                    response: retrofit2.Response<WaterOutagesResponse>
                ) {
                    // 觀察實際打到的 URL 與回應碼
                    android.util.Log.d(
                        "WATER",
                        "URL=${response.raw().request.url} code=${response.code()} size=${response.body()?.data?.size ?: -1}"
                    )
                    if (response.isSuccessful) {
                        ok++
                        all.addAll(response.body()?.data.orEmpty())
                    }
                    finishOnce()
                }

                override fun onFailure(
                    call: retrofit2.Call<WaterOutagesResponse>,
                    t: Throwable
                ) {
                    android.util.Log.e("WATER", "failure: ${t.message}", t)
                    finishOnce()
                }
            })
        }
    }



}
