package com.example.disasterprevention

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.View

class WaterOutageMoreAdapter(
    private val items: List<WaterOutage>
) : RecyclerView.Adapter<WaterOutageMoreAdapter.VH>() {

    class VH(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_outage_more, parent, false
        )
    ) {
        val root: View = itemView
        val tvArea: TextView = itemView.findViewById(R.id.tv_item_area)
        val tvTime: TextView = itemView.findViewById(R.id.tv_item_time)
        val tvReason: TextView = itemView.findViewById(R.id.tv_item_reason)
    }

    private fun shortenTime(raw: String?): String {
        if (raw.isNullOrBlank()) return "-"
        val parts = raw.split(" ")
        if (parts.size < 2) return raw
        val datePart = parts[0]
        val timePart = parts[1]
        val dateTokens = datePart.split("-")
        val mmdd = if (dateTokens.size == 3) {
            "${dateTokens[1]}/${dateTokens[2]}"
        } else {
            datePart
        }
        val hhmm = timePart.substring(0, 5)
        return "$mmdd $hhmm"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val data = items[position]

        val startPretty = shortenTime(data.start_time)
        val endPretty   = shortenTime(data.end_time)

        holder.tvArea.text = data.water_outage_areas ?: "未提供區域"
        holder.tvTime.text = "時間：$startPretty ~ $endPretty"
        holder.tvReason.text = "原因：${data.reason ?: "未提供"}"

        // <--- 這裡開始處理點擊動畫 + 導頁 --->
        holder.root.setOnClickListener { view ->
            // 1) 按下去先做一個輕微縮放，然後彈回來
            view.animate()
                .scaleX(0.96f)
                .scaleY(0.96f)
                .setDuration(80)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    // 彈回來
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(80)
                        .setInterpolator(DecelerateInterpolator())
                        .withEndAction {
                            // 動畫結束 -> 開詳細頁
                            val ctx = view.context
                            val intent = Intent(ctx, WaterOutageDetailsActivity::class.java)
                            intent.putExtra("outage", data)

                            // 你也可以在這裡加轉場動畫的 flag 讓 Activity 有進場效果
                            ctx.startActivity(intent)

                            if (ctx is android.app.Activity) {
                                // slide in from right + 減淡背景（可用你現有的動畫資源）
                                ctx.overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.fade_out
                                )
                            }
                        }
                        .start()
                }
                .start()
        }
    }

    override fun getItemCount(): Int = items.size
}
