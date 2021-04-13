package com.kakeibo.data.disk

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kakeibo.data.KkbApp
import com.kakeibo.db.KkbAppDBAdapter

@Dao
interface KkbAppDao {
    @Query("SELECT * FROM " + KkbAppDBAdapter.TABLE_KKBAPP + " LIMIT 1;")
    fun getFirst(): LiveData<KkbApp>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(kkbApp: KkbApp): Long

    @Query("DELETE FROM " + KkbAppDBAdapter.TABLE_KKBAPP)
    fun deleteAll()

    @Query("UPDATE " + KkbAppDBAdapter.TABLE_KKBAPP +
            " SET " + KkbAppDBAdapter.COL_VAL_INT_2 + " = :val2;")
    fun updateVal2(val2: Int)
}