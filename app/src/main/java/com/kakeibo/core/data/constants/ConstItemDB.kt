object ConstItemDB {
    const val TABLE_NAME_OLD = "items"
    const val TABLE_NAME = "itemEntity" // from db version 12
    const val COL_ID = "_id"
    const val COL_AMOUNT = "amount"
    const val COL_CURRENCY_CODE = "currency_code"
    const val COL_CATEGORY = "category" // dropped on version 2
    const val COL_CATEGORY_CODE = "category_code"
    const val COL_MEMO = "memo"
    const val COL_EVENT_D = "event_d" // dropped on version 3
    const val COL_EVENT_YM = "event_ym" // dropped on version 3
    const val COL_EVENT_DATE = "event_date"
    const val COL_UPDATE_DATE = "update_date"
    const val COL_IS_SYNCED = "item_is_synced"// added on version 10
    const val COL_UUID = "uuid"
}