package com.kakeibo.feature_main.domain.models

import com.kakeibo.feature_main.presentation.item_search.CATEGORY_INVALID

class DisplayedCategoryModel(
    val _id: Long = -1L,
    val code: Int = CATEGORY_INVALID,
    val name: String = "",
    val color: Int = CATEGORY_INVALID,
    val sign: Int = CATEGORY_INVALID,
    val drawable: String = "",
    val image: ByteArray? = null,
    val parent: Int = CATEGORY_INVALID,
    val description: String = "",
    val savedDate: String = ""
//    var isSynced: Boolean
)