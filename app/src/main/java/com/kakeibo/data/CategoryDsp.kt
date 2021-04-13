package com.kakeibo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.kakeibo.db.CategoryDspDBAdapter

@Entity(tableName = "categories_dsp")
class CategoryDsp(id: Long, location: Int, code: Int) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = CategoryDspDBAdapter.COL_ID)
    var id: Long = id
        private set

    @ColumnInfo(name = CategoryDspDBAdapter.COL_LOCATION)
    var location: Int = location
        private set

    @ColumnInfo(name = CategoryDspDBAdapter.COL_CODE)
    var code: Int = code
        private set
}