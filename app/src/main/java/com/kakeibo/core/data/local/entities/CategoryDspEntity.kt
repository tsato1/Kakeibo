package com.kakeibo.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kakeibo.core.data.constants.ConstCategoryDspDB

@Entity(tableName = ConstCategoryDspDB.TABLE_NAME)
data class CategoryDspEntity(

    @PrimaryKey(autoGenerate = true)
    val _id: Long,
    val location: Int,
    val code: Int

) {

    class InvalidCategoryDspException(message: String) : Exception(message)

}