package com.kakeibo.feature_main.presentation.item_search

data class SearchCriteriaLists(
    val defaultSearchCriteria: MutableList<SearchCriterion> = mutableListOf(
        SearchCriterion.TypeDateRange(),
        SearchCriterion.TypeAmount(),
        SearchCriterion.TypeCategory(),
        SearchCriterion.TypeMemo()
    ),
    val chosenSearchCriteria: MutableList<SearchCriterion> = mutableListOf()
)