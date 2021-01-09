//package com.kakeibo.data.disk;
//
//import androidx.lifecycle.LiveData;
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.kakeibo.data.CategoryLanStatus;
//
//import java.util.List;
//
//@Dao
//interface CategoryLanStatusDao {
//    @Query("SELECT * FROM categories_lan")
//    LiveData<List<CategoryLanStatus>> getAll();
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertAll(List<CategoryLanStatus> categoryLanStatuses);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insert(CategoryLanStatus categoryDspStatus);
//
//    @Query("DELETE FROM categories_lan")
//    void deleteAll();
//}
