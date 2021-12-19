package com.kakeibo.feature_settings.data.sources.local

import androidx.room.*
import com.kakeibo.core.data.constants.ConstCategoryDB
import com.kakeibo.core.data.local.entities.CategoryEntity
import com.kakeibo.util.UtilCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomCategoryDao {

    @Query("SELECT * FROM " + ConstCategoryDB.TABLE_NAME +
            " WHERE " + ConstCategoryDB.COL_ID + " = :id " +
            " AND " + ConstCategoryDB.COL_CODE + " >= " + UtilCategory.CUSTOM_CATEGORY_CODE_START)
    suspend fun getCustomCategoryById(id: Long): CategoryEntity

    @Query("SELECT * FROM " + ConstCategoryDB.TABLE_NAME +
            " WHERE " + ConstCategoryDB.COL_CODE + " >= " + UtilCategory.CUSTOM_CATEGORY_CODE_START)
    fun getAllCustomCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomCategory(categoryEntity: CategoryEntity): Long

    @Delete
    suspend fun deleteCustomCategory(categoryEntity: CategoryEntity)

}