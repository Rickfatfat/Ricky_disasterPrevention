package com.example.disasterprevention

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter

class WaterOutageDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(vh: ViewHolder, item: Any?) {
        val data = item as? WaterOutage ?: return

        // 主標題
        vh.title.text = "停水／降壓通知"

        // 副標：時間資訊
        val durationText = if (data.time_duration >= 0) "${data.time_duration} 小時" else "—"
        vh.subtitle.text = "開始：${data.start_time}   結束：${data.end_time}   影響時長：$durationText"

        // 內文：多行文字
        val bodyText = buildString {
            appendLine("原因：${safe(data.reason)}")
            appendLine("停水區域：${safe(data.water_outage_areas)}")
            appendLine("降壓區域：${safe(data.Buck_area)}")
        }.trim()

        vh.body.text = bodyText
    }

    private fun safe(s: String?): String = if (s.isNullOrBlank()) "（未提供）" else s
}
