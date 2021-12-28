package com.kakeibo.feature_main.domain.models

import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.util.UtilCategory
import java.math.BigDecimal

data class DisplayedItemModel(
    val id: Long?,
    val amount: String,
    val currencyCode: String,
    val categoryCode: Int,
    val memo: String,
    val eventDate: String,
    val updateDate: String,
    val categoryName: String = "Category Name",
    val categoryColor: Int = 0,
    val categorySign: Int = 0,
    val categoryDrawable: String = "Category Drawable",
    val categoryImage: ByteArray? = null,
    val categoryParent: Int = -1,
    val categoryDescription: String = "Category Description",
    val categorySavedDate: String = "Category Saved Date"
//    val isSynced: Boolean
) {

    fun toItemEntity(): ItemEntity {

        val amountBigDecimal = when (categoryColor) {
            UtilCategory.CATEGORY_COLOR_INCOME -> {
                BigDecimal(amount.replace(',', '.')) // todo: accommodate comma
            }
            UtilCategory.CATEGORY_COLOR_EXPENSE -> {
                BigDecimal(amount.replace(',', '.')).negate()
            }
            else -> {
                BigDecimal(0)
            }
        }

        return ItemEntity(
            id = id ?: 0,
            amount = amountBigDecimal, // converters will do .multiply(BigDecimal(1000)).toLong()
            currencyCode = currencyCode,
            categoryCode = categoryCode,
            memo = memo,
            eventDate = eventDate,
            updateDate = updateDate
//            isSynced = isSynced
        )

    }

}