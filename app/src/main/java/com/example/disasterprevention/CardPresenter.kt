package com.example.disasterprevention

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide

class CardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        // 建立 ImageCardView
        val cardView = ImageCardView(parent.context)
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.setMainImageDimensions(500, 300) // 卡片圖片尺寸，可依需求調整
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val card = item as QuakeCard
        val cardView = viewHolder.view as ImageCardView

        // 設定文字
        cardView.titleText = card.title
        cardView.contentText = card.content

        // 設定圖片
        Glide.with(cardView.context)
            .load(card.imageUrl)
            .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // 可選：釋放圖片資源
        val cardView = viewHolder.view as ImageCardView
        cardView.mainImage = null
    }
}
