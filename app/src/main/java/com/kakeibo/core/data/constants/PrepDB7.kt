package com.kakeibo.core.data.constants

import android.annotation.SuppressLint
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilCurrency
import kotlin.math.absoluteValue

@SuppressLint("Range")
object PrepDB7 {
    fun migrate_1_2(database: SupportSQLiteDatabase) {}

    fun migrate_2_3(database: SupportSQLiteDatabase) {}

    fun migrate_3_4(database: SupportSQLiteDatabase) {}

    fun migrate_4_5(database: SupportSQLiteDatabase) {
        /*
         * Category table
         * No categories table at db version == 4
         * creating one for db version == 5 so that it can be modified on migrate_5_7
         */
        database.execSQL("CREATE TABLE " + ConstCategoryDB.TABLE_NAME + " (" +
                ConstCategoryDB.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ConstCategoryDB.COL_CODE + " INTEGER NOT NULL DEFAULT 0," +
                ConstCategoryDB.COL_COLOR + " INTEGER NOT NULL DEFAULT 0," +
                ConstCategoryDB.COL_DRAWABLE + " TEXT NOT NULL DEFAULT ''," +
                ConstCategoryDB.COL_LOCATION + " INTEGER NOT NULL DEFAULT 0," +
                ConstCategoryDB.COL_DESCRIPTION + " TEXT NOT NULL DEFAULT ''," +
                ConstCategoryDB.COL_SAVED_DATE + " TEXT NOT NULL DEFAULT '');")
        var location = 0
        for (code in 0..15) {
            location = when (code) {
                8 -> 12
                9 -> 13
                10 -> 14
                11 -> 15
                12 -> 8
                13 -> 9
                14 -> 10
                15 -> 11
                else -> location
            }
            database.execSQL("INSERT INTO " + ConstCategoryDB.TABLE_NAME + " (" +
                    ConstCategoryDB.COL_LOCATION + ", " +
                    ConstCategoryDB.COL_CODE + ") " +
                    " VALUES(" + location + ", " + code + ")")
            location++
        }
    }

    fun migrate_5_7(database: SupportSQLiteDatabase) {
        /*
         * KkbApp table
         * (copied from migrate_6_7())
         */
        database.execSQL("ALTER TABLE " + ConstKkbAppDB.TABLE_KKBAPP + " RENAME TO " + ConstKkbAppDB.TABLE_KKBAPP + "_old;")
        database.execSQL("CREATE TABLE " + ConstKkbAppDB.TABLE_KKBAPP + " (" +
                ConstKkbAppDB.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ConstKkbAppDB.COL_NAME + " TEXT NOT NULL DEFAULT ''," +
                ConstKkbAppDB.COL_TYPE + " TEXT NOT NULL DEFAULT ''," +
                ConstKkbAppDB.COL_UPDATE_DATE + " TEXT NOT NULL DEFAULT ''," +
                ConstKkbAppDB.COL_VAL_INT_1 + " INTEGER NOT NULL DEFAULT 0," +
                ConstKkbAppDB.COL_VAL_INT_2 + " INTEGER NOT NULL DEFAULT -1," +
                ConstKkbAppDB.COL_VAL_INT_3 + " INTEGER NOT NULL DEFAULT 0," +
                ConstKkbAppDB.COL_VAL_STR_1 + " TEXT NOT NULL DEFAULT ''," +
                ConstKkbAppDB.COL_VAL_STR_2 + " TEXT NOT NULL DEFAULT ''," +
                ConstKkbAppDB.COL_VAL_STR_3 + " TEXT NOT NULL DEFAULT '');")
        database.execSQL("INSERT INTO " + ConstKkbAppDB.TABLE_KKBAPP + " (" +
                ConstKkbAppDB.COL_ID + "," +
                ConstKkbAppDB.COL_NAME + "," +
                ConstKkbAppDB.COL_TYPE + "," +  // for old items, take over the previous currency_code
                ConstKkbAppDB.COL_UPDATE_DATE + "," +
                ConstKkbAppDB.COL_VAL_INT_1 + "," +
                ConstKkbAppDB.COL_VAL_INT_2 + "," +
                ConstKkbAppDB.COL_VAL_INT_3 + "," +
                ConstKkbAppDB.COL_VAL_STR_1 + "," +
                ConstKkbAppDB.COL_VAL_STR_2 + "," +
                ConstKkbAppDB.COL_VAL_STR_3 + ")" +
                " SELECT " +
                ConstKkbAppDB.COL_ID + "," +
                ConstKkbAppDB.COL_NAME + "," +
                ConstKkbAppDB.COL_TYPE + "," +
                ConstKkbAppDB.COL_UPDATE_DATE + "," +
                ConstKkbAppDB.COL_VAL_INT_1 + "," +
                ConstKkbAppDB.COL_VAL_INT_2 + "," +
                ConstKkbAppDB.COL_VAL_INT_3 + "," +
                ConstKkbAppDB.COL_VAL_STR_1 + "," +
                ConstKkbAppDB.COL_VAL_STR_2 + "," +
                ConstKkbAppDB.COL_VAL_STR_3 + " FROM " + ConstKkbAppDB.TABLE_KKBAPP + "_old;")
        database.execSQL("DROP TABLE " + ConstKkbAppDB.TABLE_KKBAPP + "_old;")

        /*
         * subscriptions table (copied from migrate_6_7())
         */
        database.execSQL("CREATE TABLE IF NOT EXISTS subscriptions (primaryKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, subscriptionStatusJson TEXT, subAlreadyOwned INTEGER NOT NULL, isLocalPurchase INTEGER NOT NULL, sku TEXT, purchaseToken TEXT, isEntitlementActive INTEGER NOT NULL, willRenew INTEGER NOT NULL, activeUntilMillisec INTEGER NOT NULL, isFreeTrial INTEGER NOT NULL, isGracePeriod INTEGER NOT NULL, isAccountHold INTEGER NOT NULL)")

        /* items table */
        database.execSQL("ALTER TABLE " + ConstItemDB.TABLE_NAME + " RENAME TO " + ConstItemDB.TABLE_NAME + "_old;")
        database.execSQL("CREATE TABLE " + ConstItemDB.TABLE_NAME + " (" +
                ConstItemDB.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ConstItemDB.COL_AMOUNT + " INTEGER NOT NULL," +
                ConstItemDB.COL_CURRENCY_CODE + " TEXT NOT NULL DEFAULT '" + UtilCurrency.CURRENCY_NONE + "', " +  // for new items, currency_code is none
                ConstItemDB.COL_CATEGORY_CODE + " INTEGER NOT NULL DEFAULT 0," +
                ConstItemDB.COL_MEMO + " TEXT NOT NULL," +
                ConstItemDB.COL_EVENT_DATE + " TEXT NOT NULL," +
                ConstItemDB.COL_UPDATE_DATE + " TEXT NOT NULL);")
        database.execSQL("INSERT INTO " + ConstItemDB.TABLE_NAME + " (" +
                ConstItemDB.COL_ID + "," +
                ConstItemDB.COL_AMOUNT + "," +
                ConstItemDB.COL_CURRENCY_CODE + "," +  // for old items, take over the previous currency_code
                ConstItemDB.COL_CATEGORY_CODE + "," +
                ConstItemDB.COL_MEMO + "," +
                ConstItemDB.COL_EVENT_DATE + "," +
                ConstItemDB.COL_UPDATE_DATE + ") " +
                " SELECT " +
                ConstItemDB.COL_ID + "," +
                "CAST (" + ConstItemDB.COL_AMOUNT + " AS INTEGER)," +
                ConstItemDB.COL_CURRENCY_CODE + "," +
                ConstItemDB.COL_CATEGORY_CODE + "," +
                ConstItemDB.COL_MEMO + "," +
                ConstItemDB.COL_EVENT_DATE + "," +
                ConstItemDB.COL_UPDATE_DATE + " FROM " + ConstItemDB.TABLE_NAME + "_old;")
        database.execSQL("DROP TABLE " + ConstItemDB.TABLE_NAME + "_old;")
        val cursorItem = database.query("SELECT * FROM " + ConstItemDB.TABLE_NAME)
        if (cursorItem.moveToFirst()) {
            do {
                val id = cursorItem.getInt(cursorItem.getColumnIndex(ConstItemDB.COL_ID))
                val code = cursorItem.getInt(cursorItem.getColumnIndex(ConstItemDB.COL_CATEGORY_CODE))
                val amount = cursorItem.getInt(cursorItem.getColumnIndex(ConstItemDB.COL_AMOUNT))
                if (code != 0) { // set value to negative only for expense // category_code == 0 is Income
                    database.execSQL(
                            ("UPDATE " + ConstItemDB.TABLE_NAME +
                                    " SET " + ConstItemDB.COL_AMOUNT + "=-" + amount + // set to negative
                                    " WHERE " + ConstItemDB.COL_ID + "=" + id))
                }
            } while (cursorItem.moveToNext())
        }

        /*
         * CategoryDsp table
         * NO CategoryDsp table at db version = 5
         */
        database.execSQL("CREATE TABLE " + ConstCategoryDspDB.TABLE_NAME + " (" +
                ConstCategoryDspDB.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ConstCategoryDspDB.COL_LOCATION + " INTEGER NOT NULL DEFAULT 0," +
                ConstCategoryDspDB.COL_CODE + " INTEGER NOT NULL DEFAULT 0);")
        database.execSQL("INSERT INTO " + ConstCategoryDspDB.TABLE_NAME + " (" +
                ConstCategoryDspDB.COL_ID + "," +
                ConstCategoryDspDB.COL_LOCATION + "," +
                ConstCategoryDspDB.COL_CODE + ") " +
                " SELECT " +
                ConstCategoryDB.COL_ID + "," +
                ConstCategoryDB.COL_CODE + "," +
                ConstCategoryDB.COL_LOCATION + " FROM " + ConstCategoryDB.TABLE_NAME)

        /* Category table  */
        database.execSQL("ALTER TABLE " + ConstCategoryDB.TABLE_NAME + " RENAME TO " + ConstCategoryDB.TABLE_NAME + "_old;")
        database.execSQL("CREATE TABLE " + ConstCategoryDB.TABLE_NAME + " (" +
                ConstCategoryDB.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ConstCategoryDB.COL_CODE + " INTEGER NOT NULL DEFAULT 0," +
                ConstCategoryDB.COL_NAME + " TEXT NOT NULL DEFAULT ''," +
                ConstCategoryDB.COL_COLOR + " INTEGER NOT NULL DEFAULT 0," +
                ConstCategoryDB.COL_SIGN + " INTEGER NOT NULL DEFAULT 0," +
                ConstCategoryDB.COL_DRAWABLE + " TEXT NOT NULL DEFAULT ''," +
                ConstCategoryDB.COL_IMAGE + " BLOB DEFAULT NULL, " +
                ConstCategoryDB.COL_PARENT + " INTEGER NOT NULL DEFAULT -1," +
                ConstCategoryDB.COL_DESCRIPTION + " TEXT NOT NULL DEFAULT ''," +
                ConstCategoryDB.COL_SAVED_DATE + " TEXT NOT NULL DEFAULT '');")
        database.execSQL("INSERT INTO " + ConstCategoryDB.TABLE_NAME + " (" +
                ConstCategoryDB.COL_ID + "," +
                ConstCategoryDB.COL_CODE + "," +
                // not inserting for name
                ConstCategoryDB.COL_COLOR + "," +
                // not inserting for significance because it cannot be specified in the version 5
                ConstCategoryDB.COL_DRAWABLE + "," +
                // not inserting for image because it cannot be specified in the version 5
                // not inserting for parent because it cannot be specified in the version 5
                ConstCategoryDB.COL_DESCRIPTION + "," +
                ConstCategoryDB.COL_SAVED_DATE + ") " +
                " SELECT " +
                ConstCategoryDB.COL_ID + "," +
                ConstCategoryDB.COL_CODE + "," +
                ConstCategoryDB.COL_COLOR + "," +
                "''," +  // COL_DRAWABLE is now TEXT
                ConstCategoryDB.COL_DESCRIPTION + "," +
                ConstCategoryDB.COL_SAVED_DATE + " FROM " + ConstCategoryDB.TABLE_NAME + "_old;")
        database.execSQL("DROP TABLE " + ConstCategoryDB.TABLE_NAME + "_old;")
        val cursor = database.query(
                ("SELECT * FROM " + ConstCategoryDB.TABLE_NAME +
                        " WHERE " + ConstCategoryDB.COL_CODE + "<" + UtilCategory.CUSTOM_CATEGORY_CODE_START +
                        " ORDER BY " + ConstCategoryDB.COL_CODE))
        if (cursor.moveToFirst()) {
            do {
                val code = cursor.getInt(cursor.getColumnIndex(ConstCategoryDB.COL_CODE))
                var categoryName: String
                var drawableName: String
                var color: Int
                if (code == 0) {
                    categoryName = "INCOME"
                    drawableName = "ic_category_income"
                    color = 1
                } else if (code == 1) {
                    categoryName = "COMM"
                    drawableName = "ic_category_comm"
                    color = 0
                } else if (code == 2) {
                    categoryName = "MEAL"
                    drawableName = "ic_category_meal"
                    color = 0
                } else if (code == 3) {
                    categoryName = "UTIL"
                    drawableName = "ic_category_util"
                    color = 0
                } else if (code == 4) {
                    categoryName = "HEALTH"
                    drawableName = "ic_category_health"
                    color = 0
                } else if (code == 5) {
                    categoryName = "EDU"
                    drawableName = "ic_category_edu"
                    color = 0
                } else if (code == 6) {
                    categoryName = "CLOTH"
                    drawableName = "ic_category_cloth"
                    color = 0
                } else if (code == 7) {
                    categoryName = "TRANS"
                    drawableName = "ic_category_trans"
                    color = 0
                } else if (code == 8) {
                    categoryName = "ENT"
                    drawableName = "ic_category_ent"
                    color = 0
                } else if (code == 9) {
                    categoryName = "INS"
                    drawableName = "ic_category_ins"
                    color = 0
                } else if (code == 10) {
                    categoryName = "TAX"
                    drawableName = "ic_category_tax"
                    color = 0
                } else if (code == 11) {
                    categoryName = "OTHER"
                    drawableName = "ic_category_other"
                    color = 0
                } else if (code == 12) {
                    categoryName = "PET"
                    drawableName = "ic_category_pet"
                    color = 0
                } else if (code == 13) {
                    categoryName = "SOCIAL"
                    drawableName = "ic_category_social"
                    color = 0
                } else if (code == 14) {
                    categoryName = "COSME"
                    drawableName = "ic_category_cosme"
                    color = 0
                } else if (code == 15) {
                    categoryName = "HOUSNG"
                    drawableName = "ic_category_housing"
                    color = 0
                } else {
                    continue
                }
                database.execSQL("UPDATE " + ConstCategoryDB.TABLE_NAME +
                                " SET "
                                + ConstCategoryDB.COL_COLOR + "=" + color + ","
                                + ConstCategoryDB.COL_DRAWABLE + "='" + drawableName + "',"
                                + ConstCategoryDB.COL_NAME + "='" + categoryName + "'" +
                                " WHERE " + ConstCategoryDB.COL_CODE + "=" + code)
            } while (cursor.moveToNext())
        }

        /* CategoryLan table  */
        database.execSQL("DROP TABLE IF EXISTS " + ConstCategoryLanDB.TABLE_NAME)
    }

    fun migrate_6_7(database: SupportSQLiteDatabase) {
        /* KkbApp table  */
        database.execSQL("ALTER TABLE " + ConstKkbAppDB.TABLE_KKBAPP + " RENAME TO " + ConstKkbAppDB.TABLE_KKBAPP + "_old;")
        database.execSQL("CREATE TABLE " + ConstKkbAppDB.TABLE_KKBAPP + " (" +
                ConstKkbAppDB.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ConstKkbAppDB.COL_NAME + " TEXT NOT NULL DEFAULT ''," +
                ConstKkbAppDB.COL_TYPE + " TEXT NOT NULL DEFAULT ''," +
                ConstKkbAppDB.COL_UPDATE_DATE + " TEXT NOT NULL DEFAULT ''," +
                ConstKkbAppDB.COL_VAL_INT_1 + " INTEGER NOT NULL DEFAULT 0," +
                ConstKkbAppDB.COL_VAL_INT_2 + " INTEGER NOT NULL DEFAULT -1," +
                ConstKkbAppDB.COL_VAL_INT_3 + " INTEGER NOT NULL DEFAULT 0," +
                ConstKkbAppDB.COL_VAL_STR_1 + " TEXT NOT NULL DEFAULT ''," +
                ConstKkbAppDB.COL_VAL_STR_2 + " TEXT NOT NULL DEFAULT ''," +
                ConstKkbAppDB.COL_VAL_STR_3 + " TEXT NOT NULL DEFAULT '');")
        database.execSQL("INSERT INTO " + ConstKkbAppDB.TABLE_KKBAPP + " (" +
                ConstKkbAppDB.COL_ID + "," +
                ConstKkbAppDB.COL_NAME + "," +
                ConstKkbAppDB.COL_TYPE + "," +  // for old items, take over the previous currency_code
                ConstKkbAppDB.COL_UPDATE_DATE + "," +
                ConstKkbAppDB.COL_VAL_INT_1 + "," +
                ConstKkbAppDB.COL_VAL_INT_2 + "," +
                ConstKkbAppDB.COL_VAL_INT_3 + "," +
                ConstKkbAppDB.COL_VAL_STR_1 + "," +
                ConstKkbAppDB.COL_VAL_STR_2 + "," +
                ConstKkbAppDB.COL_VAL_STR_3 + ")" +
                " SELECT " +
                ConstKkbAppDB.COL_ID + "," +
                ConstKkbAppDB.COL_NAME + "," +
                ConstKkbAppDB.COL_TYPE + "," +
                ConstKkbAppDB.COL_UPDATE_DATE + "," +
                ConstKkbAppDB.COL_VAL_INT_1 + "," +
                ConstKkbAppDB.COL_VAL_INT_2 + "," +
                ConstKkbAppDB.COL_VAL_INT_3 + "," +
                ConstKkbAppDB.COL_VAL_STR_1 + "," +
                ConstKkbAppDB.COL_VAL_STR_2 + "," +
                ConstKkbAppDB.COL_VAL_STR_3 + " FROM " + ConstKkbAppDB.TABLE_KKBAPP + "_old;")
        database.execSQL("DROP TABLE " + ConstKkbAppDB.TABLE_KKBAPP + "_old;")

        /* subscriptions table  */
        database.execSQL("CREATE TABLE IF NOT EXISTS subscriptions (primaryKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, subscriptionStatusJson TEXT, subAlreadyOwned INTEGER NOT NULL, isLocalPurchase INTEGER NOT NULL, sku TEXT, purchaseToken TEXT, isEntitlementActive INTEGER NOT NULL, willRenew INTEGER NOT NULL, activeUntilMillisec INTEGER NOT NULL, isFreeTrial INTEGER NOT NULL, isGracePeriod INTEGER NOT NULL, isAccountHold INTEGER NOT NULL)")

        /* items table  */
        database.execSQL("ALTER TABLE " + ConstItemDB.TABLE_NAME + " RENAME TO " + ConstItemDB.TABLE_NAME + "_old;")
        database.execSQL("CREATE TABLE " + ConstItemDB.TABLE_NAME + " (" +
                ConstItemDB.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ConstItemDB.COL_AMOUNT + " INTEGER NOT NULL," +
                ConstItemDB.COL_CURRENCY_CODE + " TEXT NOT NULL DEFAULT '" + UtilCurrency.CURRENCY_NONE + "', " +  // for new items, currency_code is none
                ConstItemDB.COL_CATEGORY_CODE + " INTEGER NOT NULL DEFAULT 0," +
                ConstItemDB.COL_MEMO + " TEXT NOT NULL," +
                ConstItemDB.COL_EVENT_DATE + " TEXT NOT NULL," +
                ConstItemDB.COL_UPDATE_DATE + " TEXT NOT NULL);")
        database.execSQL("INSERT INTO " + ConstItemDB.TABLE_NAME + " (" +
                ConstItemDB.COL_ID + "," +
                ConstItemDB.COL_AMOUNT + "," +
                ConstItemDB.COL_CURRENCY_CODE + "," +  // for old items, take over the previous currency_code
                ConstItemDB.COL_CATEGORY_CODE + "," +
                ConstItemDB.COL_MEMO + "," +
                ConstItemDB.COL_EVENT_DATE + "," +
                ConstItemDB.COL_UPDATE_DATE + ") " +
                " SELECT " +
                ConstItemDB.COL_ID + "," +
                "CAST (" + ConstItemDB.COL_AMOUNT + " AS INTEGER)," +
                ConstItemDB.COL_CURRENCY_CODE + "," +
                ConstItemDB.COL_CATEGORY_CODE + "," +
                ConstItemDB.COL_MEMO + "," +
                ConstItemDB.COL_EVENT_DATE + "," +
                ConstItemDB.COL_UPDATE_DATE + " FROM " + ConstItemDB.TABLE_NAME + "_old;")
        database.execSQL("DROP TABLE " + ConstItemDB.TABLE_NAME + "_old;")
        val cursorItem = database.query(
                ("SELECT " + ConstItemDB.TABLE_NAME + "." + ConstItemDB.COL_ID + ", " +
                        ConstItemDB.COL_AMOUNT + ", " +
                        ConstItemDB.COL_CATEGORY_CODE + ", " +
                        ConstCategoryDB.COL_COLOR +
                        " FROM " + ConstItemDB.TABLE_NAME +
                        " INNER JOIN " + ConstCategoryDB.TABLE_NAME +
                        " ON " + ConstCategoryDB.COL_CODE + "=" + ConstItemDB.COL_CATEGORY_CODE +
                        " WHERE " + ConstCategoryDB.COL_COLOR + "=" + UtilCategory.CATEGORY_COLOR_EXPENSE))
        if (cursorItem.moveToFirst()) {
            do {
                val id = cursorItem.getInt(cursorItem.getColumnIndex(ConstItemDB.COL_ID))
                val amount = cursorItem.getLong(cursorItem.getColumnIndex(ConstItemDB.COL_AMOUNT))
                database.execSQL("UPDATE " + ConstItemDB.TABLE_NAME +
                                " SET " + ConstItemDB.COL_AMOUNT + "=" + ((-1) * amount) + // set to negative
                                " WHERE " + ConstItemDB.COL_ID + "=" + id)
            } while (cursorItem.moveToNext())
        }

        /*
         * Category table
         * For category-related: Just changing the schema. No new column
         */
        database.execSQL("ALTER TABLE " + ConstCategoryDB.TABLE_NAME + " RENAME TO " + ConstCategoryDB.TABLE_NAME + "_old;")
        database.execSQL("CREATE TABLE " + ConstCategoryDB.TABLE_NAME + " (" +
                ConstCategoryDB.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ConstCategoryDB.COL_CODE + " INTEGER NOT NULL DEFAULT 0," +
                ConstCategoryDB.COL_NAME + " TEXT NOT NULL DEFAULT ''," +
                ConstCategoryDB.COL_COLOR + " INTEGER NOT NULL DEFAULT 0," +
                ConstCategoryDB.COL_SIGN + " INTEGER NOT NULL DEFAULT 0," +
                ConstCategoryDB.COL_DRAWABLE + " TEXT NOT NULL DEFAULT ''," +
                ConstCategoryDB.COL_IMAGE + " BLOB DEFAULT NULL, " +
                ConstCategoryDB.COL_PARENT + " INTEGER NOT NULL DEFAULT -1," +
                ConstCategoryDB.COL_DESCRIPTION + " TEXT NOT NULL DEFAULT ''," +
                ConstCategoryDB.COL_SAVED_DATE + " TEXT NOT NULL DEFAULT '');")
        database.execSQL("INSERT INTO " + ConstCategoryDB.TABLE_NAME + " (" +
                ConstCategoryDB.COL_ID + "," +
                ConstCategoryDB.COL_CODE + "," +
                ConstCategoryDB.COL_COLOR + "," +
                ConstCategoryDB.COL_SIGN + "," +
                ConstCategoryDB.COL_DRAWABLE + "," +
                ConstCategoryDB.COL_IMAGE + "," +
                ConstCategoryDB.COL_PARENT + "," +
                ConstCategoryDB.COL_DESCRIPTION + "," +
                ConstCategoryDB.COL_SAVED_DATE + ") " +
                " SELECT " +
                ConstCategoryDB.COL_ID + "," +
                "CAST (" + ConstCategoryDB.COL_CODE + " AS INTEGER)," +
                ConstCategoryDB.COL_COLOR + "," +
                ConstCategoryDB.COL_SIGN + "," +
                "''," +  // COL_DRAWABLE is now TEXT
                ConstCategoryDB.COL_IMAGE + "," +
                ConstCategoryDB.COL_PARENT + "," +
                ConstCategoryDB.COL_DESCRIPTION + "," +
                ConstCategoryDB.COL_SAVED_DATE + " FROM " + ConstCategoryDB.TABLE_NAME + "_old;")
        database.execSQL("DROP TABLE " + ConstCategoryDB.TABLE_NAME + "_old;")
        val cursor = database.query(
                ("SELECT * FROM " + ConstCategoryDB.TABLE_NAME +
                        " WHERE " + ConstCategoryDB.COL_CODE + "<" + UtilCategory.CUSTOM_CATEGORY_CODE_START +
                        " ORDER BY " + ConstCategoryDB.COL_CODE))
        if (cursor.moveToFirst()) {
            do {
                val code = cursor.getInt(cursor.getColumnIndex(ConstCategoryDB.COL_CODE))
                var categoryName: String
                var drawableName: String
                if (code == 0) {
                    categoryName = "INCOME"
                    drawableName = "ic_category_income"
                } else if (code == 1) {
                    categoryName = "COMM"
                    drawableName = "ic_category_comm"
                } else if (code == 2) {
                    categoryName = "MEAL"
                    drawableName = "ic_category_meal"
                } else if (code == 3) {
                    categoryName = "UTIL"
                    drawableName = "ic_category_util"
                } else if (code == 4) {
                    categoryName = "HEALTH"
                    drawableName = "ic_category_health"
                } else if (code == 5) {
                    categoryName = "EDU"
                    drawableName = "ic_category_edu"
                } else if (code == 6) {
                    categoryName = "CLOTH"
                    drawableName = "ic_category_cloth"
                } else if (code == 7) {
                    categoryName = "TRANS"
                    drawableName = "ic_category_trans"
                } else if (code == 8) {
                    categoryName = "ENT"
                    drawableName = "ic_category_ent"
                } else if (code == 9) {
                    categoryName = "INS"
                    drawableName = "ic_category_ins"
                } else if (code == 10) {
                    categoryName = "TAX"
                    drawableName = "ic_category_tax"
                } else if (code == 11) {
                    categoryName = "OTHER"
                    drawableName = "ic_category_other"
                } else if (code == 12) {
                    categoryName = "PET"
                    drawableName = "ic_category_pet"
                } else if (code == 13) {
                    categoryName = "SOCIAL"
                    drawableName = "ic_category_social"
                } else if (code == 14) {
                    categoryName = "COSME"
                    drawableName = "ic_category_cosme"
                } else if (code == 15) {
                    categoryName = "HOUSNG"
                    drawableName = "ic_category_housing"
                } else if (code == 16) {
                    categoryName = "EXTRA"
                    drawableName = "ic_category_bonus"
                } else if (code == 17) {
                    categoryName = "ALLOW"
                    drawableName = "ic_category_allowance"
                } else if (code == 18) {
                    categoryName = "INV"
                    drawableName = "ic_category_in_inv"
                } else if (code == 19) {
                    categoryName = "RENT"
                    drawableName = "ic_category_in_rent"
                } else if (code == 20) {
                    categoryName = "EXPENS"
                    drawableName = "ic_category_expense"
                } else if (code == 21) {
                    categoryName = "TELE"
                    drawableName = "ic_category_tele"
                } else if (code == 22) {
                    categoryName = "INV"
                    drawableName = "ic_category_ex_inv"
                } else if (code == 23) {
                    categoryName = "RENT"
                    drawableName = "ic_category_ex_rent"
                } else {
                    continue
                }
                database.execSQL(
                        ("UPDATE " + ConstCategoryDB.TABLE_NAME +
                                " SET "
                                + ConstCategoryDB.COL_DRAWABLE + "='" + drawableName + "',"
                                + ConstCategoryDB.COL_NAME + "='" + categoryName + "'" +
                                " WHERE " + ConstCategoryDB.COL_CODE + "=" + code))
            } while (cursor.moveToNext())
        }

        /* CategoryLan table  */
        val c = database.query(
                ("SELECT * FROM " + ConstCategoryLanDB.TABLE_NAME +
                        " WHERE " + ConstCategoryLanDB.COL_CODE + ">=1000"))
        if (c.moveToFirst()) {
            do {
                val code = c.getInt(c.getColumnIndex(ConstCategoryLanDB.COL_CODE))
                var name = ""
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_ARA))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_ARA))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_ENG))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_ENG))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_FRA))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_FRA))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_SPA))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_SPA))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_HIN))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_HIN))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_IND))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_IND))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_ITA))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_ITA))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_JPN))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_JPN))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_KOR))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_KOR))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_POL))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_POL))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_POR))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_POR))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_RUS))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_RUS))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_VIE))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_VIE))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_Hans))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_Hans))
                }
                if ("" != c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_Hant))) {
                    name = c.getString(c.getColumnIndex(ConstCategoryLanDB.COL_Hant))
                }
                if ("" == name) continue
                database.execSQL(
                        ("UPDATE " + ConstCategoryDB.TABLE_NAME +
                                " SET " + ConstCategoryDB.COL_NAME + "= '" + name + "'" +
                                " WHERE " + ConstCategoryDB.COL_CODE + "=" + code))
            } while (c.moveToNext())
        }
        database.execSQL("DROP TABLE IF EXISTS " + ConstCategoryLanDB.TABLE_NAME)

        /* CategoryDsp table  */
        database.execSQL("ALTER TABLE " + ConstCategoryDspDB.TABLE_NAME + " RENAME TO " + ConstCategoryDspDB.TABLE_NAME + "_old;")
        database.execSQL("CREATE TABLE IF NOT EXISTS " + ConstCategoryDspDB.TABLE_NAME + " (" +
                ConstCategoryDspDB.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ConstCategoryDspDB.COL_LOCATION + " INTEGER NOT NULL DEFAULT 0," +
                ConstCategoryDspDB.COL_CODE + " INTEGER NOT NULL DEFAULT 0);")
        database.execSQL("INSERT INTO " + ConstCategoryDspDB.TABLE_NAME + " (" +
                ConstCategoryDspDB.COL_ID + "," +
                ConstCategoryDspDB.COL_CODE + "," +
                ConstCategoryDspDB.COL_LOCATION + ") " +
                " SELECT " +
                ConstCategoryDspDB.COL_ID + "," +
                "CAST (" + ConstCategoryDspDB.COL_CODE + " AS INTEGER)," +
                ConstCategoryDspDB.COL_LOCATION + " FROM " + ConstCategoryDspDB.TABLE_NAME + "_old;")
        database.execSQL("DROP TABLE " + ConstCategoryDspDB.TABLE_NAME + "_old;")
    }

    fun migrate_7_8(database: SupportSQLiteDatabase) {
        /* subscription added isPaused column */
        database.execSQL("DROP TABLE subscriptions")
        database.execSQL("CREATE TABLE IF NOT EXISTS subscriptions (primaryKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, subscriptionStatusJson TEXT, subAlreadyOwned INTEGER NOT NULL, isLocalPurchase INTEGER NOT NULL, sku TEXT, purchaseToken TEXT, isEntitlementActive INTEGER NOT NULL, willRenew INTEGER NOT NULL, activeUntilMillisec INTEGER NOT NULL, isFreeTrial INTEGER NOT NULL, isGracePeriod INTEGER NOT NULL, isAccountHold INTEGER NOT NULL, isPaused INTEGER NOT NULL, autoResumeTimeMillis INTEGER NOT NULL)")
    }

    fun migrate_8_9(database: SupportSQLiteDatabase) {
        /*
        set the negative amount to positive
         */
        val cursorItem = database.query(
            ("SELECT " + ConstItemDB.TABLE_NAME + "." + ConstItemDB.COL_ID + ", " +
                    ConstItemDB.COL_AMOUNT + ", " +
                    ConstItemDB.COL_CATEGORY_CODE +
                    " FROM " + ConstItemDB.TABLE_NAME)
        )
        if (cursorItem.moveToFirst()) {
            do {
                val id = cursorItem.getInt(cursorItem.getColumnIndex(ConstItemDB.COL_ID))
                val amount = cursorItem.getLong(cursorItem.getColumnIndex(ConstItemDB.COL_AMOUNT))
                database.execSQL("UPDATE " + ConstItemDB.TABLE_NAME +
                        " SET " + ConstItemDB.COL_AMOUNT + "=" + (amount.absoluteValue) + // set to positive
                        " WHERE " + ConstItemDB.COL_ID + "=" + id)
            } while (cursorItem.moveToNext())
        }

        database.execSQL("CREATE TABLE " + ConstSearchDB.TABLE_NAME + " (" +
                ConstSearchDB.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ConstSearchDB.COL_FROM_DATE + " TEXT, " +
                ConstSearchDB.COL_TO_DATE + " TEXT, " +
                ConstSearchDB.COL_FROM_AMOUNT + " TEXT, " +
                ConstSearchDB.COL_TO_AMOUNT + " TEXT, " +
                ConstSearchDB.COL_CATEGORY_CODE + " INTEGER, " +
                ConstSearchDB.COL_CATEGORY_NAME + " TEXT, " +
                ConstSearchDB.COL_MEMO + " TEXT, " +
                ConstSearchDB.COL_FROM_UPDATE_DATE + " TEXT, " +
                ConstSearchDB.COL_TO_UPDATE_DATE + " TEXT)"
        )
    }

    fun migrate_9_10(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE " + ConstItemDB.TABLE_NAME +
                " ADD COLUMN " + ConstItemDB.COL_IS_SYNCED + " INTEGER NOT NULL DEFAULT 0;")
        database.execSQL("ALTER TABLE " + ConstCategoryDB.TABLE_NAME +
                " ADD COLUMN " + ConstCategoryDB.COL_IS_SYNCED + " INTEGER NOT NULL DEFAULT 0;")
        database.execSQL("CREATE TABLE " + ConstLocallyDeletedItemIdDB.TABLE_NAME +
                " (" + ConstLocallyDeletedItemIdDB.COL_DELETED_ITEM_ID + " INTEGER PRIMARY KEY NOT NULL);")
    }
}