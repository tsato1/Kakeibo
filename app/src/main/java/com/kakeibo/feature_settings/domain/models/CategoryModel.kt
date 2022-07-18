package com.kakeibo.feature_settings.domain.models

import com.kakeibo.R
import com.kakeibo.core.data.local.entities.CategoryEntity
import com.kakeibo.ui.theme.MatchaGreen
import com.kakeibo.ui.theme.VividRed
import com.kakeibo.util.UtilCategory

class CategoryModel(
    val _id: Long,
    val code: Int,
    val name: String,
    val color: Int,
    val sign: Int,
    val drawable: String,
    val image: ByteArray? = null,
    val parent: Int,
    val description: String,
    val savedDate: String,
    var isSynced: Boolean = false
) {

    companion object {
        val types = listOf(
            Triple(UtilCategory.CATEGORY_COLOR_EXPENSE, MatchaGreen, R.string.expense),
            Triple(UtilCategory.CATEGORY_COLOR_INCOME, VividRed, R.string.income)
        )
    }

    fun toCategoryEntity(): CategoryEntity {
        return CategoryEntity(
            id = _id,
            code = code,
            name = name,
            color = color,
            sign = sign,
            drawable = drawable,
            image = image,
            parent = parent,
            description = description,
            savedDate = savedDate,
            isSynced = isSynced
        )
    }

    class InvalidCustomCategoryException(message: String) : Exception(message)
}