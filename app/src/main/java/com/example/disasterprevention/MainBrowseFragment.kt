package com.example.disasterprevention

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    private lateinit var waterAdapter: WaterOutageAdapter
    private val waterList = mutableListOf<WaterOutage>()
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

        fetchEarthquakeData()

        btnHistory.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            intent.putParcelableArrayListExtra("historyList", ArrayList(historyDataList))
            startActivity(intent)
        }
        // 取得 include 內的 RecyclerView（id 以 include_water_outage.xml 為準）
        val waterRecycler = view.findViewById<RecyclerView>(R.id.recycler_water)
        if (waterRecycler != null) {
            waterAdapter = WaterOutageAdapter(waterList)
            waterRecycler.layoutManager = LinearLayoutManager(requireContext())
            waterRecycler.adapter = waterAdapter

            fetchWaterOutages()    // ★ 加上這行，真正觸發抓資料
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
                        Glide.with(this@MainBrowseFragment).load(it.shakemap_url)
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
    private fun fetchWaterOutages() {
        val api = RetrofitClient.instance      // ← 若你叫 RetrofitClient 就改成 RetrofitClient.apiService
        val cities = arrayOf("臺中市", "台中市")

        val seen = HashSet<String>()
        val buffer = ArrayList<WaterOutage>()

        var idx = 0
        fun next() {
            if (idx >= cities.size) {
                // 依開始時間新到舊；若你的欄位不是字串時間，請調整排序 key
                buffer.sortByDescending { it.start_time }
                // 你的 Adapter 若有 reset(list) 方法就呼叫 reset；沒有就用下列三行：
                waterList.clear()
                waterList.addAll(buffer)
                waterAdapter.notifyDataSetChanged()
                Log.d("WATER", "rendered: ${waterList.size}")
                return
            }

            val county = cities[idx++]
            api.getWaterOutages(county).enqueue(object : Callback<WaterOutagesResponse> {
                override fun onResponse(
                    call: Call<WaterOutagesResponse>,
                    response: Response<WaterOutagesResponse>
                ) {
                    val items: List<WaterOutage> = response.body()?.data ?: emptyList()
                    for (w in items) {
                        val key = "${w.start_time}|${w.end_time}|${w.water_outage_areas}"
                        if (seen.add(key)) buffer.add(w)
                    }
                    next()
                }

                override fun onFailure(call: Call<WaterOutagesResponse>, t: Throwable) {
                    Log.w("WATER", "fetch fail $county: ${t.message}")
                    next()
                }
            })
        }
        next()
    }


}
