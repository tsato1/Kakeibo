package com.kakeibo.core.data.local

import androidx.room.*
import com.kakeibo.core.data.constants.ConstCategoryDspDB
import com.kakeibo.core.data.local.entities.CategoryDspEntity

@Dao
interface CategoryDspDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryDsps(categoryDspEntityList: List<CategoryDspEntity>)

    @Query("DELETE FROM " + ConstCategoryDspDB.TABLE_NAME)
    suspend fun deleteAllCategoryDsps()

}