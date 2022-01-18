package com.kakeibo.core.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kakeibo.feature_main.domain.models.SearchModel

@Entity(tableName = "searches")
class SearchEntity(
    @PrimaryKey(autoGenerate = true) val _id: Long,
    @ColumnInfo(name = "from_date") val fromDate: String? = null,
    @ColumnInfo(name = "to_date") val toDate: String? = null,
    @ColumnInfo(name = "from_amount") val fromAmount: String? = null,
    @ColumnInfo(name = "to_amount") val toAmount: String? = null,
    @ColumnInfo(name = "category_code") val categoryCode: Int? = null,
    val memo: String? = null,
    @ColumnInfo(name = "from_update_date") val fromUpdateDate: String? = null,
    @ColumnInfo(name = "to_update_date") val toUpdateDate: String? = null
) {

    fun toSearchModel(): SearchModel {
        return SearchModel(
            _id = _id,
            fromDate = fromDate,
            toDate = toDate,
            fromAmount = fromAmount,
            toAmount = toAmount,
            categoryCode = categoryCode,
            memo = memo,
            fromUpdateDate = fromUpdateDate,
            toUpdateDate = toUpdateDate
        )
    }

}