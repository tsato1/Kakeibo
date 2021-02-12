package com.kakeibo.data.disk

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kakeibo.data.KkbAppStatus
import com.kakeibo.db.KkbAppDBAdapter

@Dao
interface KkbAppStatusDao {
    @Query("SELECT * FROM " + KkbAppDBAdapter.TABLE_KKBAPP + " LIMIT 1;")
    fun getFirst(): LiveData<KkbAppStatus>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(kkbAppStatus: KkbAppStatus): Long

    @Query("DELETE FROM " + KkbAppDBAdapter.TABLE_KKBAPP)
    fun deleteAll()

    @Query("UPDATE " + KkbAppDBAdapter.TABLE_KKBAPP +
            " SET " + KkbAppDBAdapter.COL_VAL_INT_2 + " = :val2;")
    fun updateVal2(val2: Int)
}