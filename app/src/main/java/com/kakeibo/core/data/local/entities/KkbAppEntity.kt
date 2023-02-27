//package com.kakeibo.core.data.local.entities
//
//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import com.kakeibo.core.data.constants.ConstKkbAppDB
//import com.kakeibo.core.domain.models.KkbAppModel
//
//@Entity(tableName = ConstKkbAppDB.TABLE_KKBAPP)
//class KkbAppEntity(
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = ConstKkbAppDB.COL_ID)
//    var id: Long = 1,
//
//    @ColumnInfo(name = ConstKkbAppDB.COL_NAME, defaultValue = "")
//    var name: String,
//
//    @ColumnInfo(name = ConstKkbAppDB.COL_TYPE, defaultValue = "")
//    var type: String,
//
//    @ColumnInfo(name = ConstKkbAppDB.COL_UPDATE_DATE, defaultValue = "")
//    var updateDate: String,
//
//    /* db version at installation */
//    @ColumnInfo(name = ConstKkbAppDB.COL_VAL_INT_1, defaultValue = "0")
//    var valInt1: Int,
//
//    /* -1: default, 0: banner ads display agreed */
//    @ColumnInfo(name = ConstKkbAppDB.COL_VAL_INT_2, defaultValue = "-1")
//    var valInt2: Int,
//
//    @ColumnInfo(name = ConstKkbAppDB.COL_VAL_INT_3, defaultValue = "0")
//    var valInt3: Int,
//
//    @ColumnInfo(name = ConstKkbAppDB.COL_VAL_STR_1, defaultValue = "")
//    var valStr1: String,
//
//    @ColumnInfo(name = ConstKkbAppDB.COL_VAL_STR_2, defaultValue = "")
//    var valStr2: String,
//
//    @ColumnInfo(name = ConstKkbAppDB.COL_VAL_STR_3, defaultValue = "")
//    var valStr3: String
//) {
//    fun toKkbAppModel(): KkbAppModel {
//        return KkbAppModel(
//            id = id,
//            name = name,
//            type = type,
//            updateDate = updateDate,
//            intVal1 = valInt1,
//            intVal2 = valInt2,
//            intVal3 = valInt3,
//            strVal1 = valStr1,
//            strVal2 = valStr2,
//            strVal3 = valStr3
//        )
//    }
//}