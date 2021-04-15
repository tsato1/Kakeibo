package com.kakeibo.data.disk

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.kakeibo.data.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM " + ItemDBAdapter.TABLE_NAME)
    fun getAll(): LiveData<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<Item>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Item): Long

    @Query("DELETE FROM " + ItemDBAdapter.TABLE_NAME)
    fun deleteAll()

    @Query("DELETE FROM " + ItemDBAdapter.TABLE_NAME +
            " WHERE " + ItemDBAdapter.COL_ID + "=" + ":id")
    fun delete(id: Long): Int

    @RawQuery
    fun queryItems(query: SupportSQLiteQuery): List<Item>

//    @Query("SELECT " + ItemDBAdapter.COL_ID + ", " +
//                "sum(" + ItemDBAdapter.COL_AMOUNT + "), " +
//                ItemDBAdapter.COL_CURRENCY_CODE + ", " +
//                ItemDBAdapter.COL_CATEGORY_CODE + ", " +
//                ItemDBAdapter.COL_MEMO + ", " +
//                ItemDBAdapter.COL_EVENT_DATE + ", " +
//                ItemDBAdapter.COL_UPDATE_DATE +
//            " FROM " + ItemDBAdapter.TABLE_NAME +
//            " WHERE strftime('%Y', " + ItemDBAdapter.COL_EVENT_DATE + ") = :y " +
//            " AND " + ItemDBAdapter.COL_AMOUNT + ">0" +
//            " GROUP BY strftime('%Y-%m', " + ItemDBAdapter.COL_EVENT_DATE + ")" +
//            " ORDER BY " + ItemDBAdapter.COL_EVENT_DATE) //todo
    @Query("SELECT * FROM " + ItemDBAdapter.TABLE_NAME +
            " WHERE strftime('%Y', " + ItemDBAdapter.COL_EVENT_DATE + ") = :y " +
            " ORDER BY " + ItemDBAdapter.COL_EVENT_DATE)
    fun getItemsByYear(y: String): LiveData<List<Item>>

//    @Query("SELECT " + ItemDBAdapter.COL_ID + ", " +
//            "sum(" + ItemDBAdapter.COL_AMOUNT + "), " +
//            ItemDBAdapter.COL_CURRENCY_CODE + ", " +
//            ItemDBAdapter.COL_CATEGORY_CODE + ", " +
//            ItemDBAdapter.COL_MEMO + ", " +
//            ItemDBAdapter.COL_EVENT_DATE + ", " +
//            ItemDBAdapter.COL_UPDATE_DATE +
//            " FROM " + ItemDBAdapter.TABLE_NAME +
//            " WHERE strftime('%Y', " + ItemDBAdapter.COL_EVENT_DATE + ") = :y " +
//            " AND " + ItemDBAdapter.COL_AMOUNT + ">0" +
//            " GROUP BY " + ItemDBAdapter.COL_CATEGORY_CODE +
//            " ORDER BY " + ItemDBAdapter.COL_EVENT_DATE)
//    fun getItemsByYearByCategory(y: String): LiveData<List<Item>>

    @Query("SELECT * FROM " + ItemDBAdapter.TABLE_NAME +
            " WHERE strftime('%Y-%m', " + ItemDBAdapter.COL_EVENT_DATE + ") = :ym " +
            " ORDER BY " + ItemDBAdapter.COL_EVENT_DATE)
    fun getItemsByMonth(ym: String): LiveData<List<Item>>
}