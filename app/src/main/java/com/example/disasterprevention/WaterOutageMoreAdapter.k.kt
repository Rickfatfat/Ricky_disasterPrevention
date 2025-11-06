package com.example.disasterprevention

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.animation.DecelerateInterpolator

// 共用：把 "null" 字串與空白都視為無資料
private fun String?.isNullOrBlankOrLiteralNull(): Boolean =
    this.isNullOrBlank() || this.equals("null", ignoreCase = true)

// 共用：第三層卡片標題決策（欄位優先，其次 reason 關鍵字）
private fun titleFor(o: WaterOutage): String {
    val hasWater    = !o.water_outage_areas.isNullOrBlankOrLiteralNull()
    val hasPressure = !o.Buck_area.isNullOrBlankOrLiteralNull()

    if (hasWater && hasPressure) return "停水及降壓資訊"
    if (hasWater) return "停水資訊"
    if (hasPressure) return "降壓資訊"

    val r = o.reason.orEmpty()
    val outageByReason   = r.contains("停水") || r.contains("無水")
    val pressureByReason = r.contains("降壓")
    return when {
        outageByReason && pressureByReason -> "停水及降壓資訊"
        outageByReason -> "停水資訊"
        pressureByReason -> "降壓資訊"
        else -> "最新公告"
    }
}

class WaterOutageMoreAdapter(
    private val dataList: List<WaterOutage>,
    private val onItemClick: (WaterOutage) -> Unit
) : RecyclerView.Adapter<WaterOutageMoreAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {

        private val rootRow = view.findViewById<View>(R.id.root_row)
        private val tvTitle = view.findViewById<TextView>(R.id.tv_more_title)
        private val tvReason = view.findViewById<TextView>(R.id.tv_more_reason)
        private val tvTime = view.findViewById<TextView>(R.id.tv_more_time)

        fun bind(item: WaterOutage) {
            // 標題：依欄位優先、reason 後備
            tvTitle.text = titleFor(item)

            // 原因
            val reasonText = item.reason?.takeUnless { it.isNullOrBlankOrLiteralNull() } ?: "未提供"
            tvReason.text = "原因：$reasonText"

            // 時間
            val startStr = item.start_time?.takeUnless { it.isNullOrBlankOrLiteralNull() } ?: "-"
            val endStr   = item.end_time?.takeUnless { it.isNullOrBlankOrLiteralNull() } ?: "-"
            tvTime.text = "時間：$startStr ~ $endStr"

            // 焦點放大動畫
            rootRow.setOnFocusChangeListener { v, hasFocus ->
                v.animate().cancel()
                if (hasFocus) {
                    v.animate()
                        .scaleX(1.07f)
                        .scaleY(1.07f)
                        .setDuration(120L)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                } else {
                    v.scaleX = 1f
                    v.scaleY = 1f
                }
            }

            // 點擊跳詳細
            rootRow.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_outage_more, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size
}