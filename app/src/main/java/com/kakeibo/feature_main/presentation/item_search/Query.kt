package com.kakeibo.feature_main.presentation.item_search

data class Query(
    var fromDate: String = "",
    var toDate: String = "",
    var fromAmount: String = "",
    var toAmount: String = "",
    var categoryCode: Int = -1,
    var memo: String = ""
)