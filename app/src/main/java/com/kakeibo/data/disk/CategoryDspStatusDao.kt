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

    @Query("DELETE FROM " + CategoryDspDBAdapter.TABLE_NAME)
    fun deleteAll()
}