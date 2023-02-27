package com.kakeibo.core.data.constants

object ConstLocallyDeletedItemIdDB {
    const val TABLE_NAME_OLD = "locally_deleted_item_ids"
    const val COL_DELETED_ITEM_ID = "deleted_item_id"
    const val TABLE_NAME = "locallyDeletedItemIdEntity" // used in db version 12 and above
    const val COL_DELETED_ITEM_UUID = "deleted_item_uuid" // used in db version 12 and above
}