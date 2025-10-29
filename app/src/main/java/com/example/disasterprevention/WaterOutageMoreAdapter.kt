package com.example.disasterprevention

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.animation.DecelerateInterpolator

class WaterOutageMoreAdapter(
    private val dataList: List<WaterOutage>,
    private val onItemClick: (WaterOutage) -> Unit
) : RecyclerView.Adapter<WaterOutageMoreAdapter.VH>() {

    // --- ViewHolder ---
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {

        private val rootRow = view.findViewById<View>(R.id.root_row)
        private val tvTitle = view.findViewById<TextView>(R.id.tv_more_title)
        private val tvReason  = view.findViewById<TextView>(R.id.tv_more_reason)
        private val tvTime  = view.findViewById<TextView>(R.id.tv_more_time)

        fun bind(item: WaterOutage) {

            // --- A. 處理「原因」 ---
            tvReason.text = "原因：${item.reason ?: "未提供"}"

            // --- B. 處理「時間」 ---
            tvTime.text = "時間：${item.start_time ?: "-"} ~ ${item.end_time ?: "-"}"

            // --- C. 處理「動態標題」 (依賴於原因和區域欄位) ---
            val checkText = item.reason ?: ""
            val hasOutage = checkText.contains("停水") || checkText.contains("無水")
            val hasPressureDrop = checkText.contains("降壓")

            val titleTypes = mutableListOf<String>()
            if (hasOutage) titleTypes.add("停水")
            if (hasPressureDrop) titleTypes.add("降壓")

            // 如果原因中沒有關鍵字，則從區域欄位判斷
            if (titleTypes.isEmpty()) {
                if (item.water_outage_areas.takeIf { it != null && it != "null" } != null) {
                    titleTypes.add("停水")
                }
                if (item.Buck_area.takeIf { it != null && it != "null" } != null) {
                    titleTypes.add("降壓")
                }
            }

            if (titleTypes.isEmpty()) {
                tvTitle.text = "最新公告"
            } else {
                // .distinct() 是為了防止 "停水" 被加入兩次
                tvTitle.text = titleTypes.distinct().joinToString("及") + "資訊"
            }

            // --- D. 處理「動畫」和「點擊」 ---

            // 遙控器移上 / 移走 的放大縮回
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

            // OK/Enter 點下去：進詳細頁
            rootRow.setOnClickListener {
                onItemClick(item)
            }
        }
        // --- bind 方法結束 ---
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_outage_more, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val outage = dataList[position]
        holder.bind(outage)
    }

    override fun getItemCount(): Int = dataList.size
}
