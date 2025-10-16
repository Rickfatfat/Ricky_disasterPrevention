package com.example.disasterprevention

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
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

        // ---- 停水 / 降壓 區塊（來自 include_water_outage.xml）----
        tvWaterHeader = view.findViewById(R.id.tv_water_header)
        rvWater = view.findViewById(R.id.rv_water)
        rvWater.layoutManager = LinearLayoutManager(requireContext())
        waterAdapter = WaterOutageAdapter(waterList)   // 檔案放在同一個 package 底下
        rvWater.adapter = waterAdapter

        rvWater.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(
                requireContext(), LinearLayoutManager.VERTICAL
            )
        )

        // 取得資料
        fetchEarthquakeData()
        fetchWaterOutages()  // 新增：實際打停水 API

        btnHistory.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            intent.putParcelableArrayListExtra("historyList", ArrayList(historyDataList))
            startActivity(intent)
        }
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
                            "${it.time} ${it.epicenter} 規模${it.magnitude} 當地${it.taichung_intensity}級"
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
