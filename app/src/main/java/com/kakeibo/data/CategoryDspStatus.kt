package com.kakeibo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.kakeibo.db.CategoryDspDBAdapter

@Entity(tableName = "categories_dsp")
class CategoryDspStatus {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = CategoryDspDBAdapter.COL_ID)
    var id: Long = 1
        private set

    @ColumnInfo(name = CategoryDspDBAdapter.COL_LOCATION)
    var location: Int
        private set

    @ColumnInfo(name = CategoryDspDBAdapter.COL_CODE)
    var code: Int
        private set

    constructor(id: Long, location: Int, code: Int) {
        this.id = id
        this.location = location
        this.code = code
    }

    @Ignore
    constructor(location: Int, code: Int) {
        this.location = location
        this.code = code
    }
}