package com.kakeibo.core.data.local

import androidx.room.*
import com.kakeibo.core.data.constants.ConstKkbAppDB
import com.kakeibo.core.data.local.entities.KkbAppEntity

@Dao
interface KkbAppDao {
    @Query("SELECT * FROM " + ConstKkbAppDB.TABLE_KKBAPP + " LIMIT 1;")
    suspend fun getFirst(): KkbAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(kkbAppEntity: KkbAppEntity): Long

    @Query("DELETE FROM " + ConstKkbAppDB.TABLE_KKBAPP)
    suspend fun deleteAll()

    @Query("UPDATE " + ConstKkbAppDB.TABLE_KKBAPP +
            " SET " + ConstKkbAppDB.COL_VAL_INT_2 + " = :val2;")
    suspend fun updateVal2(val2: Int)
}