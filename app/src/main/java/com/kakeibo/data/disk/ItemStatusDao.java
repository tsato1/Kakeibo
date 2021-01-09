package com.kakeibo.data.disk;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.kakeibo.data.ItemStatus;

import java.util.List;

@Dao
interface ItemStatusDao {
    @Query("SELECT * FROM items")
    LiveData<List<ItemStatus>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ItemStatus> itemStatuses);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ItemStatus itemStatus);

    @Query("DELETE FROM items")
    void deleteAll();

    @RawQuery
    List<ItemStatus> queryItems(SupportSQLiteQuery query);
}
