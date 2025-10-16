package com.example.disasterprevention.ui.presenter

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter

data class WaterOutageCard(
    val title: String,
    val content: String
)

class WaterOutageCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // 我們這個 row 只顯示文字，不放圖片
            setMainImageDimensions(0, 0)
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val card = item as WaterOutageCard
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = card.title
        cardView.contentText = card.content
        // 不載入圖片
        cardView.mainImage = null
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) = Unit
}
