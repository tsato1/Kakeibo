//package com.kakeibo.data.disk;
//
//import androidx.lifecycle.LiveData;
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.kakeibo.data.CategoryDspStatus;
//import com.kakeibo.data.CategoryStatus;
//import com.kakeibo.db.CategoryDBAdapter;
//import com.kakeibo.db.CategoryDspDBAdapter;
//
//import java.util.List;
//
//@Dao
//interface CategoryDspStatusDao {
//
//    @Query("SELECT * FROM " + CategoryDspDBAdapter.TABLE_NAME)
//    LiveData<List<CategoryDspStatus>> getAll();
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertAll(List<CategoryDspStatus> dspCategories);
//
//    @Query("DELETE FROM categories_dsp")
//    void deleteAll();
//
//    @Query("SELECT " +
//            CategoryDspDBAdapter.TABLE_NAME+"."+ CategoryDspDBAdapter.COL_ID+","+
//            CategoryDspDBAdapter.TABLE_NAME+"."+ CategoryDspDBAdapter.COL_CODE+","+
//            CategoryDBAdapter.TABLE_NAME+"."+CategoryDBAdapter.COL_NAME+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_COLOR+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_SIGNIFICANCE+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DRAWABLE+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_IMAGE+","+
//            CategoryDspDBAdapter.TABLE_NAME+"."+ CategoryDspDBAdapter.COL_LOCATION+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_PARENT+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DESC+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_SAVED_DATE+
//            " FROM " + CategoryDspDBAdapter.TABLE_NAME +
//            " INNER JOIN " + CategoryDBAdapter.TABLE_NAME +
//            " ON " +
//            CategoryDspDBAdapter.TABLE_NAME+"."+ CategoryDspDBAdapter.COL_CODE +"=" +
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE +
//            " ORDER BY " + CategoryDspDBAdapter.TABLE_NAME+"."+ CategoryDspDBAdapter.COL_LOCATION)
//    LiveData<List<CategoryStatus>> getCategoryStatusesForDsp();
//
//    @Query("SELECT "+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_ID+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_NAME+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_COLOR+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DRAWABLE+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_IMAGE+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_SIGNIFICANCE+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DESC+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_SAVED_DATE+","+
//            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_PARENT+
//            " FROM " + CategoryDBAdapter.TABLE_NAME +
////            " INNER JOIN " + CategoryLanDBAdapter.TABLE_NAME +
////            " ON " +
////            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE + "=" +
////            CategoryLanDBAdapter.TABLE_NAME+"."+ CategoryLanDBAdapter.COL_CODE +
//            " WHERE " + CategoryDBAdapter.TABLE_NAME+"."+CategoryDBAdapter.COL_CODE +
//            " NOT IN " +
//            " (" +
//            " SELECT " + CategoryDspDBAdapter.COL_CODE +
//            " FROM " + CategoryDspDBAdapter.TABLE_NAME +
//            ")"+
//            " ORDER BY " + CategoryDBAdapter.TABLE_NAME+"."+CategoryDBAdapter.COL_CODE)
//    LiveData<List<CategoryStatus>> getNonDspStatusesLiveData();
//
//    @Query("SELECT " + CategoryDBAdapter.TABLE_NAME+"."+CategoryDBAdapter.COL_CODE+" FROM "+CategoryDBAdapter.TABLE_NAME)
//    LiveData<List<Integer>> getDspCodes();
//}
