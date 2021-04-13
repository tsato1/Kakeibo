package com.kakeibo.data.disk

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kakeibo.data.Category
import com.kakeibo.db.CategoryDBAdapter
import com.kakeibo.db.CategoryDspDBAdapter

@Dao
interface CategoryDao {
    @Query("SELECT * FROM " + CategoryDBAdapter.TABLE_NAME)
    fun getAll(): LiveData<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categories: List<Category>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category): Long

    @Query("DELETE FROM " + CategoryDBAdapter.TABLE_NAME)
    fun deleteAll()

    @Query("DELETE FROM " + CategoryDBAdapter.TABLE_NAME + " WHERE " + CategoryDBAdapter.COL_ID + " = :id")
    fun delete(id: Long)

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
    fun getCategoriesDisplayed(): LiveData<List<Category>>

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
            " FROM " + CategoryDBAdapter.TABLE_NAME +
            " WHERE " + CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_CODE +
            " NOT IN " +
            "(" +
            " SELECT " + CategoryDspDBAdapter.COL_CODE +
            " FROM " + CategoryDspDBAdapter.TABLE_NAME +
            ")" +
            " ORDER BY " + CategoryDBAdapter.TABLE_NAME + "." + CategoryDBAdapter.COL_CODE)
    fun getCategoriesNotDisplay(): LiveData<List<Category>>


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
    //    List<Integer> getCategoryCodes();
    //    @Query(query)
    //    List<Integer> getTmp(String query);
}