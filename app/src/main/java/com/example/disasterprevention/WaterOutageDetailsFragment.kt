package com.example.disasterprevention

import android.os.Build
import android.os.Bundle
import android.view.View
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.RowPresenter

class WaterOutageDetailsFragment : DetailsSupportFragment() {

    private var outage: WaterOutage? = null

    private lateinit var presenterSelector: ClassPresenterSelector
    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate WaterOutageDetailsFragment")

        // ✅ 修正：新版 API 33+ 的取值寫法（舊版做相容）
        outage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().intent.getParcelableExtra(
                WaterOutageDetailsActivity.EXTRA_WATER_OUTAGE,
                WaterOutage::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            requireActivity().intent.getParcelableExtra(
                WaterOutageDetailsActivity.EXTRA_WATER_OUTAGE
            )
        }

        presenterSelector = ClassPresenterSelector()
        rowsAdapter = ArrayObjectAdapter(presenterSelector)

        setupDetailsRow()
        setupTextOnlyPresenter()   // ✅ 改成用「自訂 Presenter 子類別」去隱藏圖片
        adapter = rowsAdapter
    }

    /** 只有文字的詳情 Row（不設定任何 imageDrawable、也暫不加 actions） */
    private fun setupDetailsRow() {
        val row = DetailsOverviewRow(outage)
        rowsAdapter.add(row)
    }

    /** 自訂 Presenter：覆寫 onBindRowViewHolder，在這裡把內建圖片 View 隱藏 */
    private fun setupTextOnlyPresenter() {
        val presenter = object : FullWidthDetailsOverviewRowPresenter(
            WaterOutageDescriptionPresenter()
        ) {
            override fun onBindRowViewHolder(vh: RowPresenter.ViewHolder, item: Any) {
                super.onBindRowViewHolder(vh, item)

                // ✅ 只處理「確定存在」的 id：details_overview_image
                val imageView =
                    vh.view.findViewById<View>(androidx.leanback.R.id.details_overview_image)
                imageView?.visibility = View.GONE
            }
        }.apply {
            backgroundColor = ContextCompat.getColor(
                requireContext(),
                R.color.selected_background
            )
            isParticipatingEntranceTransition = false
        }

        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, presenter)
    }

    companion object {
        private const val TAG = "WaterOutageDetails"
    }
}
