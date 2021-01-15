package com.kakeibo.data.disk

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.kakeibo.data.ItemStatus

@Dao
interface ItemStatusDao {
    @Query("SELECT * FROM items")
    fun getAll(): LiveData<List<ItemStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(itemStatuses: List<ItemStatus>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(itemStatus: ItemStatus): Long

    @Query("DELETE FROM items")
    fun deleteAll()

    @RawQuery
    fun queryItems(query: SupportSQLiteQuery): List<ItemStatus>
}