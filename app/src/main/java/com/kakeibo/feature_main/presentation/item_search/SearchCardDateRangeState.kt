package com.kakeibo.feature_main.presentation.item_search

import com.kakeibo.util.UtilDate
import kotlinx.datetime.LocalDate

data class SearchCardDateRangeState(
    val from: LocalDate = UtilDate.getTodaysLocalDate(),
    val to: LocalDate = UtilDate.getTodaysLocalDate()
)