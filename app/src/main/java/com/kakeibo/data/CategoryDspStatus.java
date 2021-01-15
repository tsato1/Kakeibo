//package com.kakeibo.data;
//
//import androidx.room.ColumnInfo;
//import androidx.room.Entity;
//import androidx.room.Ignore;
//import androidx.room.PrimaryKey;
//
//import com.kakeibo.db.CategoryDspDBAdapter;
//
//@Entity(tableName = "categories_dsp")
//public class CategoryDspStatus {
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = CategoryDspDBAdapter.COL_ID)
//    private long id = 1;
//
//    @ColumnInfo(name = CategoryDspDBAdapter.COL_LOCATION)
//    private int location;
//
//    @ColumnInfo(name = CategoryDspDBAdapter.COL_CODE)
//    private int code;
//
//    public CategoryDspStatus(long id, int location, int code) {
//        this.id = id;
//        this.location = location;
//        this.code = code;
//    }
//
//    @Ignore
//    public CategoryDspStatus(int location, int code) {
//        this.location = location;
//        this.code = code;
//    }
//
//    public long getId() {
//        return this.id;
//    }
//
//    public int getLocation() {
//        return this.location;
//    }
//
//    public int getCode() {
//        return this.code;
//    }
//}
