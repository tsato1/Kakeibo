package com.kakeibo.db

object KkbAppDBAdapter {
     const val TABLE_KKBAPP = "kkbapp"
     const val COL_ID = "_id"
     const val COL_NAME = "name"
     const val COL_TYPE = "type"
     const val COL_UPDATE_DATE = "update_date"
     const val COL_VAL_INT_1 = "value_int_1" // db version at installation
     const val COL_VAL_INT_2 = "value_int_2" // -1: default, 0: banner ads display agreed
     const val COL_VAL_INT_3 = "value_int_3"
     const val COL_VAL_STR_1 = "value_str_1"
     const val COL_VAL_STR_2 = "value_str_2"
     const val COL_VAL_STR_3 = "value_str_3"
     const val COL_VAL_INT = "int"
     const val COL_VAL_STR = "str"
     const val COL_VAL_DB_VERSION = "db_version"
     const val COL_VAL_ADS = "ads"
     const val COL_VAL_INT_2_DEFAULT = -1
     const val COL_VAL_INT_2_SHOWADS = 0 // show ads
     const val COL_VAL_INT_3_DEFAULT = -1
 }