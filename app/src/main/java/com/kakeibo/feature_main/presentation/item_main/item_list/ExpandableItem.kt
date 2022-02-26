package com.kakeibo.feature_main.presentation.item_main.item_list

import com.kakeibo.feature_main.domain.models.DisplayedItemModel

data class ExpandableItem(
    val parent: Parent,
    val children: List<DisplayedItemModel>
) {

    data class Parent(val date: String, val income: String, val expense: String)

}