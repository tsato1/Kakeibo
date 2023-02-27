package com.kakeibo.core.data.constants

object ConstCategoryDB {
    const val TABLE_NAME_OLD = "categories"
    const val TABLE_NAME = "categoryEntity" // from db version 12
    const val COL_ID = "_id"
    const val COL_CODE = "code"
    const val COL_NAME = "name" //deprecated on dbv=6 // re-added on dbv=7
    const val COL_COLOR = "color" // 1=income, 0=expense
    const val COL_SIGN = "sign" //-1=default for custom categories 0=insignificant(default categories) 1=mid 2=significant
    const val COL_DRAWABLE = "drawable"
    const val COL_IMAGE = "image" //added on dbv=6
    const val COL_LOCATION = "location" //deprecated on dbv=6
    const val COL_SUB_CATEGORIES = "sub_categories" //deprecated on dbv=6
    const val COL_PARENT = "parent"
    const val COL_DESCRIPTION = "description"
    const val COL_VAL1 = "val1"
    const val COL_VAL2 = "val2"
    const val COL_VAL3 = "val3"
    const val COL_SAVED_DATE = "saved_date" //default=""
    const val COL_IS_SYNCED = "category_is_synced" // added on version 10
    const val COL_UUID = "uuid"
}