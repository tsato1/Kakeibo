package com.kakeibo.feature_main.domain.models

class DisplayedCategoryModel(
    val _id: Long,
    val code: Int,
    val name: String,
    val color: Int,
    val sign: Int,
    val drawable: String,
    val image: ByteArray? = null,
    val parent: Int,
    val description: String,
    val savedDate: String
//    var isSynced: Boolean
)