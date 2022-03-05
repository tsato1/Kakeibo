package com.kakeibo.core.data.local.entities

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import com.kakeibo.core.data.constants.ConstCategoryDB
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import java.math.BigDecimal

data class DisplayedItemEntity(
    val _id: Long?,
    val amount: BigDecimal,
    @ColumnInfo(name = ConstItemDB.COL_CURRENCY_CODE) val currencyCode: String,
    @ColumnInfo(name = ConstItemDB.COL_CATEGORY_CODE) val categoryCode: Int,
    val memo: String,
    @ColumnInfo(name = ConstItemDB.COL_EVENT_DATE) val eventDate: String,
    @ColumnInfo(name = ConstItemDB.COL_UPDATE_DATE) val updateDate: String,
    @ColumnInfo(name = ConstCategoryDB.COL_NAME) val categoryName: String,
    @ColumnInfo(name = ConstCategoryDB.COL_COLOR) val categoryColor: Int,
    @ColumnInfo(name = ConstCategoryDB.COL_SIGN) val categorySign: Int,
    @ColumnInfo(name = ConstCategoryDB.COL_DRAWABLE) val categoryDrawable: String,
    @Nullable @ColumnInfo(name = ConstCategoryDB.COL_IMAGE) val categoryImage: ByteArray?,
    @ColumnInfo(name = ConstCategoryDB.COL_PARENT) val categoryParent: Int,
    @ColumnInfo(name = ConstCategoryDB.COL_DESCRIPTION) val categoryDescription: String,
    @ColumnInfo(name = ConstCategoryDB.COL_SAVED_DATE) val categorySavedDate: String
) {

    fun toDisplayedItemModel(): DisplayedItemModel {
        return DisplayedItemModel(
            id = _id,
            amount = amount.abs().toString(),
            currencyCode = currencyCode,
            categoryCode = categoryCode,
            memo = memo,
            eventDate = eventDate,
            updateDate = updateDate,
            categoryName = categoryName,
            categoryColor = categoryColor,
            categorySign = categorySign,
            categoryDrawable = categoryDrawable,
            categoryImage = categoryImage,
            categoryParent = categoryParent,
            categoryDescription = categoryDescription,
            categorySavedDate = categorySavedDate
        )
    }

}