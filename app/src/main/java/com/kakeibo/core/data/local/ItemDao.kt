package com.kakeibo.core.data.local

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.kakeibo.core.data.constants.ConstCategoryDB
import com.kakeibo.core.data.local.entities.CategoryEntity
import com.kakeibo.core.data.local.entities.DisplayedItemEntity
import com.kakeibo.core.data.local.entities.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT " +
            ConstItemDB.TABLE_NAME + "." + ConstItemDB.COL_ID + "," +
            ConstItemDB.COL_AMOUNT + "," +
            ConstItemDB.COL_CURRENCY_CODE + "," +
            ConstItemDB.COL_CATEGORY_CODE + "," +
            ConstItemDB.COL_MEMO + "," +
            ConstItemDB.COL_EVENT_DATE + "," +
            ConstItemDB.COL_UPDATE_DATE + "," +
            ConstCategoryDB.COL_NAME + "," +
            ConstCategoryDB.COL_COLOR + "," +
            ConstCategoryDB.COL_SIGN + "," +
            ConstCategoryDB.COL_DRAWABLE + "," +
            ConstCategoryDB.COL_IMAGE + "," +
            ConstCategoryDB.COL_PARENT + "," +
            ConstCategoryDB.COL_DESCRIPTION + "," +
            ConstCategoryDB.COL_SAVED_DATE +
            " FROM " + ConstItemDB.TABLE_NAME +
            " INNER JOIN " + ConstCategoryDB.TABLE_NAME +
            " ON " + ConstItemDB.COL_CATEGORY_CODE + " = " + ConstCategoryDB.COL_CODE +
            " ORDER BY event_date DESC")
    suspend fun getAllItems(): List<DisplayedItemEntity>

    @Query("SELECT " +
            ConstItemDB.TABLE_NAME + "." + ConstItemDB.COL_ID + "," +
            ConstItemDB.COL_AMOUNT + "," +
            ConstItemDB.COL_CURRENCY_CODE + "," +
            ConstItemDB.COL_CATEGORY_CODE + "," +
            ConstItemDB.COL_MEMO + "," +
            ConstItemDB.COL_EVENT_DATE + "," +
            ConstItemDB.COL_UPDATE_DATE + "," +
            ConstCategoryDB.COL_NAME + "," +
            ConstCategoryDB.COL_COLOR + "," +
            ConstCategoryDB.COL_SIGN + "," +
            ConstCategoryDB.COL_DRAWABLE + "," +
            ConstCategoryDB.COL_IMAGE + "," +
            ConstCategoryDB.COL_PARENT + "," +
            ConstCategoryDB.COL_DESCRIPTION + "," +
            ConstCategoryDB.COL_SAVED_DATE +
            " FROM " + ConstItemDB.TABLE_NAME +
            " INNER JOIN " + ConstCategoryDB.TABLE_NAME +
            " ON " + ConstItemDB.COL_CATEGORY_CODE + " = " + ConstCategoryDB.COL_CODE +
            " WHERE " + ConstItemDB.TABLE_NAME + "." + ConstItemDB.COL_ID + " = :id")
    suspend fun getItemById(id: Long): DisplayedItemEntity?

//    @Query("SELECT " +
//            ConstItemDB.TABLE_NAME + "." + ConstItemDB.COL_ID + "," +
//            ConstItemDB.COL_AMOUNT + "," +
//            ConstItemDB.COL_CURRENCY_CODE + "," +
//            ConstItemDB.COL_CATEGORY_CODE + "," +
//            ConstItemDB.COL_MEMO + "," +
//            ConstItemDB.COL_EVENT_DATE + "," +
//            ConstItemDB.COL_UPDATE_DATE + "," +
//            ConstCategoryDB.COL_NAME + "," +
//            ConstCategoryDB.COL_COLOR + "," +
//            ConstCategoryDB.COL_SIGN + "," +
//            ConstCategoryDB.COL_DRAWABLE + "," +
//            ConstCategoryDB.COL_IMAGE + "," +
//            ConstCategoryDB.COL_PARENT + "," +
//            ConstCategoryDB.COL_DESCRIPTION + "," +
//            ConstCategoryDB.COL_SAVED_DATE +
//            " FROM " + ConstItemDB.TABLE_NAME +
//            " INNER JOIN " + ConstCategoryDB.TABLE_NAME +
//            " ON " + ConstItemDB.COL_CATEGORY_CODE + " = " + ConstCategoryDB.COL_CODE +
//            " WHERE strftime('%Y', " + ConstItemDB.COL_EVENT_DATE + ") = :y " +
//            " ORDER BY " + ConstItemDB.COL_EVENT_DATE)
//    fun getItemsInYear(y: String): Flow<List<DisplayedItemEntity>>

//    @Query("SELECT " +
//            ConstItemDB.TABLE_NAME + "." + ConstItemDB.COL_ID + "," +
//            ConstItemDB.COL_AMOUNT + "," +
//            ConstItemDB.COL_CURRENCY_CODE + "," +
//            ConstItemDB.COL_CATEGORY_CODE + "," +
//            ConstItemDB.COL_MEMO + "," +
//            ConstItemDB.COL_EVENT_DATE + "," +
//            ConstItemDB.COL_UPDATE_DATE + "," +
//            ConstCategoryDB.COL_NAME + "," +
//            ConstCategoryDB.COL_COLOR + "," +
//            ConstCategoryDB.COL_SIGN + "," +
//            ConstCategoryDB.COL_DRAWABLE + "," +
//            ConstCategoryDB.COL_IMAGE + "," +
//            ConstCategoryDB.COL_PARENT + "," +
//            ConstCategoryDB.COL_DESCRIPTION + "," +
//            ConstCategoryDB.COL_SAVED_DATE +
//            " FROM " + ConstItemDB.TABLE_NAME +
//            " INNER JOIN " + ConstCategoryDB.TABLE_NAME +
//            " ON " + ConstItemDB.COL_CATEGORY_CODE + " = " + ConstCategoryDB.COL_CODE +
//            " WHERE strftime('%Y-%m', " + ConstItemDB.COL_EVENT_DATE + ") = :ym " +
//            " ORDER BY " + ConstItemDB.COL_EVENT_DATE)
//    fun getItemsInMonth(ym: String): Flow<List<DisplayedItemEntity>>

    @RawQuery(observedEntities = [ItemEntity::class, CategoryEntity::class])
    fun getSpecificItems(query: SupportSQLiteQuery): Flow<List<DisplayedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(itemEntity: ItemEntity): Long

    @Query("DELETE FROM " + ConstItemDB.TABLE_NAME + " WHERE " + ConstItemDB.COL_ID + " = :id")
    suspend fun deleteItemById(id: Long): Int // returns the number of rows affected by the query

    @Query("DELETE FROM items")
    suspend fun deleteAllItems(): Int





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

}