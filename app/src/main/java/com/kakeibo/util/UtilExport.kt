package com.kakeibo.util

import android.content.Context
import com.kakeibo.R
import com.kakeibo.core.data.local.entities.CategoryEntity
import com.kakeibo.core.data.local.entities.ItemEntity

object UtilExport {
    fun buildOrderByDate(context: Context,
                         itemEntityList: List<ItemEntity>,
                         categoryEntityMap: Map<Int, CategoryEntity>, fileName: String) {
        val runnable = Runnable {
            itemEntityList.sortedBy { it.eventDate }

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

            for (item in itemEntityList) {
                stringBuilder.append(item.eventDate)
                stringBuilder.append(",")
                stringBuilder.append(item.amount)
                stringBuilder.append(",")
                stringBuilder.append(categoryEntityMap[item.categoryCode]!!.name)
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
                             itemEntityList: List<ItemEntity>,
                             categoryEntityMap: Map<Int, CategoryEntity>, fileName: String) {
        val runnable = Runnable {
            itemEntityList.sortedBy { it.categoryCode }

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

            for (item in itemEntityList) {
                stringBuilder.append(categoryEntityMap[item.categoryCode]!!.name)
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