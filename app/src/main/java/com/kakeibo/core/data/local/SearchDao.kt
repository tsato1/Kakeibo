package com.kakeibo.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kakeibo.core.data.constants.ConstSearchDB
import com.kakeibo.core.data.local.entities.SearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Query("SELECT * FROM " + ConstSearchDB.TABLE_NAME)
    fun getAllSearches(): Flow<List<SearchEntity>>

    @Query("SELECT * FROM " + ConstSearchDB.TABLE_NAME + " WHERE _id = :id")
    suspend fun getSearchById(id: Long): SearchEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(searchEntity: SearchEntity): Long

    @Query("DELETE FROM " + ConstSearchDB.TABLE_NAME)
    suspend fun deleteAllSearches()

    @Query("DELETE FROM " + ConstSearchDB.TABLE_NAME + " WHERE _id = :id")
    suspend fun deleteSearchById(id: Long)

}