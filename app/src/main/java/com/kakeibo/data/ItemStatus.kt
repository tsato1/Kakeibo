package com.kakeibo.data

import androidx.annotation.NonNull
import androidx.room.*
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.data.disk.Converters
import com.kakeibo.util.UtilCurrency
import java.math.BigDecimal

@Entity(tableName = "items")
class ItemStatus {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ItemDBAdapter.COL_ID)
    var id: Long = 0
        private set

    @ColumnInfo(name = ItemDBAdapter.COL_AMOUNT)
    @TypeConverters(Converters::class)
    @NonNull
    private var amount: BigDecimal

    @ColumnInfo(name = ItemDBAdapter.COL_CURRENCY_CODE, defaultValue = UtilCurrency.CURRENCY_NONE)
    var currencyCode = UtilCurrency.CURRENCY_NONE
        private set

    @Ignore
    var fractionDigits = 0
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

    /*** called from TabFragment1 before getting saved  */
    @Ignore
    constructor( // without id: id auto-increment
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

    fun getAmount(): BigDecimal {
        if (UtilCurrency.CURRENCY_NONE == currencyCode) {
            amount.divide(BigDecimal.valueOf(1000),
                    SubApp.getFractionDigits(R.string.pref_key_fraction_digits),
                    BigDecimal.ROUND_HALF_UP)
        }
        return amount
    }
}