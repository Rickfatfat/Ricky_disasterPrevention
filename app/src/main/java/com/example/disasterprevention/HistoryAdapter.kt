package com.example.disasterprevention

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HistoryAdapter(private val historyList: List<Earthquake>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var expandedPosition = -1
    private var recyclerView: RecyclerView? = null

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivShakemap: ImageView = view.findViewById(R.id.historyShakemap)
        val tvDate: TextView = view.findViewById(R.id.historyDate)
        val tvLocation: TextView = view.findViewById(R.id.historyEpicenter)
        val tvMagnitude: TextView = view.findViewById(R.id.historyMagnitude)
        val tvLevel: TextView = view.findViewById(R.id.historyIntensity)
        val detailContainer: View = view.findViewById(R.id.detailContainer)
        val tvDetail: TextView = view.findViewById(R.id.historyDetail)
    }

    override fun onAttachedToRecyclerView(rv: RecyclerView) {
        super.onAttachedToRecyclerView(rv)
        recyclerView = rv
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
        holder.tvDetail.text = "詳細資料可在此顯示更多訊息"

        Glide.with(holder.itemView.context)
            .load(item.shakemap_url)
            .into(holder.ivShakemap)

        val isExpanded = position == expandedPosition
        holder.detailContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded

        // 聚焦動畫
        holder.itemView.isFocusable = true
        holder.itemView.isFocusableInTouchMode = true
        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.animate().scaleX(1.08f).scaleY(1.08f).setDuration(150).start()
                v.elevation = 20f
            } else {
                v.animate().scaleX(if (isExpanded) 1.08f else 1.0f)
                    .scaleY(if (isExpanded) 1.08f else 1.0f)
                    .setDuration(150).start()
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
                    v.animate().scaleX(if (holder.itemView.isFocused || isExpanded) 1.08f else 1.0f)
                        .scaleY(if (holder.itemView.isFocused || isExpanded) 1.08f else 1.0f)
                        .setDuration(100).start()
                }
            }
            false
        }

        // 點擊展開 / 收回手風琴
        holder.itemView.setOnClickListener {
            val previousExpanded = expandedPosition
            expandedPosition = if (expandedPosition == position) -1 else position

            // 收回前一個
            if (previousExpanded != -1) {
                val prevHolder = recyclerView?.findViewHolderForAdapterPosition(previousExpanded)
                prevHolder?.itemView?.findViewById<View>(R.id.detailContainer)?.let { container ->
                    collapseView(container)
                }
                notifyItemChanged(previousExpanded)
            }

            // 展開當前
            if (expandedPosition == position) {
                expandView(holder.detailContainer)
            } else {
                collapseView(holder.detailContainer)
            }

            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = historyList.size

    // --- 展開 / 收回動畫（高度 + 淡入淡出） ---
    private fun expandView(view: View, duration: Long = 200) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.UNSPECIFIED
        )
        val targetHeight = view.measuredHeight
        view.layoutParams.height = 0
        view.alpha = 0f
        view.visibility = View.VISIBLE

        val heightAnimator = ValueAnimator.ofInt(0, targetHeight)
        heightAnimator.addUpdateListener { valueAnimator ->
            view.layoutParams.height = valueAnimator.animatedValue as Int
            view.requestLayout()
        }

        val alphaAnimator = ValueAnimator.ofFloat(0f, 1f)
        alphaAnimator.addUpdateListener { valueAnimator ->
            view.alpha = valueAnimator.animatedValue as Float
        }

        heightAnimator.duration = duration
        alphaAnimator.duration = duration

        heightAnimator.start()
        alphaAnimator.start()
    }

    private fun collapseView(view: View, duration: Long = 200) {
        val initialHeight = view.height
        val heightAnimator = ValueAnimator.ofInt(initialHeight, 0)
        heightAnimator.addUpdateListener { valueAnimator ->
            view.layoutParams.height = valueAnimator.animatedValue as Int
            view.requestLayout()
        }

        val alphaAnimator = ValueAnimator.ofFloat(view.alpha, 0f)
        alphaAnimator.addUpdateListener { valueAnimator ->
            view.alpha = valueAnimator.animatedValue as Float
        }

        heightAnimator.duration = duration
        alphaAnimator.duration = duration

        heightAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
            }
        })

        heightAnimator.start()
        alphaAnimator.start()
    }
}
