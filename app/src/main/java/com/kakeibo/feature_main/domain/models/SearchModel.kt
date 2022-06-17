package com.kakeibo.feature_main.domain.models

import com.kakeibo.core.data.constants.ConstCategoryDB
import com.kakeibo.core.data.local.entities.SearchEntity
import java.math.BigDecimal

data class SearchModel(
    var _id: Long? = null,
    var fromDate: String? = null,
    var toDate: String? = null,
    var fromAmount: String? = null,
    var toAmount: String? = null,
    var categoryCode: Int? = null,
    var categoryName: String? = null,
    var memo: String? = null,
    var fromUpdateDate: String? = null,
    var toUpdateDate: String? = null
) {

    fun toSearchEntity(): SearchEntity {
        return SearchEntity(
            _id = _id ?: 0,
            fromDate = fromDate,
            toDate = toDate,
            fromAmount = fromAmount,
            toAmount = toAmount,
            categoryCode = categoryCode,
            categoryName = categoryName,
            memo = memo,
            fromUpdateDate = fromUpdateDate,
            toUpdateDate = toUpdateDate
        )
    }

    /*
    at least one of the criteria must be non-null
     */
    fun toQuery(): String {
        var out = "SELECT " +
                ConstItemDB.TABLE_NAME + "." + ConstItemDB.COL_ID + "," +
                ConstItemDB.COL_AMOUNT + "," +
                ConstItemDB.COL_CURRENCY_CODE + "," +
                ConstItemDB.COL_CATEGORY_CODE + "," +
                ConstItemDB.COL_MEMO + "," +
                ConstItemDB.COL_EVENT_DATE + "," +
                ConstItemDB.COL_UPDATE_DATE + "," +
                ConstCategoryDB.COL_NAME + "," +
                ConstCategoryDB.COL_COLOR + "," +
                ConstCategoryDB.COL_SIGN + "," +
                ConstCategoryDB.COL_DRAWABLE + "," +
                ConstCategoryDB.COL_IMAGE + "," +
                ConstCategoryDB.COL_PARENT + "," +
                ConstCategoryDB.COL_DESCRIPTION + "," +
                ConstCategoryDB.COL_SAVED_DATE +
                " FROM " + ConstItemDB.TABLE_NAME +
                " INNER JOIN " + ConstCategoryDB.TABLE_NAME +
                " ON " + ConstItemDB.COL_CATEGORY_CODE + " = " + ConstCategoryDB.COL_CODE +
                " WHERE "
        out += if (fromDate != null && toDate != null)
            ConstItemDB.COL_EVENT_DATE + " BETWEEN ? AND ? AND "
        else ""

        out += if (fromAmount != null && toAmount != null)
            ConstItemDB.COL_AMOUNT + " BETWEEN ? AND ? AND "
        else ""

        out += if (categoryCode != null)
            ConstItemDB.COL_CATEGORY_CODE + "= ? AND "
        else ""

        out += if (memo != null) {
            if (false) ConstItemDB.COL_MEMO + " LIKE '%' || ? || '%' AND "
            else ConstItemDB.COL_MEMO + " = ? AND "
        } else ""

        out = out.substring(0, out.length - 5) // removing the trailing "AND"

        out += " ORDER BY " + ConstItemDB.COL_EVENT_DATE

        return out
    }

    fun toArgs(): List<String> {
        val out = mutableListOf<String>()
        fromDate?.let { out.add(it) }
        toDate?.let { out.add(it) }
        fromAmount?.let { out.add(it.toBigDecimal().multiply(BigDecimal(1000)).toString()) }
        toAmount?.let { out.add(it.toBigDecimal().multiply(BigDecimal(1000)).toString()) }
        categoryCode?.let { out.add(it.toString()) }
        memo?.let { out.add(it) }
        return out.toList()
    }

    fun toCountQuery(): String {
        var out = "SELECT COUNT(*) FROM " + ConstItemDB.TABLE_NAME +
                " INNER JOIN " + ConstCategoryDB.TABLE_NAME +
                " ON " + ConstItemDB.COL_CATEGORY_CODE + " = " + ConstCategoryDB.COL_CODE +
                " WHERE "
        out += if (fromDate != null && toDate != null)
            ConstItemDB.COL_EVENT_DATE + " BETWEEN ? AND ? AND "
        else ""

        out += if (fromAmount != null && toAmount != null)
            ConstItemDB.COL_AMOUNT + " BETWEEN ? AND ? AND "
        else ""

        out += if (categoryCode != null)
            ConstItemDB.COL_CATEGORY_CODE + "= ? AND "
        else ""

        out += if (memo != null) {
            if (false) ConstItemDB.COL_MEMO + " LIKE '%' || ? || '%' AND "
            else ConstItemDB.COL_MEMO + " = ? AND "
        } else ""

        out = out.substring(0, out.length - 5) // removing the trailing "AND"

        return out
    }

}