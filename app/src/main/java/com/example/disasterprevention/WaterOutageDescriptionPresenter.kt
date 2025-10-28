package com.example.disasterprevention

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter

class WaterOutageDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
        vh: ViewHolder,
        item: Any?
    ) {
        val data = item as? WaterOutage ?: return

        // 大標題 (第一行大字)
        vh.title.text = "停水／降壓通報"

        // ---------- 時間長度處理 ----------
        // data.time_duration 是字串 (e.g. "6.8")，我們要把它變成「6.8 小時」
        // 如果是 null、空字串、轉數字失敗、或是負值，就顯示 "-"
        val durationText: String = data.time_duration
            ?.toFloatOrNull()
            ?.takeIf { it >= 0f }
            ?.let { "${it} 小時" }
            ?: "-"

        // 副標 (第二行中等字)
        // 我們把「開始時間 / 結束時間 / 持續時長」塞在一行
        vh.subtitle.text = "開始：${data.start_time ?: "-"}    結束：${data.end_time ?: "-"}    影響時長：$durationText"

        // ---------- 內容區塊 (多行詳細) ----------
        // body 是第三塊，通常是小一點字，可以換行顯示多段資訊
        val bodyText = buildString {
            appendLine("原因：${data.reason ?: "未提供"}")
            appendLine("停水區域：${data.water_outage_areas ?: "未提供"}")
            appendLine("減壓區域：${data.bock_area ?: "未提供"}")
        }.trim()

        vh.body.text = bodyText
    }
}
