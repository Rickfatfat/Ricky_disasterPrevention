package com.example.disasterprevention

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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
        holder.tvDate.text = item.time
        holder.tvLocation.text = item.epicenter
        holder.tvMagnitude.text = "${item.magnitude}"
        holder.tvLevel.text = item.taichung_intensity

        Glide.with(holder.itemView.context)
            .load(item.shakemap_url)
            .into(holder.ivShakemap)
    }

    override fun getItemCount(): Int = historyList.size
}
