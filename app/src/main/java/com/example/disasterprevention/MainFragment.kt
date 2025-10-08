package com.example.disasterprevention

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainFragment : BrowseSupportFragment() {

    private val mHandler = Handler(Looper.getMainLooper())
    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics
    private var mBackgroundTimer: Timer? = null
    private var mBackgroundUri: String? = null

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var cardPresenter: CardPresenter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareBackgroundManager()
        setupUIElements()

        cardPresenter = CardPresenter()
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter

        fetchEarthquakeData()
       // fetchWaterOutages()
        setupEventListeners()
    }

    private fun prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(requireActivity())
        mBackgroundManager.attach(requireActivity().window)
        mDefaultBackground = ContextCompat.getDrawable(requireContext(), R.drawable.default_background)
        mMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(mMetrics)
    }

    private fun setupUIElements() {
        title = "地震資訊"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.fastlane_background)
        searchAffordanceColor = ContextCompat.getColor(requireContext(), R.color.search_opaque)
    }

    private fun fetchEarthquakeData() {
        RetrofitClient.instance.getEarthquakes().enqueue(object : Callback<EarthquakeResponse> {
            override fun onResponse(call: Call<EarthquakeResponse>, response: Response<EarthquakeResponse>) {
                if (response.isSuccessful) {
                    val quakes = response.body()?.data ?: emptyList()
                    if (quakes.isNotEmpty()) {
                        addLatestQuakeRow(quakes[0])
                        addHistoryRow(quakes)
                    }
                }
            }

            override fun onFailure(call: Call<EarthquakeResponse>, t: Throwable) {
                Log.e(TAG, "API 請求失敗: ${t.message}")
            }
        })
    }

    private fun addLatestQuakeRow(latest: Earthquake) {
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)
        val latestCard = QuakeCard(
            latest.epicenter,
            "${latest.magnitude}級\n${latest.taichung_intensity}",
            latest.shakemap_url ?: "https://你的API圖片網址.jpg"
        )
        listRowAdapter.add(latestCard)

        val header = HeaderItem(0, "最新地震")
        rowsAdapter.add(ListRow(header, listRowAdapter))
    }

    private fun addHistoryRow(historyList: List<Earthquake>) {
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)
        for (quake in historyList) {
            val card = QuakeCard(
                quake.epicenter,
                "${quake.magnitude}",
                quake.shakemap_url ?: "https://你的API圖片網址.jpg"
            )
            listRowAdapter.add(card)
        }

        val header = HeaderItem(1, "歷史地震")
        rowsAdapter.add(ListRow(header, listRowAdapter))
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is QuakeCard) {
                val intent = Intent(requireContext(), DetailsActivity::class.java)
                intent.putExtra("EPICENTER", item.title)
                intent.putExtra("MAGNITUDE", item.content)
                startActivity(intent)
            }
        }

        onItemViewSelectedListener = OnItemViewSelectedListener { _, item, _, _ ->
            if (item is QuakeCard) {
                mBackgroundUri = item.imageUrl
                startBackgroundTimer()
            }
        }
    }

    private fun updateBackground(uri: String?) {
        val width = mMetrics.widthPixels
        val height = mMetrics.heightPixels

        Glide.with(requireContext())
            .load(uri)
            .centerCrop()
            .error(mDefaultBackground)
            .into(object : CustomTarget<Drawable>(width, height) {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    mBackgroundManager.drawable = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // 空即可
                }
            })
        mBackgroundTimer?.cancel()
    }

    private fun startBackgroundTimer() {
        mBackgroundTimer?.cancel()
        mBackgroundTimer = Timer()
        mBackgroundTimer?.schedule(UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY.toLong())
    }

    private inner class UpdateBackgroundTask : TimerTask() {
        override fun run() {
            mHandler.post { updateBackground(mBackgroundUri) }
        }
    }
    // MainFragment.kt 內（class MainFragment : BrowseSupportFragment() { ... } 裡）



    private fun fetchWaterOutages() {
        RetrofitClient.instance.getWaterOutages(county = "台中市")
            .enqueue(object : retrofit2.Callback<WaterOutagesResponse> {
                override fun onResponse(
                    call: retrofit2.Call<WaterOutagesResponse>,
                    resp: retrofit2.Response<WaterOutagesResponse>
                ) {
                    val url = resp.raw().request.url.toString()
                    android.util.Log.d("WATER", "URL=$url code=${resp.code()}")

                    if (!resp.isSuccessful) {
                        // 例如 404/500：通常是路由或後端錯誤
                        return
                    }
                    val list = resp.body()?.data.orEmpty()
                    if (list.isEmpty()) {
                        // 沒資料就不加 row；你也可以放一張「目前無通報」卡片
                        return
                    }

                    // 映射成你現有的卡片資料結構（沿用 QuakeCard）
                    val rowAdapter = androidx.leanback.widget.ArrayObjectAdapter(cardPresenter)
                    list.forEach { o ->
                        rowAdapter.add(
                            QuakeCard(
                                title = "停水/降壓",
                                content = "開始:${o.start_time ?: "-"}  結束:${o.end_time ?: "-"}\n區域:${o.water_outage_areas ?: "-"}",
                                imageUrl = "" // 沒圖就空字串
                            )
                        )
                    }

                    rowsAdapter.add(
                        androidx.leanback.widget.ListRow(
                            androidx.leanback.widget.HeaderItem("停水/降壓"),
                            rowAdapter
                        )
                    )
                }

                override fun onFailure(call: retrofit2.Call<WaterOutagesResponse>, t: Throwable) {
                    android.util.Log.e("WATER", "failure: ${t.message}")
                }
            })
    }





    companion object {
        private const val TAG = "MainFragment"
        private const val BACKGROUND_UPDATE_DELAY = 300
    }
}
