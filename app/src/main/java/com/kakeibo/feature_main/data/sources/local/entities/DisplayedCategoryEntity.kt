package com.kakeibo.feature_main.data.sources.local.entities

import androidx.room.*
import com.kakeibo.core.data.local.entities.CategoryDspEntity
import com.kakeibo.core.data.local.entities.CategoryEntity
import com.kakeibo.feature_main.domain.models.DisplayedCategory

data class DisplayedCategoryEntity(

    @Embedded val categoryDspEntity: CategoryDspEntity,
    @Relation(
        parentColumn = "code",
        entityColumn = "code"
    )
    val categoryEntity: CategoryEntity

) {
    fun toDisplayedCategory(): DisplayedCategory {
        return DisplayedCategory(
            _id = categoryEntity._id,
            code = categoryDspEntity.code,
            color = categoryEntity.color,
            name = categoryEntity.name,
            drawable = categoryEntity.drawable,
            image = categoryEntity.image,
            location = categoryDspEntity.location
        )
    }
}