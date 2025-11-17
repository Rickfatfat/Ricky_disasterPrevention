package com.example.disasterprevention

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.util.Log

class HistoryAdapter(private val historyList: List<Earthquake>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivShakemap: ImageView = view.findViewById(R.id.historyShakemap)
        val tvDate: TextView = view.findViewById(R.id.historyDate)
        val tvLocation: TextView = view.findViewById(R.id.historyEpicenter)
        val tvMagnitude: TextView = view.findViewById(R.id.historyMagnitude)
        val tvLevel: TextView = view.findViewById(R.id.historyIntensity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]

        // 設定顯示內容
        holder.tvDate.text = item.time
        holder.tvLocation.text = item.epicenter
        holder.tvMagnitude.text = "規模：${item.magnitude}"
        holder.tvLevel.text = "震度：${item.taichung_intensity}"

        Glide.with(holder.itemView.context)
            .load(item.shakemap_url)
            .into(holder.ivShakemap)

        // 聚焦動畫（上下鍵選擇列）
        holder.itemView.isFocusable = true
        holder.itemView.isFocusableInTouchMode = true
        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.animate().scaleX(1.08f).scaleY(1.08f).setDuration(150).start()
                v.elevation = 20f
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
                v.elevation = 0f
            }
        }

        // 按下縮小動畫
        holder.itemView.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start()
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(if (holder.itemView.isFocused) 1.08f else 1.0f)
                        .scaleY(if (holder.itemView.isFocused) 1.08f else 1.0f)
                        .setDuration(100).start()
                }
            }
            false
        }

        // 點擊整列跳轉到歷史地震詳細頁面
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, LatestDetailActivity::class.java)
            intent.putExtra("TITLE", "歷史地震資訊")
            intent.putExtra("TIME", item.time)
            intent.putExtra("EPICENTER", item.epicenter)
            intent.putExtra("MAGNITUDE", item.magnitude.toString())
            intent.putExtra("INTENSITY", item.taichung_intensity)
            intent.putExtra("SHAKEMAP_URL", item.shakemap_url)
            context.startActivity(intent)
            Log.d("HistoryAdapter", "傳遞資料: ${item.time}, ${item.epicenter}, ${item.magnitude}, ${item.taichung_intensity}, ${item.shakemap_url}")
        }

    }

    override fun getItemCount(): Int = historyList.size
}
