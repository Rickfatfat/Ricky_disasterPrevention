package com.example.disasterprevention

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WaterOutageListAdapter(
    private val data: List<WaterOutage>,
    private val onClick: (WaterOutage) -> Unit
) : RecyclerView.Adapter<WaterOutageListAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvArea: TextView = view.findViewById(R.id.tv_reason)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_outage_more, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = data[position]

        holder.tvTitle.text = "公告 ${position + 1}"

        // 地區
        holder.tvArea.text = "區域: ${item.water_outage_areas ?: "無資料"}"

        // 時間
        val startStr = item.start_time ?: "?"
        val endStr = item.end_time ?: "?"
        holder.tvTime.text = "時間: $startStr ~ $endStr"

        // 點擊一筆 -> call onClick 回傳那筆資料
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }
}
