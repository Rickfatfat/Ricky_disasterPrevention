package com.example.disasterprevention

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.disasterprevention.R
import com.example.disasterprevention.WaterOutage

class WaterOutageAdapter(
    private val items: MutableList<WaterOutage>
) : RecyclerView.Adapter<WaterOutageAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvContent: TextView = view.findViewById(R.id.tv_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_water_outage, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val w = items[position]
        val title = buildString {
            append(w.water_outage_areas.ifBlank { "未提供地區" })
            if (w.Buck_area.isNotBlank()) append("（降壓：${w.Buck_area}）")
        }
        val content = buildString {
            appendLine("原因：${w.reason.ifBlank { "未提供" }}")
            appendLine("時間：${w.start_time} ～ ${w.end_time.ifBlank { "未定" }}")
            append("影響時長(小時)：${w.time_duration}")
        }
        holder.tvTitle.text = title
        holder.tvContent.text = content
    }

    override fun getItemCount() = items.size

    fun reset(newItems: List<WaterOutage>) {
        android.util.Log.d("WATER", "adapter reset with ${newItems.size} items")
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

}
