package com.kakeibo.data.disk

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kakeibo.data.CategoryDspStatus
import com.kakeibo.db.CategoryDBAdapter
import com.kakeibo.db.CategoryDspDBAdapter

@Dao
interface CategoryDspStatusDao {

    @Query("SELECT * FROM " + CategoryDspDBAdapter.TABLE_NAME)
    fun getAll(): LiveData<List<CategoryDspStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(dspCategories: List<CategoryDspStatus>)

    @Query("DELETE FROM categories_dsp")
    fun deleteAll()

    @Query("SELECT " + CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_CODE + " FROM " + CategoryDBAdapter.TABLE_NAME)
    fun dspCodes(): LiveData<List<Int>>
}