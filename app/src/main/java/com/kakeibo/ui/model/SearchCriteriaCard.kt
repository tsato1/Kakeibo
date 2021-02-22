package com.kakeibo.ui.model

class SearchCriteriaCard internal constructor(var type: Int, var data: Int) {
    override fun equals(other: Any?): Boolean {
        var isSame = false
        if (other is SearchCriteriaCard) {
            isSame = type == other.type
        }
        return isSame
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + data
        return result
    }

    companion object {
        /*** for search  */
        const val TYPE_DATE_RANGE = 0
        const val TYPE_AMOUNT_RANGE = 1
        const val TYPE_CATEGORY = 2
        const val TYPE_MEMO = 3
    }
}