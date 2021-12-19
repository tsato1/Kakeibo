package com.kakeibo.util

import android.util.Log
import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.ui.model.ExpandableListRowModel
import java.util.*

object UtilExpandableList {

    private const val TAG = "UtilExpandableList"

    /*
     * expands specific date (default is today's date)
     * input variable date must be in DB format
     */
    fun expandOnlySpecificDate(
        expandableMasterMap: SortedMap<ExpandableListRowModel.Header, List<ItemEntity>>,
        expandableList: MutableList<ExpandableListRowModel>,
        date: String = UtilDate.getTodaysYMD(UtilDate.DATE_FORMAT_DB)) {
        Log.d(TAG, "date=$date")
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
}