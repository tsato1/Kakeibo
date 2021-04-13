package com.kakeibo.data.disk

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.kakeibo.data.ItemStatus

@Dao
interface ItemStatusDao {
    @Query("SELECT * FROM " + ItemDBAdapter.TABLE_NAME)
    fun getAll(): LiveData<List<ItemStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(itemStatuses: List<ItemStatus>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(itemStatus: ItemStatus): Long

    @Query("DELETE FROM " + ItemDBAdapter.TABLE_NAME)
    fun deleteAll()

    @Query("DELETE FROM " + ItemDBAdapter.TABLE_NAME +
            " WHERE " + ItemDBAdapter.COL_ID + "=" + ":id")
    fun delete(id: Long): Int

    @RawQuery
    fun queryItems(query: SupportSQLiteQuery): List<ItemStatus>

    @Query("SELECT * FROM " + ItemDBAdapter.TABLE_NAME +
            " WHERE strftime('%Y', " + ItemDBAdapter.COL_EVENT_DATE + ") = :y " +
            " ORDER BY " + ItemDBAdapter.COL_EVENT_DATE)
    fun getItemsByYear(y: String): LiveData<List<ItemStatus>>

    @Query("SELECT * FROM " + ItemDBAdapter.TABLE_NAME +
            " WHERE strftime('%Y-%m', " + ItemDBAdapter.COL_EVENT_DATE + ") = :ym " +
            " ORDER BY " + ItemDBAdapter.COL_EVENT_DATE)
    fun getItemsByMonth(ym: String): LiveData<List<ItemStatus>>
}