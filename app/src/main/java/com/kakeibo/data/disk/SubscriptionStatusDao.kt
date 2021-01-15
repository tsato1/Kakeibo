package com.kakeibo.data.disk

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kakeibo.data.SubscriptionStatus

@Dao
interface SubscriptionStatusDao {
    @Query("SELECT * FROM subscriptions")
    fun getAll(): LiveData<List<SubscriptionStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(comments: List<SubscriptionStatus>)

    @Query("DELETE FROM subscriptions")
    fun deleteAll()
}