package com.example.disasterprevention

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WaterOutageMoreAdapter(
    private val dataList: List<WaterOutage>,
    private val onItemClick: (WaterOutage) -> Unit
) : RecyclerView.Adapter<WaterOutageMoreAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {

        private val rootRow = view.findViewById<View>(R.id.root_row)
        private val tvTitle = view.findViewById<TextView>(R.id.tv_more_title)
        private val tvArea  = view.findViewById<TextView>(R.id.tv_more_area)
        private val tvTime  = view.findViewById<TextView>(R.id.tv_more_time)

        fun bind(item: WaterOutage) {

            // ---- 1. 填資料 ------------------------------------------------
            tvTitle.text = "【公告 ${adapterPosition + 1}】"
            tvArea.text  = "區域：${item.water_outage_areas ?: "-"}"
            tvTime.text  =
                "時間：${item.start_time ?: "-"} ~ ${item.end_time ?: "-"}"

            // ---- 2. 遙控器移上 / 移走 的放大縮回 --------------------------
            rootRow.setOnFocusChangeListener { v, hasFocus ->
                // 先把舊動畫停掉，避免殘留/累加
                v.animate().cancel()

                if (hasFocus) {
                    // 有焦點：放大一點點，滑順
                    v.animate()
                        .scaleX(1.07f)
                        .scaleY(1.07f)
                        .setDuration(120L)
                        .setInterpolator(
                            android.view.animation.DecelerateInterpolator()
                        )
                        .start()
                } else {
                    // 失去焦點：直接還原，不要再播一段縮小動畫
                    v.scaleX = 1f
                    v.scaleY = 1f
                }
            }

            // ---- 3. OK/Enter 點下去：進詳細頁 ----------------------------
            rootRow.setOnClickListener {
                // 這裡不再額外做「再縮一次」的動畫
                // 直接呼叫外面傳進來的 callback 去開詳細頁
                onItemClick(item)
            }
        }
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
