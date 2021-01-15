package com.kakeibo.data

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
    var id: Long = 1
        private set

    @ColumnInfo(name = ItemDBAdapter.COL_AMOUNT)
    @TypeConverters(Converters::class)
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
    constructor( // without id for id auto-increment
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
    /*** called from TabFragment1 before getting saved  */ //    @Ignore
    //    public ItemStatus (
    //            int id,
    //            BigDecimal amount,
    //            String currencyCode,
    //            int fractionDigits,
    //            int categoryCode,
    //            String memo,
    //            String eventDate,
    //            String updateDate) {
    //        this.id = id;
    //        this.amount = amount;
    //        this.currencyCode = currencyCode;
    //        this.fractionDigits = fractionDigits;
    //        this.categoryCode = categoryCode;
    //        this.memo = memo;
    //        this.eventDate = eventDate;
    //        this.updateDate = updateDate;
    //    }
    /*** called from TabFragment2 before getting displayed  */
    @Ignore
    constructor( // without id for id auto-increment
            amount: Long,
            currencyCode: String,
            fractionDigits: Int,
            categoryCode: Int,
            memo: String,
            eventDate: String,
            updateDate: String) {
        if (UtilCurrency.CURRENCY_OLD == currencyCode) {
            this.amount = BigDecimal.valueOf(amount, 0)
        }
        this.amount = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(1000), fractionDigits, BigDecimal.ROUND_HALF_UP)
        this.fractionDigits = fractionDigits
        this.categoryCode = categoryCode
        this.memo = memo
        this.eventDate = eventDate
        this.updateDate = updateDate
    }

    @Ignore
    constructor(
            id: Long,
            amount: Long,
            currencyCode: String,
            fractionDigits: Int,
            categoryCode: Int,
            memo: String,
            eventDate: String,
            updateDate: String) {
        this.id = id
        if (UtilCurrency.CURRENCY_OLD == currencyCode) {
            this.amount = BigDecimal.valueOf(amount, 0)
        }
        this.amount = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(1000), fractionDigits, BigDecimal.ROUND_HALF_UP)
        this.fractionDigits = fractionDigits
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