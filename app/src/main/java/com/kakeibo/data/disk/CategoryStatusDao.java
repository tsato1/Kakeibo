//package com.kakeibo.data.disk;
//
//import androidx.lifecycle.LiveData;
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.kakeibo.data.CategoryStatus;
//import com.kakeibo.db.CategoryDBAdapter;
//import com.kakeibo.db.CategoryLanDBAdapter;
//import com.kakeibo.util.UtilCategory;
//
//import java.util.List;
//
//@Dao
//interface CategoryStatusDao {
//
//    @Query("SELECT * FROM " + CategoryDBAdapter.TABLE_NAME)
//    LiveData<List<CategoryStatus>> getAll();
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertAll(List<CategoryStatus> categories);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long insert(CategoryStatus categoryStatus);
//
//    @Query("DELETE FROM categories")
//    void deleteAll();
//
//    @Query("SELECT "+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_ID+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_NAME+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_COLOR+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_SIGNIFICANCE+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DRAWABLE+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_IMAGE+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_PARENT+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DESC+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_SAVED_DATE+
//            " FROM " + CategoryDBAdapter.TABLE_NAME +
////            " INNER JOIN " + CategoryLanDBAdapter.TABLE_NAME +
////            " ON " +
////            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE + "=" +
////            CategoryLanDBAdapter.TABLE_NAME+"."+ CategoryLanDBAdapter.COL_CODE +
//            " ORDER BY " + CategoryDBAdapter.TABLE_NAME+"."+CategoryDBAdapter.COL_CODE)
//    LiveData<List<CategoryStatus>> getAllStatusesLiveData();
//
////    @Query("SELECT "+
////            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_ID+","+
////            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE+","+
////            ":langCode,"+
////            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_COLOR+","+
////            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_SIGNIFICANCE+","+
////            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DRAWABLE+","+
////            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_IMAGE+","+
////            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_PARENT+","+
////            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DESC+","+
////            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_SAVED_DATE+
////            " FROM " + CategoryDBAdapter.TABLE_NAME +
////            " INNER JOIN " + CategoryLanDBAdapter.TABLE_NAME +
////            " ON " +
////            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE + "=" +
////            CategoryLanDBAdapter.TABLE_NAME+"."+ CategoryLanDBAdapter.COL_CODE +
////            " WHERE " + CategoryDBAdapter.TABLE_NAME+"."+CategoryDBAdapter.COL_CODE +" >= "+UtilCategory.CUSTOM_CATEGORY_CODE_START+
////            " ORDER BY " + CategoryDBAdapter.TABLE_NAME+"."+CategoryDBAdapter.COL_CODE)
////    LiveData<List<CategoryStatus>> getCustomStatusesLiveData(String langCode);
//
//    @Query("SELECT "+ CategoryDBAdapter.COL_CODE + " FROM " + CategoryDBAdapter.TABLE_NAME)
//    LiveData<List<Integer>> getCategoryCodes();
//
////    @Query("SELECT "+ CategoryDBAdapter.COL_CODE + " FROM " + CategoryDBAdapter.TABLE_NAME)
////    List<Integer> getCategoryCodes();
//
////    @Query(query)
////    List<Integer> getTmp(String query);
//}
