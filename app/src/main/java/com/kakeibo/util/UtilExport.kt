package com.kakeibo.util

import android.content.Context
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.item_main.item_list.components.ExpandableItem

object UtilExport {
    fun buildOrderByDate(fileName: String, context: Context, itemList: List<ExpandableItem>) {
        val runnable = Runnable {
            itemList.sortedBy { it.parent.date }

            val stringBuilder = StringBuilder()
            stringBuilder.setLength(0)
            stringBuilder.append(context.resources.getString(R.string.event_date))
            stringBuilder.append(",")
            stringBuilder.append(context.resources.getString(R.string.amount))
            stringBuilder.append(",")
            stringBuilder.append(context.resources.getString(R.string.category))
            stringBuilder.append(",")
            stringBuilder.append(context.resources.getString(R.string.memo))
            stringBuilder.append(",")
            stringBuilder.append(context.resources.getString(R.string.updated_date))
            stringBuilder.append(",")
            stringBuilder.append(context.resources.getString(R.string.category_code))
            stringBuilder.append("\n")

            for (parent in itemList) {
                for (child in parent.children) {
                    stringBuilder.append(child.eventDate)
                    stringBuilder.append(",")
                    stringBuilder.append(child.amount)
                    stringBuilder.append(",")
                    stringBuilder.append(child.categoryName)
                    stringBuilder.append(",")
                    stringBuilder.append(child.memo)
                    stringBuilder.append(",")
                    stringBuilder.append(child.updateDate)
                    stringBuilder.append(",")
                    stringBuilder.append(child.categoryCode)
                    stringBuilder.append("\n")
                }
            }

            UtilFiles.writeToFile(fileName, stringBuilder.toString(), context, Context.MODE_PRIVATE)
        }
        val thread = Thread(runnable)
        thread.start()
    }
}