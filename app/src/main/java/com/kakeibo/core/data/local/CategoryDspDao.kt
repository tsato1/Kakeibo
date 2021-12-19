package com.kakeibo.core.data.local

import androidx.room.*
import com.kakeibo.core.data.constants.ConstCategoryDB
import com.kakeibo.core.data.constants.ConstCategoryDspDB
import com.kakeibo.core.data.local.entities.CategoryDspEntity
import com.kakeibo.core.data.local.entities.CategoryEntity
import com.kakeibo.feature_main.data.sources.local.entities.DisplayedCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDspDao {

//    @Query("SELECT * FROM " + ConstCategoryDB.TABLE_NAME)
//    fun getAllCategoryDsps(): Flow<List<CategoryEntity>>

    @Transaction
    @Query("SELECT * FROM " + ConstCategoryDspDB.TABLE_NAME)
    suspend fun observeDisplayedCategoryList(): List<DisplayedCategoryEntity>

    @Transaction
    @Query("SELECT * FROM " + ConstCategoryDspDB.TABLE_NAME)
    fun getDisplayedCategoryList(): Flow<List<DisplayedCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM " + ConstCategoryDspDB.TABLE_NAME) //todo sqldelight
    fun getNonDisplayedCategoryList(): Flow<List<DisplayedCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryDsps(categoryDspEntityList: List<CategoryDspEntity>)

    @Query("DELETE FROM " + ConstCategoryDspDB.TABLE_NAME)
    suspend fun deleteAllCategoryDsps()


//    @Query("SELECT " +
//            ConstCategoryDspDB.TABLE_NAME + "." + ConstCategoryDspDB.COL_ID + "," +
//            ConstCategoryDspDB.TABLE_NAME + "." + ConstCategoryDspDB.COL_CODE + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_NAME + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_COLOR + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_SIGN + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_DRAWABLE + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_IMAGE + "," +
////            ConstCategoryDspDB.TABLE_NAME + "." + ConstCategoryDspDB.COL_LOCATION + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_PARENT + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_DESCRIPTION + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_SAVED_DATE + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_IS_SYNCED +
//            " FROM " + ConstCategoryDspDB.TABLE_NAME +
//            " INNER JOIN " + ConstCategoryDB.TABLE_NAME +
//            " ON " +
//            ConstCategoryDspDB.TABLE_NAME + "." + ConstCategoryDspDB.COL_CODE + "=" +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_CODE +
//            " ORDER BY " + ConstCategoryDspDB.TABLE_NAME + "." + ConstCategoryDspDB.COL_LOCATION)
//    fun getCategoriesDisplayed(): Flow<List<CategoryEntity>>
//
//    @Query("SELECT " +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_ID + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_CODE + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_NAME + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_COLOR + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_SIGN + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_DRAWABLE + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_IMAGE + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_PARENT + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_DESCRIPTION + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_SAVED_DATE + "," +
//            ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_IS_SYNCED +
//            " FROM " + ConstCategoryDB.TABLE_NAME +
//            " WHERE " + ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_CODE +
//            " NOT IN " +
//            "(" +
//            " SELECT " + ConstCategoryDspDB.COL_CODE +
//            " FROM " + ConstCategoryDspDB.TABLE_NAME +
//            ")" +
//            " ORDER BY " + ConstCategoryDB.TABLE_NAME + "." + ConstCategoryDB.COL_CODE)
//    fun getCategoriesNotDisplayed(): Flow<List<CategoryEntity>>

}