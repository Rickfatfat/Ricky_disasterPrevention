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

    // --- 這是唯一的修改區域 ---
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position] // 取得目前這一筆資料

        // --- A. 準備所有要顯示的內容 ---

        // 1. 取得原因
        val reasonStr = "原因：${item.reason ?: "未提供"}"

        // 2. 取得時間字串
        val timeStr = "時間：${item.start_time ?: "-"} ~ ${item.end_time ?: "-"}"

        // 3. 取得區域欄位，並過濾掉 "null" 字串
        val areaOutage = item.water_outage_areas.takeIf { it != null && it != "null" }
        val areaPressure = item.Buck_area.takeIf { it != null && it != "null" }

        // 4. 組合區域文字
        val areaDisplayBuilder = StringBuilder()
        if (areaOutage != null) {
            areaDisplayBuilder.append("停水區域：\n")
            areaDisplayBuilder.append(areaOutage.trim())
        }
        if (areaPressure != null) {
            if (areaDisplayBuilder.isNotEmpty()) areaDisplayBuilder.append("\n\n")
            areaDisplayBuilder.append("降壓區域：\n")
            areaDisplayBuilder.append(areaPressure.trim())
        }

        // --- B. 組合最終給 content 欄位的文字 (順序已對調) ---
        val contentBuilder = StringBuilder()
        contentBuilder.append(reasonStr)
        contentBuilder.append("\n\n")
        contentBuilder.append(timeStr)

        if (areaDisplayBuilder.isNotEmpty()) {
            contentBuilder.append("\n\n")
            contentBuilder.append(areaDisplayBuilder.toString())
        }

        holder.content.text = contentBuilder.toString()


        // --- C. 處理「動態標題」 (邏輯不變) ---
        val checkText = item.reason ?: ""
        val hasOutage = checkText.contains("停水") || checkText.contains("無水")
        val hasPressureDrop = checkText.contains("降壓")

        val titleTypes = mutableListOf<String>()
        if (hasOutage) titleTypes.add("停水")
        if (hasPressureDrop) titleTypes.add("降壓")

        if (titleTypes.isEmpty()) {
            // 如果 reason 沒寫，但區域欄位有，也算
            if (areaOutage != null) titleTypes.add("停水")
            if (areaPressure != null) titleTypes.add("降壓")
        }

        if (titleTypes.isEmpty()) {
            holder.title.text = "最新公告"
        } else {
            // .distinct() 是為了防止 "停水" 被加入兩次
            holder.title.text = titleTypes.distinct().joinToString("及") + "資訊"
        }
    }
    // --- 修改區域結束 ---


    class VH(v: View) : RecyclerView.ViewHolder(v) {
        // ⛳ 若你的 item_water_outage.xml 的 id 不同，改成實際的 id
        val title: TextView = v.findViewById(R.id.tv_title)
        val content: TextView = v.findViewById(R.id.tv_content)
    }
}