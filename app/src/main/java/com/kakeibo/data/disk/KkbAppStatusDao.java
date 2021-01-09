package com.kakeibo.data.disk;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.kakeibo.data.KkbAppStatus;
import com.kakeibo.db.KkbAppDBAdapter;

@Dao
interface KkbAppStatusDao {
    @Query("SELECT * FROM " + KkbAppDBAdapter.TABLE_KKBAPP + " LIMIT 1;")
    LiveData<KkbAppStatus> getFirst();
}
