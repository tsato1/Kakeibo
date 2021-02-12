package com.kakeibo.util

import android.util.Log
import com.kakeibo.data.ItemStatus
import com.kakeibo.ui.model.ExpandableListRowModel
import java.util.*

object UtilExpandableList {
    /*
     * items: List<ItemStatus> has to be ordered by eventDate (ascending)
     */
    //not used
    /***fun expandAll(items: List<ItemStatus>, expandableList: MutableList<ExpandableListRowModel>) {
        var startIndex = 0
        for (date in 1..31) {
            var firstTime = true

            for (i in startIndex until items.size) {
                if (items[i].eventDate.substring(8, 10).toInt() == date) { // '2021-02-11' -> '11'
                    if (firstTime) {
                        val parent = ExpandableListRowModel(ExpandableListRowModel.PARENT, items[i].eventDate)
                        expandableList.add(parent)
                        firstTime = false
                    }

                    val child = ExpandableListRowModel(ExpandableListRowModel.CHILD, items[i])
                    expandableList.add(child)
                    startIndex = i+1
                }
            }
        }
    }***/

    /*
     * items: List<ItemStatus> has to be ordered by eventDate (ascending)
     */
    //not used
    /***fun expandLast(items: List<ItemStatus>, expandableList: MutableList<ExpandableListRowModel>) {
        var header = ""
        var firstHeaderAdded = false

        var startIndex = items.size-1
        for (date in 31 downTo 1) {
            for (i in startIndex downTo 0) {
                if (items[i].eventDate.substring(8, 10).toInt() != date) { // '2021-02-11' -> '11'
                    break
                }

                if (!firstHeaderAdded) {
                    val child = ExpandableListRowModel(ExpandableListRowModel.CHILD, items[i])
                    expandableList.add(0, child)
                }
                header = items[i].eventDate
                startIndex = i-1
            }

            if (header.isNotBlank()) {
                val parent = ExpandableListRowModel(ExpandableListRowModel.PARENT, header)
                expandableList.add(0, parent)
                header = ""
                firstHeaderAdded = true
            }
        }
    }***/

    /*
     * expands only today's items
     */
    //disposable
//    fun expandToday(
//            expandableMasterMap: SortedMap<String, List<ItemStatus>>,
//            expandableList: MutableList<ExpandableListRowModel>) {
//        expandableList.addAll(expandableMasterMap.flatMap { entry ->
//            val parents = ExpandableListRowModel(ExpandableListRowModel.PARENT, entry.key)
//            val list = mutableListOf(parents)
//            if (entry.key == UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB)) {
//                val children = entry.value.map { child ->
//                    (ExpandableListRowModel(ExpandableListRowModel.CHILD, child))
//                }
//                list.addAll(children)
//            }
//            list
//        })
//    }

    /*
     * expands specific date (default is today's date)
     */
    fun expandOnlySpecificDate(
            expandableMasterMap: SortedMap<String, List<ItemStatus>>,
            expandableList: MutableList<ExpandableListRowModel>,
            date: String = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB)) {
        expandableList.clear()
        expandableList.addAll(expandableMasterMap.flatMap { entry ->
            val parent = ExpandableListRowModel(ExpandableListRowModel.PARENT, entry.key)
            val list = mutableListOf(parent)
            if (entry.key == date) {
                list[list.size-1].isExpanded = true
                val children = entry.value.map { child ->
                    (ExpandableListRowModel(ExpandableListRowModel.CHILD, child))
                }
                list.addAll(children)
            }
            list
        })
    }

    fun createMasterListFromMasterMap(expandableMasterMap: SortedMap<String, List<ItemStatus>>): List<ExpandableListRowModel> {
        return expandableMasterMap.flatMap { (key, value) ->
            val parents = ExpandableListRowModel(ExpandableListRowModel.PARENT, key)
            val children = value.map { child ->
                (ExpandableListRowModel(ExpandableListRowModel.CHILD, child))
            }
            val list = mutableListOf(parents)
            list.addAll(children)
            list
        }
    }
}