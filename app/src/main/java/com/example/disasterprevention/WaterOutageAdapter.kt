package com.example.disasterprevention

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WaterOutageAdapter(
    private val items: MutableList<WaterOutage> = mutableListOf()
) : RecyclerView.Adapter<WaterOutageAdapter.VH>() {

    /** 重設資料並刷新列表（給 Fragment 呼叫） */
    fun reset(newItems: List<WaterOutage>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_water_outage, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = items[position]
        holder.title.text = "停水/降壓"
        holder.content.text = "開始:${it.start_time}  結束:${it.end_time}\n區域:${it.water_outage_areas}"
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        // ⛳ 若你的 item_water_outage.xml 的 id 不同，改成實際的 id
        val title: TextView = v.findViewById(R.id.tv_title)
        val content: TextView = v.findViewById(R.id.tv_content)
    }
}
