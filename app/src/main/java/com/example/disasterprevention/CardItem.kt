package com.example.disasterprevention

data class CardItem(
    val title: String,
    val subtitle: String = "",
    val onClick: (() -> Unit)? = null
)
