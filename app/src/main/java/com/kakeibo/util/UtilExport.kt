package com.kakeibo.util

import android.content.Context
import com.kakeibo.R
import com.kakeibo.data.Category
import com.kakeibo.data.Item

object UtilExport {
    fun buildOrderByDate(context: Context,
              itemList: List<Item>,
              categoryMap: Map<Int, Category>, fileName: String) {
        val runnable = Runnable {
            itemList.sortedBy { it.eventDate }

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

            for (item in itemList) {
                stringBuilder.append(item.eventDate)
                stringBuilder.append(",")
                stringBuilder.append(item.amount)
                stringBuilder.append(",")
                stringBuilder.append(categoryMap[item.categoryCode]!!.name)
                stringBuilder.append(",")
                stringBuilder.append(item.memo)
                stringBuilder.append(",")
                stringBuilder.append(item.updateDate)
                stringBuilder.append(",")
                stringBuilder.append(item.categoryCode)
                stringBuilder.append("\n")
            }

            UtilFiles.writeToFile(fileName, stringBuilder.toString(), context, Context.MODE_PRIVATE)
        }
        val thread = Thread(runnable)
        thread.start()
    }

    fun buildOrderByCategory(context: Context,
              itemList: List<Item>,
              categoryMap: Map<Int, Category>, fileName: String) {
        val runnable = Runnable {
            itemList.sortedBy { it.categoryCode }

            val stringBuilder = StringBuilder()
            stringBuilder.setLength(0)
            stringBuilder.append(context.resources.getString(R.string.category))
            stringBuilder.append(",")
            stringBuilder.append(context.resources.getString(R.string.amount))
            stringBuilder.append(",")
            stringBuilder.append(context.resources.getString(R.string.memo))
            stringBuilder.append(",")
            stringBuilder.append(context.resources.getString(R.string.event_date))
            stringBuilder.append(",")
            stringBuilder.append(context.resources.getString(R.string.updated_date))
            stringBuilder.append(",")
            stringBuilder.append(context.resources.getString(R.string.category_code))
            stringBuilder.append("\n")

            for (item in itemList) {
                stringBuilder.append(categoryMap[item.categoryCode]!!.name)
                stringBuilder.append(",")
                stringBuilder.append(item.amount)
                stringBuilder.append(",")
                stringBuilder.append(item.memo)
                stringBuilder.append(",")
                stringBuilder.append(item.eventDate)
                stringBuilder.append(",")
                stringBuilder.append(item.updateDate)
                stringBuilder.append(",")
                stringBuilder.append(item.categoryCode)
                stringBuilder.append("\n")
            }

            UtilFiles.writeToFile(fileName, stringBuilder.toString(), context, Context.MODE_PRIVATE)
        }
        val thread = Thread(runnable)
        thread.start()
    }
}