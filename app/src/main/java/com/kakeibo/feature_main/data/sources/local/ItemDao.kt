package com.kakeibo.feature_main.data.sources.local

import androidx.room.*
import com.kakeibo.feature_main.data.sources.local.entities.DisplayedItemEntity
import com.kakeibo.core.data.local.entities.ItemEntity

@Dao
interface ItemDao {

//    @Query("SELECT * FROM " + ConstItemDB.TABLE_NAME +" ORDER BY event_date DESC")
//    fun getAllItems(): Flow<List<ItemEntity>>




    @Transaction
    @Query("SELECT * FROM " + ConstItemDB.TABLE_NAME
            + " WHERE " + ConstItemDB.COL_ID + " = :id")
    suspend fun getItemById(id: Long): DisplayedItemEntity?





    @Transaction
    @Query("SELECT * FROM " + ConstItemDB.TABLE_NAME +
            " WHERE strftime('%Y', " + ConstItemDB.COL_EVENT_DATE + ") = :y " +
            " ORDER BY " + ConstItemDB.COL_EVENT_DATE)
    suspend fun getItemsByYear(y: String): List<DisplayedItemEntity>

    @Transaction
    @Query("SELECT * FROM " + ConstItemDB.TABLE_NAME +
            " WHERE strftime('%Y-%m', " + ConstItemDB.COL_EVENT_DATE + ") = :ym " +
            " ORDER BY " + ConstItemDB.COL_EVENT_DATE)
    suspend fun getItemsByYearMonth(ym: String): List<DisplayedItemEntity>




//    @Query("SELECT * FROM " + ConstItemDB.TABLE_NAME +
//            " WHERE " + ConstItemDB.COL_IS_SYNCED + " = 0")
//    suspend fun getAllUnsyncedItemEntities(): List<ItemEntity>
//
//    @Query("SELECT * FROM " + ConstLocallyDeletedItemIdDB.TABLE_NAME)
//    suspend fun getAllLocallyDeletedItemIds(): List<LocallyDeletedItemIdEntity>
//
//    @Query("DELETE FROM " + ConstLocallyDeletedItemIdDB.TABLE_NAME +
//            " WHERE " + ConstLocallyDeletedItemIdDB.COL_DELETED_ITEM_ID + " = :deletedItemId")
//    suspend fun deleteLocallyDeletedItemId(deletedItemId: Long)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertLocallyDeletedItemId(locallyDeletedItemId: LocallyDeletedItemIdEntity)




    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(itemEntity: ItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(itemEntities: List<ItemEntity>)




    @Delete
    suspend fun deleteItem(ItemEntity: ItemEntity)

    @Query("DELETE FROM " + ConstItemDB.TABLE_NAME +
            " WHERE " + ConstItemDB.COL_ID + " = :id")
    suspend fun deleteItemById(id: Long): Int // returns the number of rows affected by the query

    @Query("DELETE FROM items")
    suspend fun deleteAllItems()

}