package com.kakeibo;

import android.util.Log;

import com.kakeibo.db.ItemsDBAdapter;
import com.kakeibo.util.UtilCurrency;
import com.kakeibo.util.UtilDate;

import java.util.ArrayList;
import java.util.List;

public class Query {
    private static final String TAG = Query.class.getSimpleName();
    public static final int QUERY_TYPE_NEW = 0;
    public static final int QUERY_TYPE_SEARCH = 1;

    private String id;
    private int type;
    private String query;
    private String createDate;
    private String searchCriteria;

    private String _whereClauseDateRange="";
    private String _whereClauseAmountRange="";
    private String _whereClauseCategory="";
    private String _whereClauseMemo="";
    private List<Card> _lstCards;

    /*** Query type 0 ***/
    private int valY;//event_date
    private int valM;
    private int valD;
    /*** Query type 1 ***/
    private String valFromDate="";
    private String valToDate="";
    private String valMinAmount="";
    private String valMaxAmount="";
    private int valCategoryCode = 0;
    private String valCategory="";
    private String valMemo="";

    Query (int type){
        this.type = type;
        this.createDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB);
        _lstCards = new ArrayList<>();
        reset();
    }

    Query (String y, String m, String d, int format) {
        this.type = QUERY_TYPE_NEW;
        valY = Integer.parseInt(y);
        valM = Integer.parseInt(m);
        valD = Integer.parseInt(d);
        setValDate(y, m, d, format);
        reset();
    }

    public String getCreateDate() {
        return createDate;
    }

    public int getType() {
        return this.type;
    }

    public String getQuery() {
        return this.query;
    }

    public int getValY() {
        return valY;
    }

    public int getValM() {
        return valM;
    }

    public int getValD() {
        return valD;
    }

    public String getValFromDate() {
        return valFromDate;
    }

    public String getValToDate() {
        return valToDate;
    }

    public String getValFromToDate() {
        if (valFromDate.equals(valToDate)) {
            return valFromDate;
        }

        return valFromDate + "-" + valToDate;
    }

    public String getValMinAmount() {
        return valMinAmount;
    }

    public String getValMaxAmount() {
        return valMaxAmount;
    }

    public String getValMinMaxAmount() {
        if (valMinAmount.equals(valMaxAmount)) {
            return valMinAmount;
        }

        return valMinAmount + "-" + valMaxAmount;
    }

    public int getValCategoryCode() {
        return valCategoryCode;
    }

    public String getValCategory() {
        return valCategory;
    }

    public String getValMemo() {
        return valMemo;
    }

    public String getWhereClauseDateRange() {
        return _whereClauseDateRange;
    }

    public String getWhereClauseAmountRange() {
        return _whereClauseAmountRange;
    }

    public String getWhereClauseCategory() {
        return _whereClauseCategory;
    }

    public String getWhereClauseMemo() {
        return _whereClauseMemo;
    }

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public void setValDate(String fromDate, String toDate, int dateFormat) {
        valFromDate = fromDate;
        valToDate = toDate;

        String[] fromDateDB = UtilDate.convertDateFormat(fromDate, dateFormat, 3).split("-");
        String[] toDateDB = UtilDate.convertDateFormat(toDate, dateFormat, 3).split("-");
        String fromYMD = "\'" + fromDateDB[0] + "-" + fromDateDB[1] + "-" + fromDateDB[2] + "\'";
        String toYMD = "\'" + toDateDB[0] + "-" + toDateDB[1] + "-" + toDateDB[2] + "\'";

        _whereClauseDateRange = ItemsDBAdapter.COL_EVENT_DATE + " between " +
                " strftime('%Y-%m-%d', " + fromYMD + ") and strftime('%Y-%m-%d'," + toYMD + ")";

        /*** if _fromDate equals _toDate, cannot use between ***/
        if (UtilDate.compareDate(fromDate, toDate, dateFormat) == 0) {
            String str = UtilDate.convertDateFormat(fromDate, dateFormat, 3);
            String ymd = "\'" + str.split("-")[0] + "-" +
                    str.split("-")[1] + "-" +
                    str.split("-")[2] + "\'";
            _whereClauseDateRange = " strftime('%Y-%m-%d', " + ItemsDBAdapter.COL_EVENT_DATE + ") = " + ymd;
        }
    }

    public void setValDate(String y, String m, String d, int format) {
        valY = Integer.parseInt(y);
        valM = Integer.parseInt(m);
        valD = Integer.parseInt(d);

        String ymd = y + "-" + m + "-" + d;

        valFromDate = UtilDate.getDateFromDBDate(ymd, format);
        valToDate = valFromDate;

        String ym = "\'" + y + "-" + m + "\'";

        _whereClauseDateRange = " strftime('%Y-%m', " + ItemsDBAdapter.COL_EVENT_DATE + ") = " + ym;
    }

    public void setValAmount(String min, String max) {
        valMinAmount = min;
        valMaxAmount = max;

        _whereClauseAmountRange = ItemsDBAdapter.COL_AMOUNT + " >= " +
                String.valueOf(min) + " AND " +
                ItemsDBAdapter.COL_AMOUNT + " <= " + String.valueOf(max);
    }

    public void setCategory(int categoryCode, String category) {
        valCategoryCode = categoryCode;
        valCategory = category;

        _whereClauseCategory = ItemsDBAdapter.COL_CATEGORY_CODE + " = \'" + categoryCode + "\'";
    }

    public void setMemo(String memo) {
        valMemo = memo;

        _whereClauseMemo = ItemsDBAdapter.COL_MEMO + " = \'" + memo + "\'";
    }

    public void setListCards(List<Card> lstCards) {
        _lstCards = new ArrayList<>(lstCards.size());
        if (lstCards.contains(new Card(Card.TYPE_DATE_RANGE, 0))) {
            _lstCards.add(new Card(Card.TYPE_DATE_RANGE, 0));
        }
        if (lstCards.contains(new Card(Card.TYPE_AMOUNT_RANGE, 0))) {
            _lstCards.add(new Card(Card.TYPE_AMOUNT_RANGE, 0));
        }
        if (lstCards.contains(new Card(Card.TYPE_CATEGORY, 0))) {
            _lstCards.add(new Card(Card.TYPE_CATEGORY, 0));
        }
        if (lstCards.contains(new Card(Card.TYPE_MEMO, 0))) {
            _lstCards.add(new Card(Card.TYPE_MEMO, 0));
        }
    }

    public void buildQuery() {
        switch (type) {
            case QUERY_TYPE_NEW:
                buildQueryNew();
                break;
            case QUERY_TYPE_SEARCH:
                buildQuerySearch();
                break;
        }
    }

    private void buildQueryNew() {
        if ("".equals(_whereClauseDateRange)) {
            query = "SELECT * from " + ItemsDBAdapter.TABLE_ITEM +
                    " ORDER BY " + ItemsDBAdapter.COL_EVENT_DATE;
        } else {
            query = "SELECT * from " + ItemsDBAdapter.TABLE_ITEM + " WHERE" +
                    _whereClauseDateRange +
                    " ORDER BY " + ItemsDBAdapter.COL_EVENT_DATE;
        }

        searchCriteria = "Date Range: " + getValFromToDate();
    }

    private void buildQuerySearch() {
        Log.d(TAG, "buildQuerySearch()");

        StringBuilder stbCriteria = new StringBuilder();
        StringBuilder stbQuery = new StringBuilder();
        stbQuery.append("SELECT * from " + ItemsDBAdapter.TABLE_ITEM);

        if (!_lstCards.isEmpty()) {
            stbQuery.append(" WHERE ");
            if (_lstCards.contains(new Card(Card.TYPE_DATE_RANGE, 0))) {
                stbCriteria.append("Date Range: ");
                stbCriteria.append(getValFromToDate());
                stbCriteria.append("\n");

                stbQuery.append(_whereClauseDateRange);
                _lstCards.remove(new Card(Card.TYPE_DATE_RANGE, 0));
                if(!_lstCards.isEmpty()) stbQuery.append(" AND ");
            }
            if (_lstCards.contains(new Card(Card.TYPE_AMOUNT_RANGE, 0))) {
                stbCriteria.append("Amount Range: ");
                stbCriteria.append(getValMinMaxAmount());
                stbCriteria.append("\n");

                stbQuery.append(_whereClauseAmountRange);
                _lstCards.remove(new Card(Card.TYPE_AMOUNT_RANGE, 0));
                if(!_lstCards.isEmpty()) stbQuery.append(" AND ");
            }
            if (_lstCards.contains(new Card(Card.TYPE_CATEGORY, 0))) {
                stbCriteria.append("Category: ");
                stbCriteria.append(getValCategory());
                stbCriteria.append("\n");

                stbQuery.append(_whereClauseCategory);
                _lstCards.remove(new Card(Card.TYPE_CATEGORY, 0));
                if(!_lstCards.isEmpty()) stbQuery.append(" AND ");
            }
            if (_lstCards.contains(new Card(Card.TYPE_MEMO, 0))) {
                stbCriteria.append("Memo: ");
                stbCriteria.append(getValMemo());
                stbCriteria.append("\n");

                stbQuery.append(_whereClauseMemo);
            }
        }
        searchCriteria = stbCriteria.toString();

        stbQuery.append(" ORDER BY " + ItemsDBAdapter.COL_EVENT_DATE);
        query = stbQuery.toString();
    }

    private void reset() {
        _whereClauseDateRange ="";
        _whereClauseAmountRange ="";
        _whereClauseCategory ="";
        _whereClauseMemo ="";
    }
}
