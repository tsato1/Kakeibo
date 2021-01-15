package com.kakeibo.data.disk

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kakeibo.data.CategoryStatus
import com.kakeibo.db.CategoryDBAdapter
import com.kakeibo.db.CategoryDspDBAdapter

@Dao
interface CategoryStatusDao {
    @Query("SELECT * FROM " + CategoryDBAdapter.TABLE_NAME)
    fun getAll(): LiveData<List<CategoryStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categories: List<CategoryStatus>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(categoryStatus: CategoryStatus): Long

    @Query("DELETE FROM categories")
    fun deleteAll()

    @Query("SELECT " +
            CategoryDspDBAdapter.TABLE_NAME + "." + CategoryDspDBAdapter.COL_ID + "," +
            CategoryDspDBAdapter.TABLE_NAME + "." + CategoryDspDBAdapter.COL_CODE + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_NAME + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_COLOR + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_SIGNIFICANCE + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_DRAWABLE + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_IMAGE + "," +
            CategoryDspDBAdapter.TABLE_NAME + "." + CategoryDspDBAdapter.COL_LOCATION + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_PARENT + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_DESC + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_SAVED_DATE +
            " FROM " + CategoryDspDBAdapter.TABLE_NAME +
            " INNER JOIN " + CategoryDBAdapter.TABLE_NAME +
            " ON " +
            CategoryDspDBAdapter.TABLE_NAME + "." + CategoryDspDBAdapter.COL_CODE + "=" +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_CODE +
            " ORDER BY " + CategoryDspDBAdapter.TABLE_NAME + "." + CategoryDspDBAdapter.COL_LOCATION)
    fun getCategoriesForDisplay(): LiveData<List<CategoryStatus>>

    @Query("SELECT " +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_ID + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_CODE + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_NAME + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_COLOR + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_DRAWABLE + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_IMAGE + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_SIGNIFICANCE + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_DESC + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_SAVED_DATE + "," +
            CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_PARENT +
            " FROM " + CategoryDBAdapter.TABLE_NAME +  //            " INNER JOIN " + CategoryLanDBAdapter.TABLE_NAME +
            //            " ON " +
            //            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE + "=" +
            //            CategoryLanDBAdapter.TABLE_NAME+"."+ CategoryLanDBAdapter.COL_CODE +
            " WHERE " + CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_CODE +
            " NOT IN " +
            " (" +
            " SELECT " + CategoryDspDBAdapter.COL_CODE +
            " FROM " + CategoryDspDBAdapter.TABLE_NAME +
            ")" +
            " ORDER BY " + CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_CODE)
    fun nonDspStatusesLiveData(): LiveData<List<CategoryStatus>>


    //    @Query("SELECT "+
    //            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_ID+","+
    //            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE+","+
    //            ":langCode,"+
    //            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_COLOR+","+
    //            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_SIGNIFICANCE+","+
    //            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DRAWABLE+","+
    //            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_IMAGE+","+
    //            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_PARENT+","+
    //            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DESC+","+
    //            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_SAVED_DATE+
    //            " FROM " + CategoryDBAdapter.TABLE_NAME +
    //            " INNER JOIN " + CategoryLanDBAdapter.TABLE_NAME +
    //            " ON " +
    //            CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE + "=" +
    //            CategoryLanDBAdapter.TABLE_NAME+"."+ CategoryLanDBAdapter.COL_CODE +
    //            " WHERE " + CategoryDBAdapter.TABLE_NAME+"."+CategoryDBAdapter.COL_CODE +" >= "+UtilCategory.CUSTOM_CATEGORY_CODE_START+
    //            " ORDER BY " + CategoryDBAdapter.TABLE_NAME+"."+CategoryDBAdapter.COL_CODE)
    //    LiveData<List<CategoryStatus>> getCustomStatusesLiveData(String langCode);
    //    @Query("SELECT "+ CategoryDBAdapter.COL_CODE + " FROM " + CategoryDBAdapter.TABLE_NAME)
    @Query("SELECT " + CategoryDBAdapter.COL_CODE + " FROM " + CategoryDBAdapter.TABLE_NAME)
    fun getAllCodes(): LiveData<List<Int>>
    //    List<Integer> getCategoryCodes();
    //    @Query(query)
    //    List<Integer> getTmp(String query);
}