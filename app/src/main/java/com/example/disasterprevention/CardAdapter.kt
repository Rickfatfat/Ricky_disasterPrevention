package com.example.disasterprevention

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class CardAdapter(private val cardItems: List<CardItem>) :
    RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val frame: FrameLayout = view.findViewById(R.id.focus_wrapper)
        val cardView: androidx.cardview.widget.CardView = view.findViewById(R.id.card_container)
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
        holder.title.setTextColor(item.titleColor)
        holder.subtitle.setTextColor(item.subtitleColor)

        // ===== 背景處理 =====
        if (item.backgroundColor != null) {
            holder.layout.setBackgroundColor(item.backgroundColor)
        } else if (item.backgroundResId != null) {
            holder.layout.setBackgroundResource(item.backgroundResId)
        } else {
            holder.layout.setBackgroundColor(0x00000000)
        }

        holder.layout.backgroundTintList = null
        holder.layout.clipToOutline = true

        // ===== ICON / LOTTIE =====
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

        // ===== 點擊事件 =====
        holder.cardView.setOnClickListener { item.onClick?.invoke() }

        // ===== 聚焦：放大 + 柔光亮邊 =====
        holder.cardView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.cardView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).start()
                holder.cardView.cardElevation = 12f
                holder.frame.foreground = ContextCompat.getDrawable(
                    holder.cardView.context,
                    R.drawable.bg_card_focus_outline
                )
                holder.frame.invalidate() // ⬅️ 立即刷新，確保顯示
            } else {
                holder.cardView.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                holder.cardView.cardElevation = 6f
                holder.frame.foreground = null
                holder.frame.invalidate() // ⬅️ 清除後刷新
            }
        }
    }

    override fun getItemCount(): Int = cardItems.size
}
