package com.kakeibo.db

object CategoryDBAdapter {
    const val TABLE_NAME = "categories"
    const val COL_ID = "_id"
    const val COL_CODE = "code"
    const val COL_NAME = "name" //deprecated on dbv=6 // re-added on dbv=7

    const val COL_COLOR = "color" // 1=income, 0=expense, 11-20=custom

    const val COL_SIGNIFICANCE = "sign" //0=insignificant 1=mid 2=significant

    const val COL_DRAWABLE = "drawable"
    const val COL_IMAGE = "image" //added on dbv=6

    const val COL_LOCATION = "location" //deprecated on dbv=6

    const val COL_SUB_CATEGORIES = "sub_categories" //deprecated on dbv=6

    const val COL_PARENT = "parent"
    const val COL_DESC = "description"
    const val COL_VAL1 = "val1"
    const val COL_VAL2 = "val2"
    const val COL_VAL3 = "val3"
    const val COL_SAVED_DATE = "saved_date" //default=""
}