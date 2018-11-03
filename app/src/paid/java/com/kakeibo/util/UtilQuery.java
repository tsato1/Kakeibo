package com.kakeibo.util;

import com.kakeibo.MainActivity;
import com.kakeibo.db.ItemsDBAdapter;

import static com.kakeibo.db.ItemsDBAdapter.COL_AMOUNT;
import static com.kakeibo.db.ItemsDBAdapter.COL_CATEGORY_CODE;
import static com.kakeibo.db.ItemsDBAdapter.COL_CURRENCY_CODE;
import static com.kakeibo.db.ItemsDBAdapter.COL_EVENT_DATE;
import static com.kakeibo.db.ItemsDBAdapter.COL_MEMO;

public class UtilQuery {
    public static final String DESC = "DESC";
    public static final String ASC = "ASC";

    private static StringBuilder[] builderCs;
    private static StringBuilder builderC;
    private static StringBuilder builderD;
    private static boolean where;

    public static void init() {
        builderCs = new StringBuilder[MainActivity.sCategories.length];
        for (int i=0; i<builderCs.length; ++i) {
            builderCs[i] = new StringBuilder("SELECT * FROM ITEMS");
        }
        builderC = new StringBuilder("SELECT SUM(amount), "+ItemsDBAdapter.COL_CATEGORY_CODE+" FROM ITEMS");
        builderD = new StringBuilder("SELECT * FROM ITEMS");
        where = false;
    }

    /*** has to be DB format ***/
    public static void setDate(String fromDate, String toDate) {
        if (!where) {
            for (StringBuilder builder :builderCs) {
                builder.append(" WHERE ");
            }
            builderC.append(" WHERE ");
            builderD.append(" WHERE ");
            where = true;
        }
        else {
            for (StringBuilder builder :builderCs) {
                builder.append(" AND ");
            }
            builderC.append(" AND ");
            builderD.append(" AND ");
        }

        if (toDate==null || "".equals(toDate)) {
            String ym = "\'" + fromDate.split("-")[0] + "-" + fromDate.split("-")[1] + "\'";
            for (StringBuilder builder :builderCs) {
                builder.append("strftime('%Y-%m', '" + COL_EVENT_DATE).append("') = ").append(ym);
            }
            builderC.append("strftime('%Y-%m', '" + COL_EVENT_DATE).append("') = ").append(ym);
            builderD.append("strftime('%Y-%m', '" + COL_EVENT_DATE).append("') = ").append(ym);
        }
        else {
            for (StringBuilder builder :builderCs) {
                builder.append(COL_EVENT_DATE + " between strftime('%Y-%m-%d', '").append(fromDate)
                        .append("') and strftime('%Y-%m-%d', '").append(toDate).append("')");
            }
            builderC.append(COL_EVENT_DATE + " between strftime('%Y-%m-%d', '").append(fromDate)
                    .append("') and strftime('%Y-%m-%d', '").append(toDate).append("')");
            builderD.append(COL_EVENT_DATE + " between strftime('%Y-%m-%d', '").append(fromDate)
                    .append("') and strftime('%Y-%m-%d', '").append(toDate).append("')");
        }
    }

    public static void setAmount(int min, int max) {
        if (!where) {
            for (StringBuilder builder :builderCs) {
                builder.append(" WHERE ");
            }
            builderC.append(" WHERE ");
            builderD.append(" WHERE ");
            where = true;
        }
        else {
            for (StringBuilder builder :builderCs) {
                builder.append(" AND ");
            }
            builderC.append(" AND ");
            builderD.append(" AND ");
        }

        for (StringBuilder builder :builderCs) {
            builder.append(COL_AMOUNT + " BETWEEN ").append(min).append(" AND ").append(max);
        }
        builderC.append(COL_AMOUNT + " BETWEEN ").append(min).append(" AND ").append(max);
        builderD.append(COL_AMOUNT + " BETWEEN ").append(min).append(" AND ").append(max);
    }

    public static void setCurrencyCode(String currencyCode) {
        if (!where) {
            for (StringBuilder builder :builderCs) {
                builder.append(" WHERE ");
            }
            builderC.append(" WHERE ");
            builderD.append(" WHERE ");
            where = true;
        }
        else {
            for (StringBuilder builder :builderCs) {
                builder.append(" AND ");
            }
            builderC.append(" AND ");
            builderD.append(" AND ");
        }

        if (currencyCode==null) {
            for (StringBuilder builder :builderCs) {
                builder.append(COL_CURRENCY_CODE + "=" + "\'")
                        .append(MainActivity.sCurrency.getCurrencyCode()).append("\'");
            }
            builderC.append(COL_CURRENCY_CODE + "=" + "\'")
                    .append(MainActivity.sCurrency.getCurrencyCode()).append("\'");
            builderD.append(COL_CURRENCY_CODE + "=" + "\'")
                    .append(MainActivity.sCurrency.getCurrencyCode()).append("\'");
        }
        else {
            for (StringBuilder builder :builderCs) {
                builder.append(COL_CURRENCY_CODE + "=" + "\'").append(currencyCode).append("\'");
            }
            builderC.append(COL_CURRENCY_CODE + "=" + "\'").append(currencyCode).append("\'");
            builderD.append(COL_CURRENCY_CODE + "=" + "\'").append(currencyCode).append("\'");
        }
    }

    public static void setCategoryCode(String categoryCode) {
        if (!where) {
            for (StringBuilder builder :builderCs) {
                builder.append(" WHERE ");
            }
            builderC.append(" WHERE ");
            builderD.append(" WHERE ");
            where = true;
        }
        else {
            for (StringBuilder builder :builderCs) {
                builder.append(" AND ");
            }
            builderC.append(" AND ");
            builderD.append(" AND ");
        }

        if (categoryCode != null) {
            for (StringBuilder builder :builderCs) {
                builder.append(COL_CATEGORY_CODE + "=").append(categoryCode);
            }
            builderC.append(COL_CATEGORY_CODE + "=").append(categoryCode);
            builderD.append(COL_CATEGORY_CODE + "=").append(categoryCode);
        }
    }

    public static void setMemo(String memo) {
        if (!where) {
            for (StringBuilder builder :builderCs) {
                builder.append(" WHERE ");
            }
            builderC.append(" WHERE ");
            builderD.append(" WHERE ");
            where = true;
        }
        else {
            for (StringBuilder builder :builderCs) {
                builder.append(" AND ");
            }
            builderC.append(" AND ");
            builderD.append(" AND ");
        }

        if (!"".equals(memo)) {
            for (StringBuilder builder :builderCs) {
                builder.append(COL_MEMO + "=" + "\'").append(memo).append("\'");
            }
            builderC.append(COL_MEMO + "=" + "\'").append(memo).append("\'");
            builderD.append(COL_MEMO + "=" + "\'").append(memo).append("\'");
        }
    }

    public static void setCsWhere(String col) {
        if (!where) {
            for (StringBuilder builder :builderCs) {
                builder.append(" WHERE ");
            }
            where = true;
        }
        else {
            for (StringBuilder builder :builderCs) {
                builder.append(" AND ");
            }
        }
        for (int i = 0; i < builderCs.length; ++i) {

            builderCs[i].append(col).append("=").append(i);
        }
    }

    public static void setCOrderBy(String colOrderBy, String direction) {
        builderC.append(" ORDER BY ").append(colOrderBy).append(" ").append(direction);
    }

    public static void setDOrderBy(String colOrderBy, String direction) {
        builderD.append(" ORDER BY ").append(colOrderBy).append(" ").append(direction);
    }

    public static void setCGroupBy(String colGroupBy) {
        builderC.append(" GROUP BY ").append(colGroupBy);
    }

    public static String[] buildQueryCs() {
        String[] out = new String[builderCs.length];
        for (int i = 0; i< builderCs.length; ++i) {
            out[i] = builderCs[i].toString();
        }
        return out;
    }

    public static String buildQueryC() {
        return builderC.toString();
    }

    public static String buildQueryD() {
        return builderD.toString();
    }

}
