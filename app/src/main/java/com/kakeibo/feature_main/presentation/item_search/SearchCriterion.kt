package com.kakeibo.feature_main.presentation.item_search

sealed class SearchCriterion(val name: String) {
    data class TypeDateRange(val type: String = "DateRange") : SearchCriterion(name = type)
    data class TypeAmount(val type: String = "Amount") : SearchCriterion(name = type)
    data class TypeCategory(val type: String = "Category") : SearchCriterion(name = type)
    data class TypeMemo(val type: String = "Memo") : SearchCriterion(name = type)
}