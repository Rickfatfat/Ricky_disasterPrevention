package com.example.disasterprevention

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater

class CardAdapter(private val items: List<CardItem>) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: View = view.findViewById(R.id.card_inner)
        val title: TextView = view.findViewById(R.id.card_title)
        val subtitle: TextView = view.findViewById(R.id.card_subtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.title
        holder.subtitle.text = item.subtitle

        // 動態設定顏色
        val drawable = DrawableCompat.wrap(holder.container.background)
        DrawableCompat.setTint(drawable, item.backgroundColor)
        holder.container.background = drawable

        holder.title.setTextColor(item.titleColor)
        holder.subtitle.setTextColor(item.subtitleColor)

        // 焦點 & 點擊動畫
        holder.itemView.apply {
            isFocusable = true
            isFocusableInTouchMode = true

            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.animate().scaleX(1.08f).scaleY(1.08f).setDuration(150).start()
                    v.elevation = 20f
                } else {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
                    v.elevation = 0f
                }
            }

            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start()
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.animate().scaleX(if (v.isFocused) 1.08f else 1.0f)
                            .scaleY(if (v.isFocused) 1.08f else 1.0f)
                            .setDuration(100).start()
                    }
                }
                false
            }

            setOnClickListener {
                item.onClick?.invoke()
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
