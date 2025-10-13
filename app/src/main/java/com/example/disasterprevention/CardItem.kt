package com.example.disasterprevention

data class CardItem(
    val title: String,
    val subtitle: String = "",
    val backgroundColor: Int,
    val titleColor: Int,
    val subtitleColor: Int,
    val onClick: (() -> Unit)? = null
)

