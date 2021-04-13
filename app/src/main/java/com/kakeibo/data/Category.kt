package com.kakeibo.data

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kakeibo.db.CategoryDBAdapter

@Entity(tableName = "categories")
class Category(
        id: Long,
        @ColumnInfo(name = CategoryDBAdapter.COL_CODE, defaultValue = "0") var code: Int,
        @ColumnInfo(name = CategoryDBAdapter.COL_NAME, defaultValue = "") var name: String,
        @ColumnInfo(name = CategoryDBAdapter.COL_COLOR, defaultValue = "0") var color: Int,
        @ColumnInfo(name = CategoryDBAdapter.COL_SIGNIFICANCE, defaultValue = "0") var significance: Int,
        @ColumnInfo(name = CategoryDBAdapter.COL_DRAWABLE, defaultValue = "") var drawable: String,
        @Nullable @ColumnInfo(name = CategoryDBAdapter.COL_IMAGE) var image: ByteArray?,
        @ColumnInfo(name = CategoryDBAdapter.COL_PARENT, defaultValue = "-1") var parent: Int,
        @ColumnInfo(name = CategoryDBAdapter.COL_DESC, defaultValue = "") var description: String,
        @ColumnInfo(name = CategoryDBAdapter.COL_SAVED_DATE, defaultValue = "") var savedDate: String) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = CategoryDBAdapter.COL_ID)
    var id: Long = id
        private set

}