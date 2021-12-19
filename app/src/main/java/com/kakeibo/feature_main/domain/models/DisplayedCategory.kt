package com.kakeibo.feature_main.domain.models

data class DisplayedCategory(
    val _id: Long?,
    val code: Int,
    val color: Int,
    val name: String,
    val drawable: String,
    val image: ByteArray? = null,
    val location: Int
)