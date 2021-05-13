package com.kakeibo.data

import androidx.annotation.NonNull
import androidx.room.*
import com.kakeibo.data.disk.Converters
import com.kakeibo.util.UtilCurrency
import java.math.BigDecimal

@Entity(tableName = "items")
class Item {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ItemDBAdapter.COL_ID)
    var id: Long = 0
        private set

    @ColumnInfo(name = ItemDBAdapter.COL_AMOUNT)
    @TypeConverters(Converters::class)
    @NonNull
    var amount: BigDecimal

    @ColumnInfo(name = ItemDBAdapter.COL_CURRENCY_CODE, defaultValue = UtilCurrency.CURRENCY_NONE)
    var currencyCode = UtilCurrency.CURRENCY_NONE
        private set

    @ColumnInfo(name = ItemDBAdapter.COL_CATEGORY_CODE, defaultValue = "0")
    var categoryCode = 0
        private set

    @ColumnInfo(name = ItemDBAdapter.COL_MEMO)
    var memo = ""

    @ColumnInfo(name = ItemDBAdapter.COL_EVENT_DATE)
    var eventDate = ""
        private set

    @ColumnInfo(name = ItemDBAdapter.COL_UPDATE_DATE)
    var updateDate = ""
        private set

    constructor(
            id: Long,
            amount: BigDecimal,
            currencyCode: String,
            categoryCode: Int,
            memo: String,
            eventDate: String,
            updateDate: String) {
        this.id = id
        this.amount = amount
        this.currencyCode = currencyCode
        this.categoryCode = categoryCode
        this.memo = memo
        this.eventDate = eventDate
        this.updateDate = updateDate
    }

    /* called from TabFragment1 before getting saved  */
    @Ignore
    constructor( // without id: id is auto-increment
            amount: BigDecimal,
            currencyCode: String,
            categoryCode: Int,
            memo: String,
            eventDate: String,
            updateDate: String) {
        this.amount = amount
        this.currencyCode = currencyCode
        this.categoryCode = categoryCode
        this.memo = memo
        this.eventDate = eventDate
        this.updateDate = updateDate
    }
}