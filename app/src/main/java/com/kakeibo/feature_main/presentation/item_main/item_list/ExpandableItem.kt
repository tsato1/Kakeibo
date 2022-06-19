package com.kakeibo.feature_main.presentation.item_main.item_list

import com.kakeibo.feature_main.domain.models.DisplayedItemModel

data class ExpandableItem(
    val parent: Parent,
    val children: List<DisplayedItemModel>
) {

    data class Parent(val date: String, val income: String, val expense: String, val scrollTo: Int)

}

fun List<DisplayedItemModel>.containsAt(id: Long): Int = run {
    var counter = 0
    for (ele in this) {
        if (ele.id == id) return counter
        counter += 1
    }
    return -1
}