package com.kakeibo.core.data.local

import androidx.room.*
import com.kakeibo.core.data.local.entities.CategoryEntity
import com.kakeibo.core.data.constants.ConstCategoryDB
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM " + ConstCategoryDB.TABLE_NAME)
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryEntity: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categoryEntities: List<CategoryEntity>)

    @Query("DELETE FROM " + ConstCategoryDB.TABLE_NAME + " WHERE " + ConstCategoryDB.COL_ID + " = :id")
    suspend fun deleteCategory(id: Long)

    @Query("DELETE FROM " + ConstCategoryDB.TABLE_NAME)
    suspend fun deleteAllCategories()

}