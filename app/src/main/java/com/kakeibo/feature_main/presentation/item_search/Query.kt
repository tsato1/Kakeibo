package com.kakeibo.feature_main.presentation.item_search

data class Query(
    var fromDate: String? = null,
    var toDate: String? = null,
    var fromAmount: String? = null,
    var toAmount: String? = null,
    var categoryCode: Int? = null,
    var memo: String? = null,
    var fromUpdateDate: String? = null,
    var toUpdateDate: String? = null
)