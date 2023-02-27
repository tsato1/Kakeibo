package com.kakeibo.core.data.local

import androidx.room.*
import com.kakeibo.core.data.local.entities.CategoryEntity
import com.kakeibo.core.data.constants.ConstCategoryDB
import com.kakeibo.core.data.constants.ConstCategoryDspDB
import com.kakeibo.util.UtilCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM " + ConstCategoryDB.TABLE_NAME)
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM " + ConstCategoryDB.TABLE_NAME +
            " WHERE " + ConstCategoryDB.COL_ID + " = :id " +
            " AND " + ConstCategoryDB.COL_CODE + " >= " + UtilCategory.CUSTOM_CATEGORY_CODE_START)
    suspend fun getCustomCategoryById(id: Long): CategoryEntity

    @Query("SELECT * FROM " + ConstCategoryDB.TABLE_NAME +
            " WHERE " + ConstCategoryDB.COL_CODE + " >= " + UtilCategory.CUSTOM_CATEGORY_CODE_START)
    fun getAllCustomCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT " +
            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_ID + "," +
            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_CODE + "," +
            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_NAME + "," +
            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_COLOR + "," +
            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_SIGN + "," +
            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_DRAWABLE + "," +
            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_IMAGE + "," +
            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_PARENT + "," +
            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_DESCRIPTION + "," +
            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_SAVED_DATE + "," +
            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_IS_SYNCED + "," +
            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_UUID +
            " FROM " + ConstCategoryDB.TABLE_NAME +
            " INNER JOIN " + ConstCategoryDspDB.TABLE_NAME +
            " ON " + ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_CODE +
            " = " + ConstCategoryDspDB.TABLE_NAME + "." + ConstCategoryDspDB.COL_CODE +
            " ORDER BY " + ConstCategoryDspDB.COL_LOCATION)
    fun getAllDisplayedCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM " + ConstCategoryDB.TABLE_NAME +
            " WHERE " + ConstCategoryDB.COL_CODE +
            " NOT IN ( SELECT " + ConstCategoryDspDB.COL_CODE + " FROM " + ConstCategoryDspDB.TABLE_NAME + ")" +
            " ORDER BY " + ConstCategoryDB.COL_CODE)
    fun getAllNotDisplayedCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryEntity: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categoryEntities: List<CategoryEntity>)

    @Query("DELETE FROM " + ConstCategoryDB.TABLE_NAME + " WHERE " + ConstCategoryDB.COL_ID + " = :id")
    suspend fun deleteCategoryById(id: Long)

    @Query("DELETE FROM " + ConstCategoryDB.TABLE_NAME)
    suspend fun deleteAllCategories()

}