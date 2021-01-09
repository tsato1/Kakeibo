package com.kakeibo.util;

import com.kakeibo.db.ItemDBAdapter;
import com.kakeibo.ui.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kakeibo.db.ItemDBAdapter.COL_AMOUNT;
import static com.kakeibo.db.ItemDBAdapter.COL_CATEGORY_CODE;
import static com.kakeibo.db.ItemDBAdapter.COL_EVENT_DATE;
import static com.kakeibo.db.ItemDBAdapter.COL_MEMO;

public class UtilQuery {
    public static final String DESC = "DESC";
    public static final String ASC = "ASC";
    public static final String SUM_AMOUNT = "SUM(amount)";

    private static Map<Integer, StringBuilder> builderCs; /*** for category detailed list ***/
    private static StringBuilder builderC; /*** for category list ***/
    private static StringBuilder builderD; /*** for expandable date list ***/
    private static boolean where;
    private static boolean orderC;
    private static boolean orderD;

    public static void init(List<Integer> categoryCodes) {
        builderCs = new HashMap<>();
        for (Integer code: categoryCodes) {
            builderCs.put(code, new StringBuilder("SELECT * FROM ITEMS"));
        }
        builderC = new StringBuilder("SELECT " + SUM_AMOUNT + ", " +
                ItemDBAdapter.COL_CATEGORY_CODE +
                " FROM ITEMS");
        builderD = new StringBuilder("SELECT * FROM ITEMS");
        where = false;
        orderC = false;
        orderD = false;
    }

    /*** has to be DB format ***/
    public static void setDate(String fromDate, String toDate) {
        if (!where) {
            for (StringBuilder builder :builderCs.values()) {
                builder.append(" WHERE ");
            }
            builderC.append(" WHERE ");
            builderD.append(" WHERE ");
            where = true;
        }
        else {
            for (StringBuilder builder :builderCs.values()) {
                builder.append(" AND ");
            }
            builderC.append(" AND ");
            builderD.append(" AND ");
        }

        if (toDate==null || "".equals(toDate)) {
            String ym = "\'" + fromDate.split("-")[0] + "-" + fromDate.split("-")[1] + "\'";
            for (StringBuilder builder :builderCs.values()) {
                builder.append("strftime('%Y-%m', " + COL_EVENT_DATE).append(") = ").append(ym);
            }
            builderC.append("strftime('%Y-%m', " + COL_EVENT_DATE).append(") = ").append(ym);
            builderD.append("strftime('%Y-%m', " + COL_EVENT_DATE).append(") = ").append(ym);
        }
        else {
            for (StringBuilder builder :builderCs.values()) {
                builder.append(COL_EVENT_DATE + " between strftime('%Y-%m-%d', '").append(fromDate)
                        .append("') and strftime('%Y-%m-%d', '").append(toDate).append("')");
            }
            builderC.append(COL_EVENT_DATE + " between strftime('%Y-%m-%d', '").append(fromDate)
                    .append("') and strftime('%Y-%m-%d', '").append(toDate).append("')");
            builderD.append(COL_EVENT_DATE + " between strftime('%Y-%m-%d', '").append(fromDate)
                    .append("') and strftime('%Y-%m-%d', '").append(toDate).append("')");
        }
    }

    public static void setAmount(long min, long max) {
        if (!where) {
            for (StringBuilder builder :builderCs.values()) {
                builder.append(" WHERE ");
            }
            builderC.append(" WHERE ");
            builderD.append(" WHERE ");
            where = true;
        }
        else {
            for (StringBuilder builder :builderCs.values()) {
                builder.append(" AND ");
            }
            builderC.append(" AND ");
            builderD.append(" AND ");
        }

        for (StringBuilder builder :builderCs.values()) {
            builder.append(COL_AMOUNT + " BETWEEN ").append(min).append(" AND ").append(max);
        }
        builderC.append(COL_AMOUNT + " BETWEEN ").append(min).append(" AND ").append(max);
        builderD.append(COL_AMOUNT + " BETWEEN ").append(min).append(" AND ").append(max);
    }

    public static void setCategoryCode(String categoryCode) {
        if (!where) {
            for (StringBuilder builder :builderCs.values()) {
                builder.append(" WHERE ");
            }
            builderC.append(" WHERE ");
            builderD.append(" WHERE ");
            where = true;
        }
        else {
            for (StringBuilder builder :builderCs.values()) {
                builder.append(" AND ");
            }
            builderC.append(" AND ");
            builderD.append(" AND ");
        }

        if (categoryCode != null) {
            for (StringBuilder builder :builderCs.values()) {

                builder.append(COL_CATEGORY_CODE + "=").append(categoryCode);
            }
            builderC.append(COL_CATEGORY_CODE + "=").append(categoryCode);
            builderD.append(COL_CATEGORY_CODE + "=").append(categoryCode);
        }
    }

    public static void setMemo(String memo) {
        if (!where) {
            for (StringBuilder builder :builderCs.values()) {
                builder.append(" WHERE ");
            }
            builderC.append(" WHERE ");
            builderD.append(" WHERE ");
            where = true;
        }
        else {
            for (StringBuilder builder :builderCs.values()) {
                builder.append(" AND ");
            }
            builderC.append(" AND ");
            builderD.append(" AND ");
        }

        if (!"".equals(memo)) {
            for (StringBuilder builder :builderCs.values()) {
                builder.append(COL_MEMO + "=" + "\'").append(memo).append("\'");
            }
            builderC.append(COL_MEMO + "=" + "\'").append(memo).append("\'");
            builderD.append(COL_MEMO + "=" + "\'").append(memo).append("\'");
        }
    }

    public static void setCsWhere(String col) {
        if (!where) {
            for (StringBuilder builder :builderCs.values()) {
                builder.append(" WHERE ");
            }
            where = true;
        }
        else {
            for (StringBuilder builder :builderCs.values()) {
                builder.append(" AND ");
            }
        }
        for (Map.Entry<Integer, StringBuilder> entry: builderCs.entrySet()) {
            StringBuilder sb = entry.getValue();
            sb.append(col).append("=").append(entry.getKey());
            builderCs.put(entry.getKey(), sb);
        }
    }

    public static void setCOrderBy(String colOrderBy, String direction) {
        if (!orderC) {
            builderC.append(" ORDER BY ");
            orderC = true;
        }
        else {
            builderC.append(", ");
        }
        builderC.append(colOrderBy).append(" ").append(direction);
    }

    public static void setDOrderBy(String colOrderBy, String direction) {
        if (!orderD) {
            builderD.append(" ORDER BY ");
            orderD = true;
        }
        else {
            builderD.append(", ");
        }
        builderD.append(colOrderBy).append(" ").append(direction);
    }

    public static void setCGroupBy(String colGroupBy) {
        builderC.append(" GROUP BY ").append(colGroupBy);
    }

    private static String buildQueryC() {
        return builderC.toString();
    }

    private static Map<Integer, String> buildQueryCs() {
        Map<Integer, String> out = new HashMap<>();
        for (Map.Entry<Integer, StringBuilder> entry: builderCs.entrySet()) {
            out.put(entry.getKey(), entry.getValue().toString());
        }
        return out;
    }

    private static String buildQueryD() {
        return builderD.toString();
    }

    public static void build(Query query) {
        query.setQueryC(buildQueryC());
        query.setQueryCs(buildQueryCs());
        query.setQueryD(buildQueryD());
    }
}
