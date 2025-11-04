package com.example.disasterprevention

data class CardItem(
    val title: String,
    val subtitle: String,
    val backgroundColor: Int? = null,
    val backgroundResId: Int? = null,
    val backgroundTint: Int? = null,
    val titleColor: Int,
    val subtitleColor: Int,
    val iconResId: Int? = null,
    val iconLottieResId: Int? = null,
    val onClick: (() -> Unit)? = null
)

