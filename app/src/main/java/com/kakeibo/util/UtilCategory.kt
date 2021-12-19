package com.kakeibo.util

import com.kakeibo.core.data.local.entities.CategoryEntity

object UtilCategory {
    const val NUM_MAX_DSP_CATEGORIES = 16
    const val CUSTOM_CATEGORY_CODE_START = 1000
    const val NUM_MAX_CUSTOM_CATEGORY = 5

    const val CATEGORY_COLOR_INCOME = 1
    const val CATEGORY_COLOR_EXPENSE = 0
    const val CATEGORY_COLOR_NONE = 2

    const val CATEGORY_SIGN_CUS = -1 // default value for custom categories is -1
    const val CATEGORY_SIGN_LOW = 0 // default categories are 0
    const val CATEGORY_SIGN_MID = 1
    const val CATEGORY_SIGN_HIG = 2

    const val CATEGORY_PARENT_NON = -1 // if category doesn't have parent categories, -1

    fun isCategoryValid(
        code: String,
        name: String,
        allCategoriesMap: Map<Int, CategoryEntity>
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