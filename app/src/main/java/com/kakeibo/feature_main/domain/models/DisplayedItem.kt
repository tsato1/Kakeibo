package com.kakeibo.feature_main.domain.models

import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.util.UtilCategory
import java.math.BigDecimal

data class DisplayedItem(
    val id: Long? = null,
    val amount: String,
    val currencyCode: String,
    val categoryCode: Int,
    val categoryColor: Int,
    val memo: String,
    val eventDate: String,
    val updateDate: String
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