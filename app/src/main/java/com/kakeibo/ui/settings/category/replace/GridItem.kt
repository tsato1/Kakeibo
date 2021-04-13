package com.kakeibo.ui.settings.category.replace

import com.kakeibo.data.Category

sealed class GridItem(val id: Long, val itemType: Int, val category: Category?) {
    companion object {
        const val ITEM_TYPE_HEADER = 1
        const val ITEM_TYPE_PARENT = 2
        const val ITEM_TYPE_CHILD = 3
    }

    class HeaderItem(id: Long) : GridItem(id, ITEM_TYPE_HEADER, null)
    class ParentItem(id: Long, val data: Category) : GridItem(id, ITEM_TYPE_PARENT, data)
    class ChildItem(id: Long, val data: Category) : GridItem(id, ITEM_TYPE_CHILD, data)
}