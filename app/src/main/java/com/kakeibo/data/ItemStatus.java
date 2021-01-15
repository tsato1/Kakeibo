//package com.kakeibo.data;
//
//import androidx.annotation.NonNull;
//import androidx.room.ColumnInfo;
//import androidx.room.Entity;
//import androidx.room.Ignore;
//import androidx.room.PrimaryKey;
//import androidx.room.TypeConverters;
//
//import com.kakeibo.R;
//import com.kakeibo.SubApp;
//import com.kakeibo.data.disk.Converters;
//import com.kakeibo.db.ItemDBAdapter;
//import com.kakeibo.util.UtilCurrency;
//
//import java.math.BigDecimal;
//
//@Entity(tableName = "items")
//public class ItemStatus {
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = ItemDBAdapter.COL_ID)
//    private long id = 1;
//
//    @ColumnInfo(name = ItemDBAdapter.COL_AMOUNT)
//    @TypeConverters({Converters.class})
//    @NonNull
//    private BigDecimal amount;
//
//    @ColumnInfo(name = ItemDBAdapter.COL_CURRENCY_CODE, defaultValue = UtilCurrency.CURRENCY_NONE)
//    @NonNull
//    private String currencyCode = UtilCurrency.CURRENCY_NONE;
//
//    @Ignore
//    private int fractionDigits;
//
//    @ColumnInfo(name = ItemDBAdapter.COL_CATEGORY_CODE, defaultValue = "0")
//    private int categoryCode = 0;
//
//    @ColumnInfo(name = ItemDBAdapter.COL_MEMO)
//    @NonNull
//    private String memo = "";
//
//    @ColumnInfo(name = ItemDBAdapter.COL_EVENT_DATE)
//    @NonNull
//    private String eventDate = "";
//
//    @ColumnInfo(name = ItemDBAdapter.COL_UPDATE_DATE)
//    @NonNull
//    private String updateDate = "";
//
//    public ItemStatus(
//            long id,
//            BigDecimal amount,
//            String currencyCode,
//            int categoryCode,
//            String memo,
//            String eventDate,
//            String updateDate) {
//        this.id = id;
//        this.amount = amount;
//        this.currencyCode = currencyCode;
//        this.categoryCode = categoryCode;
//        this.memo = memo;
//        this.eventDate = eventDate;
//        this.updateDate = updateDate;
//    }
//
//    /*** called from TabFragment1 before getting saved ***/
//    @Ignore
//    public ItemStatus (
//            // without id for id auto-increment
//            BigDecimal amount,
//            String currencyCode,
//            int categoryCode,
//            String memo,
//            String eventDate,
//            String updateDate) {
//
//        this.amount = amount;
//        this.currencyCode = currencyCode;
//        this.categoryCode = categoryCode;
//        this.memo = memo;
//        this.eventDate = eventDate;
//        this.updateDate = updateDate;
//    }
//
//    /*** called from TabFragment1 before getting saved ***/
////    @Ignore
////    public ItemStatus (
////            int id,
////            BigDecimal amount,
////            String currencyCode,
////            int fractionDigits,
////            int categoryCode,
////            String memo,
////            String eventDate,
////            String updateDate) {
////        this.id = id;
////        this.amount = amount;
////        this.currencyCode = currencyCode;
////        this.fractionDigits = fractionDigits;
////        this.categoryCode = categoryCode;
////        this.memo = memo;
////        this.eventDate = eventDate;
////        this.updateDate = updateDate;
////    }
//
//    /*** called from TabFragment2 before getting displayed ***/
//    @Ignore
//    public ItemStatus (
//            // without id for id auto-increment
//            long amount,
//            String currencyCode,
//            int fractionDigits,
//            int categoryCode,
//            String memo,
//            String eventDate,
//            String updateDate) {
//
//        if (UtilCurrency.CURRENCY_OLD.equals(currencyCode)) {
//            this.amount = BigDecimal.valueOf(amount, 0);
//        }
//
//        this.amount = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(1000), fractionDigits, BigDecimal.ROUND_HALF_UP);
//        this.fractionDigits = fractionDigits;
//        this.categoryCode = categoryCode;
//        this.memo = memo;
//        this.eventDate = eventDate;
//        this.updateDate = updateDate;
//    }
//
//    @Ignore
//    public ItemStatus (
//            long id,
//            long amount,
//            String currencyCode,
//            int fractionDigits,
//            int categoryCode,
//            String memo,
//            String eventDate,
//            String updateDate) {
//        this.id = id;
//
//        if (UtilCurrency.CURRENCY_OLD.equals(currencyCode)) {
//            this.amount = BigDecimal.valueOf(amount, 0);
//        }
//
//        this.amount = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(1000), fractionDigits, BigDecimal.ROUND_HALF_UP);
//        this.fractionDigits = fractionDigits;
//        this.categoryCode = categoryCode;
//        this.memo = memo;
//        this.eventDate = eventDate;
//        this.updateDate = updateDate;
//    }
//
//    public long getId() {
//        return this.id;
//    }
//
//    public BigDecimal getAmount() {
//        if (UtilCurrency.CURRENCY_NONE.equals(this.currencyCode)) {
//            this.amount.divide(BigDecimal.valueOf(1000),
//                    SubApp.getFractionDigits(R.string.pref_key_fraction_digits),
//                    BigDecimal.ROUND_HALF_UP);
//        }
//        return this.amount;
//    }
//
//    public String getCurrencyCode() {
//        return this.currencyCode;
//    }
//
//    public int getFractionDigits() {
//        return this.fractionDigits;
//    }
//
//    public int getCategoryCode()
//    {
//        return this.categoryCode;
//    }
//
//    public void setMemo(String memo) {
//        this.memo = memo;
//    }
//
//    public String getMemo()
//    {
//        return this.memo;
//    }
//
//    public String getEventDate()
//    {
//        return this.eventDate;
//    }
//
//    public String getUpdateDate()
//    {
//        return this.updateDate;
//    }
//}