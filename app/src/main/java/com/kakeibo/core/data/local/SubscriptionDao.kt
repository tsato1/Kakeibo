//package com.kakeibo.core.data.local
//
//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.kakeibo.data.Subscription
//
//@Dao
//interface SubscriptionDao {
//    @Query("SELECT * FROM subscriptions")
//    fun getAll(): LiveData<List<Subscription>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(comments: List<Subscription>)
//
//    @Query("DELETE FROM subscriptions")
//    fun deleteAll()
//}