package com.kakeibo.core.data.local.entities

import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.Expose
import com.kakeibo.core.data.local.Converters
import com.kakeibo.util.UtilCurrency
import java.math.BigDecimal

@Entity(tableName = "items")
class ItemEntity {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = ConstItemDB.COL_UUID)
    var uuid: String = ""
        private set

    @ColumnInfo(name = ConstItemDB.COL_AMOUNT)
    @TypeConverters(Converters::class)
    @NonNull
    var amount: BigDecimal
        private set

    @ColumnInfo(name = ConstItemDB.COL_CURRENCY_CODE, defaultValue = UtilCurrency.CURRENCY_NONE)
    var currencyCode = UtilCurrency.CURRENCY_NONE
        private set

    @ColumnInfo(name = ConstItemDB.COL_CATEGORY_CODE, defaultValue = "0")
    var categoryCode = 0
        private set

    @ColumnInfo(name = ConstItemDB.COL_MEMO)
    var memo = ""
        private set

    @ColumnInfo(name = ConstItemDB.COL_EVENT_DATE)
    var eventDate = ""
        private set

    @ColumnInfo(name = ConstItemDB.COL_UPDATE_DATE)
    var updateDate = ""
        private set

    @Expose(deserialize = false, serialize = false) // this val will be ignored in Retrofit communication
    @ColumnInfo(name = ConstItemDB.COL_IS_SYNCED)
    var isSynced = false

    constructor(
        uuid: String,
        amount: BigDecimal,
        currencyCode: String,
        categoryCode: Int,
        memo: String,
        eventDate: String,
        updateDate: String,
        isSynced: Boolean
    ) {
        this.uuid = uuid
        this.amount = amount
        this.currencyCode = currencyCode
        this.categoryCode = categoryCode
        this.memo = memo
        this.eventDate = eventDate
        this.updateDate = updateDate
        this.isSynced = isSynced
    }

    /* called from TabFragment1 before getting saved  */
    @Ignore
    constructor( // without id: id is auto-increment
        amount: BigDecimal,
        currencyCode: String,
        categoryCode: Int,
        memo: String,
        eventDate: String,
        updateDate: String,
        isSynced: Boolean
    ) {
        this.amount = amount
        this.currencyCode = currencyCode
        this.categoryCode = categoryCode
        this.memo = memo
        this.eventDate = eventDate
        this.updateDate = updateDate
        this.isSynced = isSynced
    }

    class InvalidItemException(message: String): Exception(message)

    class ItemNotFoundException(message: String): Exception(message)

}

//@Entity(tableName = ConstItemDB.TABLE_NAME)
//data class ItemEntity(
//    @PrimaryKey(autoGenerate = true)
//    val _id: Long? = null,
//
//    @TypeConverters(Converters::class)
//    val amount: BigDecimal,
//
//    @ColumnInfo(name = ConstItemDB.COL_CURRENCY_CODE)
//    val currencyCode: String,
//
//    @ColumnInfo(name = ConstItemDB.COL_CATEGORY_CODE)
//    val categoryCode: Int,
//
//    val memo: String,
//
//    @ColumnInfo(name = ConstItemDB.COL_EVENT_DATE)
//    val eventDate: String,
//
//    @ColumnInfo(name = ConstItemDB.COL_UPDATE_DATE)
//    val updateDate: String,
//
//    @Expose(deserialize = false, serialize = false) // this val will be ignored in Retrofit communication
//    @ColumnInfo(name = ConstItemDB.COL_IS_SYNCED)
//    val isSynced: Boolean
//) {
//
//    class InvalidItemException(message: String): Exception(message)
//
//}