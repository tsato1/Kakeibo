//package com.kakeibo.data.disk
//
//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.kakeibo.data.SearchCriteria
//import com.kakeibo.core.data.db.SearchDBAdapter
//
//@Dao
//interface SearchCriteriaDao {
//
//    @Query("SELECT * FROM " + SearchDBAdapter.TABLE_NAME)
//    fun getAll(): LiveData<List<SearchCriteria>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insert(searchCriteria: SearchCriteria): Long
//
//    @Query("DELETE FROM " + SearchDBAdapter.TABLE_NAME)
//    fun deleteAll()
//}