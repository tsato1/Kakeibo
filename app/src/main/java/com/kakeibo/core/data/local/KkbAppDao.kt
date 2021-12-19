package com.kakeibo.core.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kakeibo.core.data.constants.ConstKkbAppDB
import com.kakeibo.core.data.local.entities.KkbApp

@Dao
interface KkbAppDao {
    @Query("SELECT * FROM " + ConstKkbAppDB.TABLE_KKBAPP + " LIMIT 1;")
    fun getFirst(): LiveData<KkbApp>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(kkbApp: KkbApp): Long

    @Query("DELETE FROM " + ConstKkbAppDB.TABLE_KKBAPP)
    suspend fun deleteAll()

    @Query("UPDATE " + ConstKkbAppDB.TABLE_KKBAPP +
            " SET " + ConstKkbAppDB.COL_VAL_INT_2 + " = :val2;")
    suspend fun updateVal2(val2: Int)
}