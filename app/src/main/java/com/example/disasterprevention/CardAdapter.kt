package com.example.disasterprevention

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout

class CardAdapter(private val cardItems: List<CardItem>) :
    RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: ConstraintLayout = view.findViewById(R.id.card_inner)
        val title: TextView = view.findViewById(R.id.card_title)
        val subtitle: TextView = view.findViewById(R.id.card_subtitle)
        val icon: ImageView = view.findViewById(R.id.card_icon)
        val lottie: LottieAnimationView = view.findViewById(R.id.card_lottie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cardItems[position]

        holder.title.text = item.title
        holder.subtitle.text = item.subtitle

        when {
            item.backgroundResId != null -> holder.layout.setBackgroundResource(item.backgroundResId)
            item.backgroundColor != null -> holder.layout.setBackgroundColor(item.backgroundColor)
            else -> holder.layout.setBackgroundColor(0x00000000)
        }
        item.backgroundTint?.let { tintColor ->
            val bg = holder.layout.background
            bg?.setTint(tintColor)
            holder.layout.background = bg
        }

        holder.title.setTextColor(item.titleColor)
        holder.subtitle.setTextColor(item.subtitleColor)

        if (item.iconLottieResId != null) {
            holder.icon.visibility = View.GONE
            holder.lottie.visibility = View.VISIBLE
            holder.lottie.setAnimation(item.iconLottieResId)
            holder.lottie.playAnimation()
        } else if (item.iconResId != null) {
            holder.lottie.cancelAnimation()
            holder.lottie.visibility = View.GONE
            holder.icon.visibility = View.VISIBLE
            holder.icon.setImageResource(item.iconResId)
        } else {
            holder.lottie.cancelAnimation()
            holder.lottie.visibility = View.GONE
            holder.icon.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { item.onClick?.invoke() }

        // 聚焦放大動畫
        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).start()
                holder.layout.elevation = 12f
            } else {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                holder.layout.elevation = 4f
            }
        }
    }

    override fun getItemCount(): Int = cardItems.size
}
