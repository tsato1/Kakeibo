package com.kakeibo.data

import androidx.annotation.NonNull
import androidx.room.*
import com.kakeibo.data.disk.Converters
import com.kakeibo.db.SearchDBAdapter
import java.math.BigDecimal

@Entity(tableName = SearchDBAdapter.TABLE_NAME)
class SearchCriteria {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SearchDBAdapter.COL_ID)
    var id: Long = 0
        private set

    @ColumnInfo(name = SearchDBAdapter.COL_FROM_DATE, defaultValue = "")
    var fromDate: String = ""
        private set

    @ColumnInfo(name = SearchDBAdapter.COL_TO_DATE, defaultValue = "")
    var toDate: String = ""
        private set

    @ColumnInfo(name = SearchDBAdapter.COL_FROM_AMOUNT)
    @TypeConverters(Converters::class)
    @NonNull
    var fromAmount: BigDecimal = BigDecimal.ZERO
        private set

    @ColumnInfo(name = SearchDBAdapter.COL_TO_AMOUNT)
    @TypeConverters(Converters::class)
    @NonNull
    var toAmount: BigDecimal = BigDecimal.ZERO
        private set

    @ColumnInfo(name = SearchDBAdapter.COL_CATEGORY_CODE, defaultValue = "0")
    var categoryCode: Int = 0
        private set

    @ColumnInfo(name = SearchDBAdapter.COL_MEMO, defaultValue = "")
    var memo = ""
        private set

    @ColumnInfo(name = SearchDBAdapter.COL_FROM_UPDATE_DATE, defaultValue = "")
    var fromUpdateDate = ""
        private set

    @ColumnInfo(name = SearchDBAdapter.COL_TO_UPDATE_DATE, defaultValue = "")
    var toUpdateDate = ""
        private set

    @ColumnInfo(name = SearchDBAdapter.COL_SAVED_DATE, defaultValue = "")
    var savedDate = ""
        private set

    constructor(
        id: Long,
        fromDate: String,
        toDate: String,
        fromAmount: BigDecimal,
        toAmount: BigDecimal,
        categoryCode: Int,
        memo: String,
        fromUpdateDate: String,
        toUpdateDate: String,
        savedDate: String
    ) {
        this.id = id
        this.fromDate = fromDate
        this.toDate = toDate
        this.fromAmount = fromAmount
        this.toAmount = toAmount
        this.categoryCode = categoryCode
        this.memo = memo
        this.fromUpdateDate = fromUpdateDate
        this.toUpdateDate = toUpdateDate
        this.savedDate = savedDate
    }

    @Ignore
    constructor(
        fromDate: String,
        toDate: String,
        fromAmount: BigDecimal,
        toAmount: BigDecimal,
        categoryCode: Int,
        memo: String,
        fromUpdateDate: String,
        toUpdateDate: String,
        savedDate: String
    ) {
        this.fromDate = fromDate
        this.toDate = toDate
        this.fromAmount = fromAmount
        this.toAmount = toAmount
        this.categoryCode = categoryCode
        this.memo = memo
        this.fromUpdateDate = fromUpdateDate
        this.toUpdateDate = toUpdateDate
        this.savedDate = savedDate
    }
}