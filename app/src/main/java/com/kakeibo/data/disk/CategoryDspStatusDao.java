package com.kakeibo.data.disk;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kakeibo.data.CategoryDspStatus;

import java.util.List;

@Dao
interface CategoryDspStatusDao {
    @Query("SELECT * FROM categories_dsp")
    LiveData<List<CategoryDspStatus>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryDspStatus> comments);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CategoryDspStatus categoryDspStatus);

    @Query("DELETE FROM categories_dsp")
    void deleteAll();
}
