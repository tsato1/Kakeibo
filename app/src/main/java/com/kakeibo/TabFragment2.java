package com.kakeibo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kakeibo.db.ItemsDBAdapter;
import com.kakeibo.util.UtilDate;
import com.kakeibo.util.UtilQuery;

import java.util.Calendar;

/**
 * Created by T on 2015/09/14.
 */
public class TabFragment2 extends Fragment implements ItemLoadListener {
    private static final String TAG = TabFragment2.class.getSimpleName();

    public static int REPORT_BY_DATE = 0;
    public static int REPORT_BY_CATEGORY = 1;
    public static int REPORT_BY_AMOUNT = 2; //todo

    private Activity _activity;
    private View _view;
    private CoordinatorLayout rootView;
    private ImageButton btnPrev, btnNext;
    private Button btnDate;
    private TextView txvIncome, txvExpense, txvBalance;
    private FloatingActionButton fabDiscard;

    private FragmentManager _cfmDetail;
    private FragmentTransaction _ftrDetail;
    private TabFragment2C _tabFragment2C;
    private TabFragment2D _tabFragment2D;

    private static Query _query;
    private static int _calMonth, _calYear;
    private static String _eventDate;

    private Balance _balance;

    public static TabFragment2 newInstance() {
        TabFragment2 tabFragment2 = new TabFragment2();
        Bundle args = new Bundle();
        tabFragment2.setArguments(args);
        return tabFragment2;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String todaysDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB);
        String[] ymd = todaysDate.split("-");
        _calYear = Integer.parseInt(ymd[0]);
        _calMonth = Integer.parseInt(ymd[1]);

        /*** making query ***/
        _query = new Query(Query.QUERY_TYPE_NEW);
        UtilQuery.init();
        UtilQuery.setDate(todaysDate, "");
        UtilQuery.setCGroupBy(ItemsDBAdapter.COL_CATEGORY_CODE);
        UtilQuery.setCOrderBy(UtilQuery.SUM_AMOUNT, UtilQuery.DESC);
        UtilQuery.setCsWhere(ItemsDBAdapter.COL_CATEGORY_CODE);
        UtilQuery.setDOrderBy(ItemsDBAdapter.COL_EVENT_DATE, UtilQuery.ASC);
        _query.setQueryC(UtilQuery.buildQueryC());
        _query.setQueryCs(UtilQuery.buildQueryCs());
        _query.setQueryD(UtilQuery.buildQueryD());

        /*** preparing fragment in detail view ***/
        _cfmDetail = getChildFragmentManager();
        _ftrDetail = _cfmDetail.beginTransaction();
        _tabFragment2D = TabFragment2D.newInstance(this, _query);
        _ftrDetail.add(R.id.frl_tab2_container, _tabFragment2D);
        _ftrDetail.addToBackStack(null);
        _ftrDetail.commit();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView() called");
        _activity = getActivity();
        _view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        findViews();

        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reset();
    }

    void findViews(){
        rootView = _view.findViewById(R.id.col_root_fragment2);
        btnPrev = _view.findViewById(R.id.btn_prev);
        btnDate = _view.findViewById(R.id.btn_date);
        btnNext = _view.findViewById(R.id.btn_next);
        txvIncome = _view.findViewById(R.id.txv_income);
        txvExpense = _view.findViewById(R.id.txv_expense);
        txvBalance = _view.findViewById(R.id.txv_balance);

        btnPrev.setOnClickListener(new ButtonClickListener());
        btnDate.setOnClickListener(new ButtonClickListener());
        btnNext.setOnClickListener(new ButtonClickListener());
        btnDate.setOnLongClickListener(new ButtonLongClickListener());
    }

    /***
     * called when btnNext or btnPrev is clicked
     * builds query with new year and month value
     * ***/
    private void buildQuery() {
        String y = String.valueOf(_calYear);
        String m = UtilDate.convertMtoMM(_calMonth);

        _query = new Query(Query.QUERY_TYPE_NEW);
        UtilQuery.init();
        UtilQuery.setDate(y+"-"+m+"-01", "");
        UtilQuery.setCGroupBy(ItemsDBAdapter.COL_CATEGORY_CODE);
        UtilQuery.setCOrderBy(UtilQuery.SUM_AMOUNT, UtilQuery.DESC);
        UtilQuery.setCsWhere(ItemsDBAdapter.COL_CATEGORY_CODE);
        UtilQuery.setDOrderBy(ItemsDBAdapter.COL_EVENT_DATE, UtilQuery.ASC);
        _query.setQueryC(UtilQuery.buildQueryC());
        _query.setQueryCs(UtilQuery.buildQueryCs());
        _query.setQueryD(UtilQuery.buildQueryD());
    }

    class ButtonLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick (View view) {
            AlertDialog.Builder dialogSaveSearch = new AlertDialog.Builder(_activity);
            dialogSaveSearch.setIcon(R.mipmap.ic_mikan);
            dialogSaveSearch.setTitle(getString(R.string.title_returning_to_monthly_report));
            dialogSaveSearch.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String todaysDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB);
                    _query = new Query(Query.QUERY_TYPE_NEW);
                    UtilQuery.init();
                    UtilQuery.setDate(todaysDate, "");
                    UtilQuery.setCGroupBy(ItemsDBAdapter.COL_CATEGORY_CODE);
                    UtilQuery.setCOrderBy(UtilQuery.SUM_AMOUNT, UtilQuery.DESC);
                    UtilQuery.setCsWhere(ItemsDBAdapter.COL_CATEGORY_CODE);
                    UtilQuery.setDOrderBy(ItemsDBAdapter.COL_EVENT_DATE, UtilQuery.ASC);
                    _query.setQueryC(UtilQuery.buildQueryC());
                    _query.setQueryCs(UtilQuery.buildQueryCs());
                    _query.setQueryD(UtilQuery.buildQueryD());

                    reset();
                    if (_cfmDetail.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2C) {
                        _tabFragment2C = (TabFragment2C) _cfmDetail.findFragmentById(R.id.frl_tab2_container);
                        _tabFragment2C.setQuery(_query);
                        _tabFragment2C.loadItemsOrderByCategory();
                    } else if (_cfmDetail.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2D) {
                        _tabFragment2D = (TabFragment2D) _cfmDetail.findFragmentById(R.id.frl_tab2_container);
                        _tabFragment2D.setQuery(_query);
                        _tabFragment2D.loadItemsOrderByDate();
                    }
                }
            });
            dialogSaveSearch.show();

            return true;
        }
    }

    class ButtonClickListener implements View.OnClickListener {
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btn_date:

                    break;
                case R.id.btn_prev:
                    _calMonth--;
                    if(_calMonth <=0) {
                        _calMonth = 12;
                        _calYear--;
                        if (_calYear <= 0) {
                            _calYear = Calendar.getInstance().get(Calendar.YEAR);
                        }
                    }
                    btnDate.setText(getTextBtnDate());
                    buildQuery();
                    if (_cfmDetail.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2C) {
                        _tabFragment2C = (TabFragment2C) _cfmDetail.findFragmentById(R.id.frl_tab2_container);
                        _tabFragment2C.setQuery(_query);
                        _tabFragment2C.loadItemsOrderByCategory();
                    } else if (_cfmDetail.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2D) {
                        _tabFragment2D = (TabFragment2D) _cfmDetail.findFragmentById(R.id.frl_tab2_container);
                        _tabFragment2D.setQuery(_query);
                        _tabFragment2D.loadItemsOrderByDate();
                    }
                    break;
                case R.id.btn_next:
                    _calMonth++;
                    if(_calMonth > 12) {
                        _calMonth = 1;
                        _calYear++;
                    }
                    btnDate.setText(getTextBtnDate());
                    buildQuery();
                    if (_cfmDetail.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2C) {
                        _tabFragment2C = (TabFragment2C) _cfmDetail.findFragmentById(R.id.frl_tab2_container);
                        _tabFragment2C.setQuery(_query);
                        _tabFragment2C.loadItemsOrderByCategory();
                    } else if (_cfmDetail.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2D) {
                        _tabFragment2D = (TabFragment2D) _cfmDetail.findFragmentById(R.id.frl_tab2_container);
                        _tabFragment2D.setQuery(_query);
                        _tabFragment2D.loadItemsOrderByDate();
                    }
                    break;
            }
        }
    }

    public String getTextBtnDate() {
        int year = _calYear;
        int month = _calMonth;

        String str;
        switch (MainActivity.sDateFormat) {
            case 1: // MDY
            case 2: // DMY
                str = (UtilDate.convertMtoMM(month) + "/" + year);
                break;
            default:  // YMD
                str = (year + "/" + UtilDate.convertMtoMM(month));
        }

        return str;
    }

    public void makeBalanceTable(){
        txvIncome.setText(String.valueOf(_balance.getIncome()));
        txvExpense.setText(String.valueOf(_balance.getExpense()));

        if (_balance.inMinusOut() < 0) {
            txvBalance.setTextColor(ContextCompat.getColor(_activity, R.color.colorRed));
            txvBalance.setText(String.valueOf(_balance.getBalance()));
        }
        else if (_balance.inMinusOut() > 0) {
            txvBalance.setTextColor(ContextCompat.getColor(_activity, R.color.colorBlue));
            String str = "+" + String.valueOf(_balance.getBalance());
            txvBalance.setText(str);
        }
        else {
            txvBalance.setTextColor(ContextCompat.getColor(_activity, R.color.colorBlack));
            txvBalance.setText(String.valueOf(_balance.getBalance()));
        }
    }

    public void reset() {
        Log.d(TAG, "reset() called");

        switch (_query.getType()) {
            case Query.QUERY_TYPE_NEW:
                if (_eventDate == null || "".equals(_eventDate)) {
                    Calendar cal = Calendar.getInstance();
                    _calMonth = cal.get(Calendar.MONTH) + 1;
                    _calYear = cal.get(Calendar.YEAR);
                } else {
                    _calMonth = Integer.parseInt(_eventDate.split("-")[1]);
                    _calYear = Integer.parseInt(_eventDate.split("-")[0]);
                }

                btnDate.setText(getTextBtnDate());
                btnNext.setVisibility(View.VISIBLE);
                btnPrev.setVisibility(View.VISIBLE);
                rootView.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                break;
            case Query.QUERY_TYPE_SEARCH:
                btnDate.setText(getString(R.string.title_search_result));
                btnNext.setVisibility(View.INVISIBLE);
                btnPrev.setVisibility(View.INVISIBLE);
                rootView.setBackgroundColor(getResources().getColor(R.color.colorBackground_search));
                break;
        }
    }

    public void focusOnSavedItem(Query query, String eventDate) {
        _query = query;
        _eventDate = eventDate;
        reset();

        _ftrDetail = _cfmDetail.beginTransaction();
        _tabFragment2D = TabFragment2D.newInstance(this, query);
        _ftrDetail.replace(R.id.frl_tab2_container, _tabFragment2D);
        _ftrDetail.addToBackStack(null);
        _ftrDetail.commit();
    }

    public void onSearch(Query query, String fromDate, String toDate) {
        _query = query;
        reset();

        _ftrDetail = _cfmDetail.beginTransaction();
        _tabFragment2D = TabFragment2D.newInstance(this, query);
        _ftrDetail.replace(R.id.frl_tab2_container, _tabFragment2D);
        _ftrDetail.addToBackStack(null);
        _ftrDetail.commit();
    }

    @Override
    public void onItemsLoaded(Balance balance) {
        Log.d(TAG, "onItemsLoaded() called");
        this._balance = balance;

        makeBalanceTable();

        if (_cfmDetail.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2D) {
            if (_eventDate == null || "".equals(_eventDate)) {
                _eventDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB);
            }
            _tabFragment2D.focusOnSavedItem(_eventDate);
        }
    }

    public void toggleViews() {
        if (_cfmDetail.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2C) {
            Log.d(TAG, "2c detail fragment is visible");

            _ftrDetail = _cfmDetail.beginTransaction();
            _tabFragment2D = TabFragment2D.newInstance(TabFragment2.this, _query);
            _ftrDetail.replace(R.id.frl_tab2_container, _tabFragment2D);
            _ftrDetail.addToBackStack(null);
            _ftrDetail.commit();
        } else if (_cfmDetail.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2D) {
            Log.d(TAG, "2d detail fragment is visible");

            _ftrDetail = _cfmDetail.beginTransaction();
            _tabFragment2C = TabFragment2C.newInstance(TabFragment2.this, _query);
            _ftrDetail.replace(R.id.frl_tab2_container, _tabFragment2C);
            _ftrDetail.addToBackStack(null);
            _ftrDetail.commit();
        }
    }

    public void export() {
        if (_cfmDetail.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2C) {
            TabFragment2C f = (TabFragment2C) _cfmDetail.findFragmentById(R.id.frl_tab2_container);
            f.export();
        } else if (_cfmDetail.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2D) {
            TabFragment2D f = (TabFragment2D) _cfmDetail.findFragmentById(R.id.frl_tab2_container);
            f.export();
        }
    }
}
