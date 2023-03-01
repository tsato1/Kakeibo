package com.kakeibo.core.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kakeibo.core.data.constants.ConstLocallyDeletedItemIdDB

@Entity(tableName = ConstLocallyDeletedItemIdDB.TABLE_NAME)
data class LocallyDeletedItemIdEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = ConstLocallyDeletedItemIdDB.COL_DELETED_ITEM_UUID)
    val deletedItemUUID: String,
    @ColumnInfo(name = ConstLocallyDeletedItemIdDB.COL_DELETED_ITEM_ID)
    val deletedItemId: Long
)