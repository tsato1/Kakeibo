//package com.kakeibo.data.disk;
//
//import androidx.lifecycle.LiveData;
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.kakeibo.data.SubscriptionStatus;
//
//import java.util.List;
//
//@Dao
//interface SubscriptionStatusDao {
//    @Query("SELECT * FROM subscriptions")
//    LiveData<List<SubscriptionStatus>> getAll();
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertAll(List<SubscriptionStatus> comments);
//
//    @Query("DELETE FROM subscriptions")
//    void deleteAll();
//}
