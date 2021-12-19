package com.kakeibo.core.data.local.entities

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.BLOB
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kakeibo.core.data.constants.ConstCategoryDB
import com.kakeibo.feature_settings.domain.models.CategoryModel

@Entity(tableName = "categories")
class CategoryEntity(
    id: Long,
    @ColumnInfo(name = ConstCategoryDB.COL_CODE, defaultValue = "0") var code: Int,
    @ColumnInfo(name = ConstCategoryDB.COL_NAME, defaultValue = "") var name: String,
    @ColumnInfo(name = ConstCategoryDB.COL_COLOR, defaultValue = "0") var color: Int,
    @ColumnInfo(name = ConstCategoryDB.COL_SIGN, defaultValue = "0") var sign: Int,
    @ColumnInfo(name = ConstCategoryDB.COL_DRAWABLE, defaultValue = "") var drawable: String,
    @Nullable @ColumnInfo(name = ConstCategoryDB.COL_IMAGE) var image: ByteArray?,
    @ColumnInfo(name = ConstCategoryDB.COL_PARENT, defaultValue = "-1") var parent: Int,
    @ColumnInfo(name = ConstCategoryDB.COL_DESCRIPTION, defaultValue = "") var description: String,
    @ColumnInfo(name = ConstCategoryDB.COL_SAVED_DATE, defaultValue = "") var savedDate: String) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ConstCategoryDB.COL_ID)
    var _id: Long = id
        private set

    fun toCategoryModel(): CategoryModel {
        return CategoryModel(
            _id = _id,
            code = code,
            name = name,
            color = color,
            sign = sign,
            drawable = drawable,
            image = image,
            parent = parent,
            description = description,
            savedDate = savedDate
//            isSynced = isSynced
        )
    }
}



//@Entity(tableName = ConstCategoryDB.TABLE_NAME)
//class CategoryEntity(
//    @PrimaryKey(autoGenerate = true) val _id: Long? = null,
//    val code: Int,
//    val name: String,
//    val color: Int,
//    val sign: Int,
//    val drawable: String,
//    @ColumnInfo(typeAffinity = BLOB) val image: ByteArray? = null,
//    val parent: Int,
//    val description: String,
//    @ColumnInfo(name = ConstCategoryDB.COL_SAVED_DATE) val savedDate: String,
//    @ColumnInfo(name = ConstCategoryDB.COL_IS_SYNCED) var isSynced: Boolean
//) {
//
//    fun toCategoryModel(): CategoryModel {
//        return CategoryModel(
//            _id = _id,
//            code = code,
//            name = name,
//            color = color,
//            sign = sign,
//            drawable = drawable,
//            image = image,
//            parent = parent,
//            description = description,
//            savedDate = savedDate,
//            isSynced = isSynced
//        )
//    }
//
//}