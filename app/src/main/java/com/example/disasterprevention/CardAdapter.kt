package com.example.disasterprevention

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(
    private val items: List<CardItem>
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.card_item)
        val title: TextView = view.findViewById(R.id.tv_card_title)
        val subtitle: TextView = view.findViewById(R.id.tv_card_subtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle

        holder.card.setOnClickListener { item.onClick?.invoke() }

        // TV 焦點效果
        holder.card.isFocusable = true
        holder.card.isFocusableInTouchMode = true
    }

    override fun getItemCount(): Int = items.size
}
