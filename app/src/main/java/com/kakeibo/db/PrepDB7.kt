package com.kakeibo.db

import android.content.ContentValues
import android.util.Log
import androidx.room.OnConflictStrategy
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kakeibo.db.PrepDB.addMoreCategories
import com.kakeibo.db.PrepDB.initCategoriesTableRevised
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilCurrency
import java.math.BigDecimal

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
        database.execSQL("CREATE TABLE " + CategoryDBAdapter.TABLE_NAME + " (" +
                CategoryDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                CategoryDBAdapter.COL_CODE + " INTEGER NOT NULL DEFAULT 0," +
                CategoryDBAdapter.COL_COLOR + " INTEGER NOT NULL DEFAULT 0," +
                CategoryDBAdapter.COL_DRAWABLE + " TEXT NOT NULL DEFAULT ''," +
                CategoryDBAdapter.COL_LOCATION + " INTEGER NOT NULL DEFAULT 0," +
                CategoryDBAdapter.COL_DESC + " TEXT NOT NULL DEFAULT ''," +
                CategoryDBAdapter.COL_SAVED_DATE + " TEXT NOT NULL DEFAULT '');")
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
            database.execSQL("INSERT INTO " + CategoryDBAdapter.TABLE_NAME + " (" +
                    CategoryDBAdapter.COL_LOCATION + ", " +
                    CategoryDBAdapter.COL_CODE + ") " +
                    " VALUES(" + location + ", " + code + ")")
            location++
        }
    }

    fun migrate_5_7(database: SupportSQLiteDatabase) {
        /*
         * KkbApp table
         * (copied from migrate_6_7())
         */
        database.execSQL("ALTER TABLE " + KkbAppDBAdapter.TABLE_KKBAPP + " RENAME TO " + KkbAppDBAdapter.TABLE_KKBAPP + "_old;")
        database.execSQL("CREATE TABLE " + KkbAppDBAdapter.TABLE_KKBAPP + " (" +
                KkbAppDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                KkbAppDBAdapter.COL_NAME + " TEXT NOT NULL DEFAULT ''," +
                KkbAppDBAdapter.COL_TYPE + " TEXT NOT NULL DEFAULT ''," +
                KkbAppDBAdapter.COL_UPDATE_DATE + " TEXT NOT NULL DEFAULT ''," +
                KkbAppDBAdapter.COL_VAL_INT_1 + " INTEGER NOT NULL DEFAULT 0," +
                KkbAppDBAdapter.COL_VAL_INT_2 + " INTEGER NOT NULL DEFAULT -1," +
                KkbAppDBAdapter.COL_VAL_INT_3 + " INTEGER NOT NULL DEFAULT 0," +
                KkbAppDBAdapter.COL_VAL_STR_1 + " TEXT NOT NULL DEFAULT ''," +
                KkbAppDBAdapter.COL_VAL_STR_2 + " TEXT NOT NULL DEFAULT ''," +
                KkbAppDBAdapter.COL_VAL_STR_3 + " TEXT NOT NULL DEFAULT '');")
        database.execSQL("INSERT INTO " + KkbAppDBAdapter.TABLE_KKBAPP + " (" +
                KkbAppDBAdapter.COL_ID + "," +
                KkbAppDBAdapter.COL_NAME + "," +
                KkbAppDBAdapter.COL_TYPE + "," +  // for old items, take over the previous currency_code
                KkbAppDBAdapter.COL_UPDATE_DATE + "," +
                KkbAppDBAdapter.COL_VAL_INT_1 + "," +
                KkbAppDBAdapter.COL_VAL_INT_2 + "," +
                KkbAppDBAdapter.COL_VAL_INT_3 + "," +
                KkbAppDBAdapter.COL_VAL_STR_1 + "," +
                KkbAppDBAdapter.COL_VAL_STR_2 + "," +
                KkbAppDBAdapter.COL_VAL_STR_3 + ")" +
                " SELECT " +
                KkbAppDBAdapter.COL_ID + "," +
                KkbAppDBAdapter.COL_NAME + "," +
                KkbAppDBAdapter.COL_TYPE + "," +
                KkbAppDBAdapter.COL_UPDATE_DATE + "," +
                KkbAppDBAdapter.COL_VAL_INT_1 + "," +
                KkbAppDBAdapter.COL_VAL_INT_2 + "," +
                KkbAppDBAdapter.COL_VAL_INT_3 + "," +
                KkbAppDBAdapter.COL_VAL_STR_1 + "," +
                KkbAppDBAdapter.COL_VAL_STR_2 + "," +
                KkbAppDBAdapter.COL_VAL_STR_3 + " FROM " + KkbAppDBAdapter.TABLE_KKBAPP + "_old;")
        database.execSQL("DROP TABLE " + KkbAppDBAdapter.TABLE_KKBAPP + "_old;")

        /*
         * subscriptions table (copied from migrate_6_7())
         */
        database.execSQL("CREATE TABLE IF NOT EXISTS subscriptions (primaryKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, subscriptionStatusJson TEXT, subAlreadyOwned INTEGER NOT NULL, isLocalPurchase INTEGER NOT NULL, sku TEXT, purchaseToken TEXT, isEntitlementActive INTEGER NOT NULL, willRenew INTEGER NOT NULL, activeUntilMillisec INTEGER NOT NULL, isFreeTrial INTEGER NOT NULL, isGracePeriod INTEGER NOT NULL, isAccountHold INTEGER NOT NULL)")

        /* items table */
        database.execSQL("ALTER TABLE " + ItemDBAdapter.TABLE_NAME + " RENAME TO " + ItemDBAdapter.TABLE_NAME + "_old;")
        database.execSQL("CREATE TABLE " + ItemDBAdapter.TABLE_NAME + " (" +
                ItemDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ItemDBAdapter.COL_AMOUNT + " INTEGER NOT NULL," +
                ItemDBAdapter.COL_CURRENCY_CODE + " TEXT NOT NULL DEFAULT '" + UtilCurrency.CURRENCY_NONE + "', " +  // for new items, currency_code is none
                ItemDBAdapter.COL_CATEGORY_CODE + " INTEGER NOT NULL DEFAULT 0," +
                ItemDBAdapter.COL_MEMO + " TEXT NOT NULL," +
                ItemDBAdapter.COL_EVENT_DATE + " TEXT NOT NULL," +
                ItemDBAdapter.COL_UPDATE_DATE + " TEXT NOT NULL);")
        database.execSQL("INSERT INTO " + ItemDBAdapter.TABLE_NAME + " (" +
                ItemDBAdapter.COL_ID + "," +
                ItemDBAdapter.COL_AMOUNT + "," +
                ItemDBAdapter.COL_CURRENCY_CODE + "," +  // for old items, take over the previous currency_code
                ItemDBAdapter.COL_CATEGORY_CODE + "," +
                ItemDBAdapter.COL_MEMO + "," +
                ItemDBAdapter.COL_EVENT_DATE + "," +
                ItemDBAdapter.COL_UPDATE_DATE + ") " +
                " SELECT " +
                ItemDBAdapter.COL_ID + "," +
                "CAST (" + ItemDBAdapter.COL_AMOUNT + " AS INTEGER)," +
                ItemDBAdapter.COL_CURRENCY_CODE + "," +
                ItemDBAdapter.COL_CATEGORY_CODE + "," +
                ItemDBAdapter.COL_MEMO + "," +
                ItemDBAdapter.COL_EVENT_DATE + "," +
                ItemDBAdapter.COL_UPDATE_DATE + " FROM " + ItemDBAdapter.TABLE_NAME + "_old;")
        database.execSQL("DROP TABLE " + ItemDBAdapter.TABLE_NAME + "_old;")
        val cursorItem = database.query("SELECT * FROM " + ItemDBAdapter.TABLE_NAME)
        if (cursorItem.moveToFirst()) {
            do {
                val id = cursorItem.getInt(cursorItem.getColumnIndex(ItemDBAdapter.COL_ID))
                val code = cursorItem.getInt(cursorItem.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE))
                val amount = cursorItem.getInt(cursorItem.getColumnIndex(ItemDBAdapter.COL_AMOUNT))
                if (code != 0) { // set value to negative only for expense // category_code == 0 is Income
                    database.execSQL(
                            ("UPDATE " + ItemDBAdapter.TABLE_NAME +
                                    " SET " + ItemDBAdapter.COL_AMOUNT + "=-" + amount + // set to negative
                                    " WHERE " + ItemDBAdapter.COL_ID + "=" + id))
                }
            } while (cursorItem.moveToNext())
        }

        /*
         * CategoryDsp table
         * NO CategoryDsp table at db version = 5
         */
        database.execSQL("CREATE TABLE " + CategoryDspDBAdapter.TABLE_NAME + " (" +
                CategoryDspDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                CategoryDspDBAdapter.COL_LOCATION + " INTEGER NOT NULL DEFAULT 0," +
                CategoryDspDBAdapter.COL_CODE + " INTEGER NOT NULL DEFAULT 0);")
        database.execSQL("INSERT INTO " + CategoryDspDBAdapter.TABLE_NAME + " (" +
                CategoryDspDBAdapter.COL_ID + "," +
                CategoryDspDBAdapter.COL_LOCATION + "," +
                CategoryDspDBAdapter.COL_CODE + ") " +
                " SELECT " +
                CategoryDBAdapter.COL_ID + "," +
                CategoryDBAdapter.COL_CODE + "," +
                CategoryDBAdapter.COL_LOCATION + " FROM " + CategoryDBAdapter.TABLE_NAME)

        /* Category table  */
        database.execSQL("ALTER TABLE " + CategoryDBAdapter.TABLE_NAME + " RENAME TO " + CategoryDBAdapter.TABLE_NAME + "_old;")
        database.execSQL("CREATE TABLE " + CategoryDBAdapter.TABLE_NAME + " (" +
                CategoryDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                CategoryDBAdapter.COL_CODE + " INTEGER NOT NULL DEFAULT 0," +
                CategoryDBAdapter.COL_NAME + " TEXT NOT NULL DEFAULT ''," +
                CategoryDBAdapter.COL_COLOR + " INTEGER NOT NULL DEFAULT 0," +
                CategoryDBAdapter.COL_SIGNIFICANCE + " INTEGER NOT NULL DEFAULT 0," +
                CategoryDBAdapter.COL_DRAWABLE + " TEXT NOT NULL DEFAULT ''," +
                CategoryDBAdapter.COL_IMAGE + " BLOB DEFAULT NULL, " +
                CategoryDBAdapter.COL_PARENT + " INTEGER NOT NULL DEFAULT -1," +
                CategoryDBAdapter.COL_DESC + " TEXT NOT NULL DEFAULT ''," +
                CategoryDBAdapter.COL_SAVED_DATE + " TEXT NOT NULL DEFAULT '');")
        database.execSQL("INSERT INTO " + CategoryDBAdapter.TABLE_NAME + " (" +
                CategoryDBAdapter.COL_ID + "," +
                CategoryDBAdapter.COL_CODE + "," +
                // not inserting for name
                CategoryDBAdapter.COL_COLOR + "," +
                // not inserting for significance because it cannot be specified in the version 5
                CategoryDBAdapter.COL_DRAWABLE + "," +
                // not inserting for image because it cannot be specified in the version 5
                // not inserting for parent because it cannot be specified in the version 5
                CategoryDBAdapter.COL_DESC + "," +
                CategoryDBAdapter.COL_SAVED_DATE + ") " +
                " SELECT " +
                CategoryDBAdapter.COL_ID + "," +
                CategoryDBAdapter.COL_CODE + "," +
                CategoryDBAdapter.COL_COLOR + "," +
                "''," +  // COL_DRAWABLE is now TEXT
                CategoryDBAdapter.COL_DESC + "," +
                CategoryDBAdapter.COL_SAVED_DATE + " FROM " + CategoryDBAdapter.TABLE_NAME + "_old;")
        database.execSQL("DROP TABLE " + CategoryDBAdapter.TABLE_NAME + "_old;")
        val cursor = database.query(
                ("SELECT * FROM " + CategoryDBAdapter.TABLE_NAME +
                        " WHERE " + CategoryDBAdapter.COL_CODE + "<" + UtilCategory.CUSTOM_CATEGORY_CODE_START +
                        " ORDER BY " + CategoryDBAdapter.COL_CODE))
        if (cursor.moveToFirst()) {
            do {
                val code = cursor.getInt(cursor.getColumnIndex(CategoryDBAdapter.COL_CODE))
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
                database.execSQL("UPDATE " + CategoryDBAdapter.TABLE_NAME +
                                " SET "
                                + CategoryDBAdapter.COL_COLOR + "=" + color + ","
                                + CategoryDBAdapter.COL_DRAWABLE + "='" + drawableName + "',"
                                + CategoryDBAdapter.COL_NAME + "='" + categoryName + "'" +
                                " WHERE " + CategoryDBAdapter.COL_CODE + "=" + code)
            } while (cursor.moveToNext())
        }

        /* CategoryLan table  */
        database.execSQL("DROP TABLE IF EXISTS " + CategoryLanDBAdapter.TABLE_NAME)
    }

    fun migrate_6_7(database: SupportSQLiteDatabase) {
        /* KkbApp table  */
        database.execSQL("ALTER TABLE " + KkbAppDBAdapter.TABLE_KKBAPP + " RENAME TO " + KkbAppDBAdapter.TABLE_KKBAPP + "_old;")
        database.execSQL("CREATE TABLE " + KkbAppDBAdapter.TABLE_KKBAPP + " (" +
                KkbAppDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                KkbAppDBAdapter.COL_NAME + " TEXT NOT NULL DEFAULT ''," +
                KkbAppDBAdapter.COL_TYPE + " TEXT NOT NULL DEFAULT ''," +
                KkbAppDBAdapter.COL_UPDATE_DATE + " TEXT NOT NULL DEFAULT ''," +
                KkbAppDBAdapter.COL_VAL_INT_1 + " INTEGER NOT NULL DEFAULT 0," +
                KkbAppDBAdapter.COL_VAL_INT_2 + " INTEGER NOT NULL DEFAULT -1," +
                KkbAppDBAdapter.COL_VAL_INT_3 + " INTEGER NOT NULL DEFAULT 0," +
                KkbAppDBAdapter.COL_VAL_STR_1 + " TEXT NOT NULL DEFAULT ''," +
                KkbAppDBAdapter.COL_VAL_STR_2 + " TEXT NOT NULL DEFAULT ''," +
                KkbAppDBAdapter.COL_VAL_STR_3 + " TEXT NOT NULL DEFAULT '');")
        database.execSQL("INSERT INTO " + KkbAppDBAdapter.TABLE_KKBAPP + " (" +
                KkbAppDBAdapter.COL_ID + "," +
                KkbAppDBAdapter.COL_NAME + "," +
                KkbAppDBAdapter.COL_TYPE + "," +  // for old items, take over the previous currency_code
                KkbAppDBAdapter.COL_UPDATE_DATE + "," +
                KkbAppDBAdapter.COL_VAL_INT_1 + "," +
                KkbAppDBAdapter.COL_VAL_INT_2 + "," +
                KkbAppDBAdapter.COL_VAL_INT_3 + "," +
                KkbAppDBAdapter.COL_VAL_STR_1 + "," +
                KkbAppDBAdapter.COL_VAL_STR_2 + "," +
                KkbAppDBAdapter.COL_VAL_STR_3 + ")" +
                " SELECT " +
                KkbAppDBAdapter.COL_ID + "," +
                KkbAppDBAdapter.COL_NAME + "," +
                KkbAppDBAdapter.COL_TYPE + "," +
                KkbAppDBAdapter.COL_UPDATE_DATE + "," +
                KkbAppDBAdapter.COL_VAL_INT_1 + "," +
                KkbAppDBAdapter.COL_VAL_INT_2 + "," +
                KkbAppDBAdapter.COL_VAL_INT_3 + "," +
                KkbAppDBAdapter.COL_VAL_STR_1 + "," +
                KkbAppDBAdapter.COL_VAL_STR_2 + "," +
                KkbAppDBAdapter.COL_VAL_STR_3 + " FROM " + KkbAppDBAdapter.TABLE_KKBAPP + "_old;")
        database.execSQL("DROP TABLE " + KkbAppDBAdapter.TABLE_KKBAPP + "_old;")

        /* subscriptions table  */
        database.execSQL("CREATE TABLE IF NOT EXISTS subscriptions (primaryKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, subscriptionStatusJson TEXT, subAlreadyOwned INTEGER NOT NULL, isLocalPurchase INTEGER NOT NULL, sku TEXT, purchaseToken TEXT, isEntitlementActive INTEGER NOT NULL, willRenew INTEGER NOT NULL, activeUntilMillisec INTEGER NOT NULL, isFreeTrial INTEGER NOT NULL, isGracePeriod INTEGER NOT NULL, isAccountHold INTEGER NOT NULL)")

        /* items table  */
        database.execSQL("ALTER TABLE " + ItemDBAdapter.TABLE_NAME + " RENAME TO " + ItemDBAdapter.TABLE_NAME + "_old;")
        database.execSQL("CREATE TABLE " + ItemDBAdapter.TABLE_NAME + " (" +
                ItemDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ItemDBAdapter.COL_AMOUNT + " INTEGER NOT NULL," +
                ItemDBAdapter.COL_CURRENCY_CODE + " TEXT NOT NULL DEFAULT '" + UtilCurrency.CURRENCY_NONE + "', " +  // for new items, currency_code is none
                ItemDBAdapter.COL_CATEGORY_CODE + " INTEGER NOT NULL DEFAULT 0," +
                ItemDBAdapter.COL_MEMO + " TEXT NOT NULL," +
                ItemDBAdapter.COL_EVENT_DATE + " TEXT NOT NULL," +
                ItemDBAdapter.COL_UPDATE_DATE + " TEXT NOT NULL);")
        database.execSQL("INSERT INTO " + ItemDBAdapter.TABLE_NAME + " (" +
                ItemDBAdapter.COL_ID + "," +
                ItemDBAdapter.COL_AMOUNT + "," +
                ItemDBAdapter.COL_CURRENCY_CODE + "," +  // for old items, take over the previous currency_code
                ItemDBAdapter.COL_CATEGORY_CODE + "," +
                ItemDBAdapter.COL_MEMO + "," +
                ItemDBAdapter.COL_EVENT_DATE + "," +
                ItemDBAdapter.COL_UPDATE_DATE + ") " +
                " SELECT " +
                ItemDBAdapter.COL_ID + "," +
                "CAST (" + ItemDBAdapter.COL_AMOUNT + " AS INTEGER)," +
                ItemDBAdapter.COL_CURRENCY_CODE + "," +
                ItemDBAdapter.COL_CATEGORY_CODE + "," +
                ItemDBAdapter.COL_MEMO + "," +
                ItemDBAdapter.COL_EVENT_DATE + "," +
                ItemDBAdapter.COL_UPDATE_DATE + " FROM " + ItemDBAdapter.TABLE_NAME + "_old;")
        database.execSQL("DROP TABLE " + ItemDBAdapter.TABLE_NAME + "_old;")
        val cursorItem = database.query(
                ("SELECT " + ItemDBAdapter.TABLE_NAME + "." + ItemDBAdapter.COL_ID + ", " +
                        ItemDBAdapter.COL_AMOUNT + ", " +
                        ItemDBAdapter.COL_CATEGORY_CODE + ", " +
                        CategoryDBAdapter.COL_COLOR +
                        " FROM " + ItemDBAdapter.TABLE_NAME +
                        " INNER JOIN " + CategoryDBAdapter.TABLE_NAME +
                        " ON " + CategoryDBAdapter.COL_CODE + "=" + ItemDBAdapter.COL_CATEGORY_CODE +
                        " WHERE " + CategoryDBAdapter.COL_COLOR + "=" + UtilCategory.CATEGORY_COLOR_EXPENSE))
        if (cursorItem.moveToFirst()) {
            do {
                val id = cursorItem.getInt(cursorItem.getColumnIndex(ItemDBAdapter.COL_ID))
                val amount = cursorItem.getLong(cursorItem.getColumnIndex(ItemDBAdapter.COL_AMOUNT))
                database.execSQL("UPDATE " + ItemDBAdapter.TABLE_NAME +
                                " SET " + ItemDBAdapter.COL_AMOUNT + "=" + ((-1) * amount) + // set to negative
                                " WHERE " + ItemDBAdapter.COL_ID + "=" + id)
            } while (cursorItem.moveToNext())
        }

        /*
         * Category table
         * For category-related: Just changing the schema. No new column
         */
        database.execSQL("ALTER TABLE " + CategoryDBAdapter.TABLE_NAME + " RENAME TO " + CategoryDBAdapter.TABLE_NAME + "_old;")
        database.execSQL("CREATE TABLE " + CategoryDBAdapter.TABLE_NAME + " (" +
                CategoryDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                CategoryDBAdapter.COL_CODE + " INTEGER NOT NULL DEFAULT 0," +
                CategoryDBAdapter.COL_NAME + " TEXT NOT NULL DEFAULT ''," +
                CategoryDBAdapter.COL_COLOR + " INTEGER NOT NULL DEFAULT 0," +
                CategoryDBAdapter.COL_SIGNIFICANCE + " INTEGER NOT NULL DEFAULT 0," +
                CategoryDBAdapter.COL_DRAWABLE + " TEXT NOT NULL DEFAULT ''," +
                CategoryDBAdapter.COL_IMAGE + " BLOB DEFAULT NULL, " +
                CategoryDBAdapter.COL_PARENT + " INTEGER NOT NULL DEFAULT -1," +
                CategoryDBAdapter.COL_DESC + " TEXT NOT NULL DEFAULT ''," +
                CategoryDBAdapter.COL_SAVED_DATE + " TEXT NOT NULL DEFAULT '');")
        database.execSQL("INSERT INTO " + CategoryDBAdapter.TABLE_NAME + " (" +
                CategoryDBAdapter.COL_ID + "," +
                CategoryDBAdapter.COL_CODE + "," +
                CategoryDBAdapter.COL_COLOR + "," +
                CategoryDBAdapter.COL_SIGNIFICANCE + "," +
                CategoryDBAdapter.COL_DRAWABLE + "," +
                CategoryDBAdapter.COL_IMAGE + "," +
                CategoryDBAdapter.COL_PARENT + "," +
                CategoryDBAdapter.COL_DESC + "," +
                CategoryDBAdapter.COL_SAVED_DATE + ") " +
                " SELECT " +
                CategoryDBAdapter.COL_ID + "," +
                "CAST (" + CategoryDBAdapter.COL_CODE + " AS INTEGER)," +
                CategoryDBAdapter.COL_COLOR + "," +
                CategoryDBAdapter.COL_SIGNIFICANCE + "," +
                "''," +  // COL_DRAWABLE is now TEXT
                CategoryDBAdapter.COL_IMAGE + "," +
                CategoryDBAdapter.COL_PARENT + "," +
                CategoryDBAdapter.COL_DESC + "," +
                CategoryDBAdapter.COL_SAVED_DATE + " FROM " + CategoryDBAdapter.TABLE_NAME + "_old;")
        database.execSQL("DROP TABLE " + CategoryDBAdapter.TABLE_NAME + "_old;")
        val cursor = database.query(
                ("SELECT * FROM " + CategoryDBAdapter.TABLE_NAME +
                        " WHERE " + CategoryDBAdapter.COL_CODE + "<" + UtilCategory.CUSTOM_CATEGORY_CODE_START +
                        " ORDER BY " + CategoryDBAdapter.COL_CODE))
        if (cursor.moveToFirst()) {
            do {
                val code = cursor.getInt(cursor.getColumnIndex(CategoryDBAdapter.COL_CODE))
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
                        ("UPDATE " + CategoryDBAdapter.TABLE_NAME +
                                " SET "
                                + CategoryDBAdapter.COL_DRAWABLE + "='" + drawableName + "',"
                                + CategoryDBAdapter.COL_NAME + "='" + categoryName + "'" +
                                " WHERE " + CategoryDBAdapter.COL_CODE + "=" + code))
            } while (cursor.moveToNext())
        }

        /* CategoryLan table  */
        val c = database.query(
                ("SELECT * FROM " + CategoryLanDBAdapter.TABLE_NAME +
                        " WHERE " + CategoryLanDBAdapter.COL_CODE + ">=1000"))
        if (c.moveToFirst()) {
            do {
                val code = c.getInt(c.getColumnIndex(CategoryLanDBAdapter.COL_CODE))
                var name = ""
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_ARA))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_ARA))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_ENG))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_ENG))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_FRA))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_FRA))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_SPA))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_SPA))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_HIN))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_HIN))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_IND))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_IND))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_ITA))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_ITA))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_JPN))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_JPN))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_KOR))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_KOR))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_POL))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_POL))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_POR))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_POR))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_RUS))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_RUS))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_VIE))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_VIE))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_Hans))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_Hans))
                }
                if ("" != c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_Hant))) {
                    name = c.getString(c.getColumnIndex(CategoryLanDBAdapter.COL_Hant))
                }
                if ("" == name) continue
                database.execSQL(
                        ("UPDATE " + CategoryDBAdapter.TABLE_NAME +
                                " SET " + CategoryDBAdapter.COL_NAME + "= '" + name + "'" +
                                " WHERE " + CategoryDBAdapter.COL_CODE + "=" + code))
            } while (c.moveToNext())
        }
        database.execSQL("DROP TABLE IF EXISTS " + CategoryLanDBAdapter.TABLE_NAME)

        /* CategoryDsp table  */
        database.execSQL("ALTER TABLE " + CategoryDspDBAdapter.TABLE_NAME + " RENAME TO " + CategoryDspDBAdapter.TABLE_NAME + "_old;")
        database.execSQL("CREATE TABLE IF NOT EXISTS " + CategoryDspDBAdapter.TABLE_NAME + " (" +
                CategoryDspDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                CategoryDspDBAdapter.COL_LOCATION + " INTEGER NOT NULL DEFAULT 0," +
                CategoryDspDBAdapter.COL_CODE + " INTEGER NOT NULL DEFAULT 0);")
        database.execSQL("INSERT INTO " + CategoryDspDBAdapter.TABLE_NAME + " (" +
                CategoryDspDBAdapter.COL_ID + "," +
                CategoryDspDBAdapter.COL_CODE + "," +
                CategoryDspDBAdapter.COL_LOCATION + ") " +
                " SELECT " +
                CategoryDspDBAdapter.COL_ID + "," +
                "CAST (" + CategoryDspDBAdapter.COL_CODE + " AS INTEGER)," +
                CategoryDspDBAdapter.COL_LOCATION + " FROM " + CategoryDspDBAdapter.TABLE_NAME + "_old;")
        database.execSQL("DROP TABLE " + CategoryDspDBAdapter.TABLE_NAME + "_old;")
    }
}