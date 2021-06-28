package com.kakeibo.util

import com.kakeibo.data.Category

object UtilCategory {
    const val NUM_MAX_DSP_CATEGORIES = 16
    const val CUSTOM_CATEGORY_CODE_START = 1000
    const val NUM_MAX_CUSTOM_CATEGORY = 5

    const val CATEGORY_COLOR_INCOME = 1
    const val CATEGORY_COLOR_EXPENSE = 0
    const val CATEGORY_COLOR_NONE = 2

    const val CATEGORY_SIGN_LOW = 0
    const val CATEGORY_SIGN_MID = 1
    const val CATEGORY_SIGN_HIG = 2

    fun isCategoryValid(
        code: String,
        name: String,
        allCategoriesMap: Map<Int, Category>
    ): Boolean {

        if (code.toIntOrNull() == null)
            return false

        if (!allCategoriesMap.containsKey(code.toInt()))
            return false

        val category = allCategoriesMap[code.toInt()]
        category?.let {
            if (it.name != name)
                return false
        }

        return true
    }
}