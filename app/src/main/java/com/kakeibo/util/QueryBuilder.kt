package com.kakeibo.util

import com.kakeibo.ui.Query
import java.util.*

object QueryBuilder {
    const val DESC = "DESC"
    const val ASC = "ASC"
    const val SUM_AMOUNT = "SUM(amount)"
    private var builderCs: MutableMap<Int, StringBuilder>? = null

    /*** for category detailed list  */
    private var builderC: StringBuilder? = null

    /*** for category list  */
    private var builderD: StringBuilder? = null

    /*** for expandable date list  */
    private var where = false
    private var orderC = false
    private var orderD = false
    fun init(categoryCodes: List<Int>) {

        builderCs = HashMap()

        for (code in categoryCodes) {
            builderCs?.put(code, StringBuilder("SELECT * FROM ITEMS"))
        }
        builderC = StringBuilder("SELECT " + SUM_AMOUNT + ", " +
                ItemDBAdapter.COL_CATEGORY_CODE +
                " FROM ITEMS")
        builderD = StringBuilder("SELECT * FROM ITEMS")
        where = false
        orderC = false
        orderD = false
    }

    /*** has to be DB format  */
    fun setDate(fromDate: String, toDate: String?) {
        if (!where) {
            for (builder in builderCs!!.values) {
                builder.append(" WHERE ")
            }
            builderC!!.append(" WHERE ")
            builderD!!.append(" WHERE ")
            where = true
        } else {
            for (builder in builderCs!!.values) {
                builder.append(" AND ")
            }
            builderC!!.append(" AND ")
            builderD!!.append(" AND ")
        }
        if (toDate == null || "" == toDate) {
            val ym = "\'" + fromDate.split("-").toTypedArray()[0] + "-" + fromDate.split("-").toTypedArray()[1] + "\'"

            for (builder in builderCs!!.values) {
                builder.append("strftime('%Y-%m', " + ItemDBAdapter.COL_EVENT_DATE).append(") = ").append(ym)
            }

            builderC!!.append("strftime('%Y-%m', " + ItemDBAdapter.COL_EVENT_DATE).append(") = ").append(ym)
            builderD!!.append("strftime('%Y-%m', " + ItemDBAdapter.COL_EVENT_DATE).append(") = ").append(ym)
        } else {
            for (builder in builderCs!!.values) {
                builder.append(ItemDBAdapter.COL_EVENT_DATE + " between strftime('%Y-%m-%d', '").append(fromDate)
                        .append("') and strftime('%Y-%m-%d', '").append(toDate).append("')")
            }
            builderC!!.append(ItemDBAdapter.COL_EVENT_DATE + " between strftime('%Y-%m-%d', '").append(fromDate)
                    .append("') and strftime('%Y-%m-%d', '").append(toDate).append("')")
            builderD!!.append(ItemDBAdapter.COL_EVENT_DATE + " between strftime('%Y-%m-%d', '").append(fromDate)
                    .append("') and strftime('%Y-%m-%d', '").append(toDate).append("')")
        }
    }

    fun setAmount(min: Long, max: Long) {
        if (!where) {
            for (builder in builderCs!!.values) {
                builder.append(" WHERE ")
            }
            builderC!!.append(" WHERE ")
            builderD!!.append(" WHERE ")
            where = true
        } else {
            for (builder in builderCs!!.values) {
                builder.append(" AND ")
            }
            builderC!!.append(" AND ")
            builderD!!.append(" AND ")
        }
        for (builder in builderCs!!.values) {
            builder.append(ItemDBAdapter.COL_AMOUNT + " BETWEEN ").append(min).append(" AND ").append(max)
        }
        builderC!!.append(ItemDBAdapter.COL_AMOUNT + " BETWEEN ").append(min).append(" AND ").append(max)
        builderD!!.append(ItemDBAdapter.COL_AMOUNT + " BETWEEN ").append(min).append(" AND ").append(max)
    }

    fun setCategoryCode(categoryCode: String?) {
        if (!where) {
            for (builder in builderCs!!.values) {
                builder.append(" WHERE ")
            }
            builderC!!.append(" WHERE ")
            builderD!!.append(" WHERE ")
            where = true
        } else {
            for (builder in builderCs!!.values) {
                builder.append(" AND ")
            }
            builderC!!.append(" AND ")
            builderD!!.append(" AND ")
        }
        if (categoryCode != null) {
            for (builder in builderCs!!.values) {
                builder.append(ItemDBAdapter.COL_CATEGORY_CODE + "=").append(categoryCode)
            }
            builderC!!.append(ItemDBAdapter.COL_CATEGORY_CODE + "=").append(categoryCode)
            builderD!!.append(ItemDBAdapter.COL_CATEGORY_CODE + "=").append(categoryCode)
        }
    }

    fun setMemo(memo: String) {
        if (!where) {
            for (builder in builderCs!!.values) {
                builder.append(" WHERE ")
            }
            builderC!!.append(" WHERE ")
            builderD!!.append(" WHERE ")
            where = true
        } else {
            for (builder in builderCs!!.values) {
                builder.append(" AND ")
            }
            builderC!!.append(" AND ")
            builderD!!.append(" AND ")
        }
        if ("" != memo) {
            for (builder in builderCs!!.values) {
                builder.append(ItemDBAdapter.COL_MEMO + "=" + "\'").append(memo).append("\'")
            }
            builderC!!.append(ItemDBAdapter.COL_MEMO + "=" + "\'").append(memo).append("\'")
            builderD!!.append(ItemDBAdapter.COL_MEMO + "=" + "\'").append(memo).append("\'")
        }
    }

    fun setCsWhere(col: String?) {
        if (!where) {
            for (builder in builderCs!!.values) {
                builder.append(" WHERE ")
            }
            where = true
        } else {
            for (builder in builderCs!!.values) {
                builder.append(" AND ")
            }
        }
        for ((key, sb) in builderCs!!) {
            sb.append(col).append("=").append(key)
            builderCs!![key] = sb
        }
    }

    fun setCOrderBy(colOrderBy: String?, direction: String?) {
        if (!orderC) {
            builderC!!.append(" ORDER BY ")
            orderC = true
        } else {
            builderC!!.append(", ")
        }
        builderC!!.append(colOrderBy).append(" ").append(direction)
    }

    fun setDOrderBy(colOrderBy: String?, direction: String?) {
        if (!orderD) {
            builderD!!.append(" ORDER BY ")
            orderD = true
        } else {
            builderD!!.append(", ")
        }
        builderD!!.append(colOrderBy).append(" ").append(direction)
    }

    fun setCGroupBy(colGroupBy: String?) {
        builderC!!.append(" GROUP BY ").append(colGroupBy)
    }

    private fun buildQueryC(): String {
        return builderC.toString()
    }

    private fun buildQueryCs(): Map<Int?, String?> {
        val out: MutableMap<Int?, String?> = HashMap()
        for ((key, value) in builderCs!!) {
            out[key] = value.toString()
        }
        return out
    }

    private fun buildQueryD(): String {
        return builderD.toString()
    }

    fun build(query: Query) {
        query.queryC = buildQueryC()
        query.queryCs = buildQueryCs()
        query.queryD = buildQueryD()
    }
}