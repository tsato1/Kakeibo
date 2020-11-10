package com.kakeibo.data.disk;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kakeibo.data.CategoryStatus;

import java.util.List;

@Dao
interface CategoryStatusDao {
    @Query("SELECT * FROM categories")
    LiveData<List<CategoryStatus>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryStatus> comments);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CategoryStatus categoryStatus);

    @Query("DELETE FROM categories")
    void deleteAll();
}
