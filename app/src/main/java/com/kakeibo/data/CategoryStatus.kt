package com.kakeibo.data

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.kakeibo.db.CategoryDBAdapter

@Entity(tableName = "categories")
class CategoryStatus {
    /***
     * Getter
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = CategoryDBAdapter.COL_ID)
    var id: Long = 1
        private set

    /***
     * Setter
     */
    @ColumnInfo(name = CategoryDBAdapter.COL_CODE, defaultValue = "0")
    var code = 0

    @ColumnInfo(name = CategoryDBAdapter.COL_NAME, defaultValue = "")
    var name = ""

    @ColumnInfo(name = CategoryDBAdapter.COL_COLOR, defaultValue = "0")
    var color = 0

    @ColumnInfo(name = CategoryDBAdapter.COL_SIGNIFICANCE, defaultValue = "0")
    var significance = 0

    @ColumnInfo(name = CategoryDBAdapter.COL_DRAWABLE, defaultValue = "")
    var drawable = ""

    @ColumnInfo(name = CategoryDBAdapter.COL_IMAGE)
    @Nullable
    var image: ByteArray?

    @ColumnInfo(name = CategoryDBAdapter.COL_PARENT, defaultValue = "-1")
    var parent = -1

    @ColumnInfo(name = CategoryDBAdapter.COL_DESC, defaultValue = "")
    var description = ""

    @ColumnInfo(name = CategoryDBAdapter.COL_SAVED_DATE, defaultValue = "")
    var savedDate = ""

    constructor(id: Long,
                code: Int,
                name: String,
                color: Int,
                significance: Int,
                drawable: String,
                @Nullable image: ByteArray?,
                parent: Int,
                description: String,
                savedDate: String) {
        this.id = id
        this.code = code
        this.name = name
        this.color = color
        this.significance = significance
        this.drawable = drawable
        this.image = image
        this.parent = parent
        this.description = description
        this.savedDate = savedDate
    }

    @Ignore
    constructor(
            code: Int,
            color: Int,
            significance: Int,
            drawable: String,
            @Nullable image: ByteArray?,
            parent: Int,
            description: String,
            savedDate: String) {
        this.code = code
        this.color = color
        this.significance = significance
        this.drawable = drawable
        this.image = image
        this.parent = parent
        this.description = description
        this.savedDate = savedDate
    }
}