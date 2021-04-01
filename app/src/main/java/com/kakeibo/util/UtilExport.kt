package com.kakeibo.util

import android.content.Context
import com.kakeibo.R
import com.kakeibo.data.CategoryStatus
import com.kakeibo.data.ItemStatus

object UtilExport {
    fun build(context: Context,
              itemList: List<ItemStatus>,
              categoryMap: Map<Int, CategoryStatus>, fileName: String) {
        val runnable = Runnable {
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
            stringBuilder.append("\n")

            for (item in itemList) {
                stringBuilder.append(categoryMap[item.categoryCode]!!.name)
                stringBuilder.append(",")
                stringBuilder.append(item.getAmount())
                stringBuilder.append(",")
                stringBuilder.append(item.memo)
                stringBuilder.append(",")
                stringBuilder.append(item.eventDate)
                stringBuilder.append(",")
                stringBuilder.append(item.updateDate)
                stringBuilder.append("\n")
            }

            UtilFiles.writeToFile(fileName, stringBuilder.toString(), context, Context.MODE_PRIVATE)
        }
        val thread = Thread(runnable)
        thread.start()
    }
}