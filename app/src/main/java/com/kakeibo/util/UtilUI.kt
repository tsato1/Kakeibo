package com.kakeibo.util

import android.content.Context
import com.kakeibo.R

object UtilUI {

    fun getToolbarHeight(context: Context): Int {
        val styledAttributes = context.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        val toolbarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return toolbarHeight
    }

    fun getTabsHeight(context: Context): Int {
        return context.resources.getDimension(R.dimen.tab_height).toInt()
    }
}