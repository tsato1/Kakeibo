package com.kakeibo.feature_main.data.sources.local.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.kakeibo.core.data.constants.ConstCategoryDB
import com.kakeibo.core.data.local.entities.CategoryEntity
import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.feature_main.domain.models.DisplayedItem

data class DisplayedItemEntity(

    @Embedded val itemEntity: ItemEntity,
    @Relation(
        parentColumn = ConstItemDB.COL_CATEGORY_CODE,
        entityColumn = ConstCategoryDB.COL_CODE
    )
    val categoryEntity: CategoryEntity

) {
    fun toDisplayedItem(): DisplayedItem {
        return DisplayedItem(
            id = itemEntity.id,
            amount = itemEntity.amount.toString(),
            currencyCode = itemEntity.currencyCode,
            categoryCode = categoryEntity.code,
            categoryColor = categoryEntity.color,
            memo = itemEntity.memo,
            eventDate = itemEntity.eventDate,
            updateDate = itemEntity.updateDate
//            isSynced = itemEntity.isSynced
        )
    }
}