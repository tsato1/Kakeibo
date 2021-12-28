package com.kakeibo.feature_settings.presentation.category_reorder

import com.kakeibo.feature_settings.domain.models.CategoryModel

sealed class GridItem(val id: Long, val itemType: Int, val category: CategoryModel?) {
    companion object {
        const val ITEM_TYPE_HEADER = 1
        const val ITEM_TYPE_PARENT = 2
        const val ITEM_TYPE_CHILD = 3
    }

    class HeaderItem(id: Long) : GridItem(id, ITEM_TYPE_HEADER, null)
    class ParentItem(id: Long, val data: CategoryModel) : GridItem(id, ITEM_TYPE_PARENT, data)
    class ChildItem(id: Long, val data: CategoryModel) : GridItem(id, ITEM_TYPE_CHILD, data)
}