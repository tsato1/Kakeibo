package com.kakeibo.util

import com.kakeibo.data.Item
import com.kakeibo.ui.adapter.view.ExpandableListAdapter
import com.kakeibo.ui.model.ExpandableListRowModel
import java.util.*

object UtilExpandableList {
    /*
     * expands specific date (default is today's date)
     */
    fun expandOnlySpecificDate(
            expandableMasterMap: SortedMap<ExpandableListRowModel.Header, List<Item>>,
            expandableList: MutableList<ExpandableListRowModel>,
            date: String = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB)) {
        expandableList.clear()
        expandableList.addAll(expandableMasterMap.flatMap { entry ->
            val parent = ExpandableListRowModel(ExpandableListRowModel.PARENT, entry.key)
            val list = mutableListOf(parent)
            if (entry.key.date == date) {
                list[list.size-1].isExpanded = true
                val children = entry.value.map { child ->
                    (ExpandableListRowModel(ExpandableListRowModel.CHILD, child))
                }
                list.addAll(children)
            }
            list
        })
    }

    fun expandOnlySpecificDate(
            adapter: ExpandableListAdapter,
            date: String = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB)) {
        return expandOnlySpecificDate(adapter.getMasterMap(), adapter.getExpandableList(), date)
    }
}