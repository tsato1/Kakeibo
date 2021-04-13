package com.kakeibo.data.disk

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kakeibo.data.CategoryDsp
import com.kakeibo.db.CategoryDspDBAdapter

@Dao
interface CategoryDspDao {

    @Query("SELECT * FROM " + CategoryDspDBAdapter.TABLE_NAME)
    fun getAll(): LiveData<List<CategoryDsp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(dspCategories: List<CategoryDsp>)

    @Query("DELETE FROM " + CategoryDspDBAdapter.TABLE_NAME)
    fun deleteAll()
}