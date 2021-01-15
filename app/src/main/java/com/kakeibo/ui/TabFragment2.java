//package com.kakeibo.ui;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.coordinatorlayout.widget.CoordinatorLayout;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.core.content.ContextCompat;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import com.kakeibo.R;
//import com.kakeibo.SubApp;
//import com.kakeibo.db.ItemDBAdapter;
//import com.kakeibo.ui.items.Balance;
//import com.kakeibo.util.UtilDate;
//import com.kakeibo.util.QueryBuilder;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//
//import javax.annotation.Nonnull;
//
///**
// * Created by T on 2015/09/14.
// */
//public class TabFragment2 extends Fragment implements ItemLoadListener {
//    private static final String TAG = TabFragment2.class.getSimpleName();
//    private static final int SWIPE_REFRESH_MILLI_SECOND = 400;
//
//    public static int REPORT_BY_DATE = 0;
//    public static int REPORT_BY_CATEGORY = 1;
//    public static int REPORT_BY_AMOUNT = 2; //todo
//
//    private static int _dateFormat;
//
//    private Activity _activity;
//    private CoordinatorLayout rootView;
//    private SwipeRefreshLayout srlReload;
//    private ImageButton btnPrev, btnNext, btnClose;
//    private Button btnDate;
//    private TextView txvIncome, txvExpense, txvBalance;
//
//    private FragmentManager _fragmentManager;
//    private FragmentTransaction _ftrDetail;
//    private TabFragment2C _tabFragment2C;
//    private TabFragment2D _tabFragment2D;
//
//    private static Query _query;
//    private static int _calMonth, _calYear;
//    private static String _eventDate;
//
//    private Balance _balance;
//
//    public static TabFragment2 newInstance() {
//        TabFragment2 tabFragment2 = new TabFragment2();
//        Bundle args = new Bundle();
//        tabFragment2.setArguments(args);
//        return tabFragment2;
//    }
//
//    @Override
//    public void onAttach(@Nonnull Context context) {
//        super.onAttach(context);
//        _dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format);
//
//        String todaysDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB);
//        String[] ymd = todaysDate.split("-");
//        _calYear = Integer.parseInt(ymd[0]);
//        _calMonth = Integer.parseInt(ymd[1]);
//
//        /*** making query ***/
//        _query = new Query(Query.QUERY_TYPE_NEW);
//        QueryBuilder.init(new ArrayList<>());
//        QueryBuilder.setDate(todaysDate, "");
//        QueryBuilder.setCGroupBy(ItemDBAdapter.COL_CATEGORY_CODE);
//        QueryBuilder.setCOrderBy(QueryBuilder.SUM_AMOUNT, QueryBuilder.DESC);
//        QueryBuilder.setCsWhere(ItemDBAdapter.COL_CATEGORY_CODE);
//        QueryBuilder.setDOrderBy(ItemDBAdapter.COL_EVENT_DATE, QueryBuilder.ASC);
//        QueryBuilder.build(_query);
//
//        /*** preparing fragment in detail view ***/
//        _fragmentManager = getChildFragmentManager();
//        _ftrDetail = _fragmentManager.beginTransaction();
//        _tabFragment2D = TabFragment2D.newInstance(this, _query);
//        _ftrDetail.replace(R.id.frl_tab2_container, _tabFragment2D);
//        _ftrDetail.addToBackStack(null);
//        _ftrDetail.commit();
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        _activity = getActivity();
//
//        View view = inflater.inflate(R.layout.fragment_report, container, false);
//
//        rootView = view.findViewById(R.id.col_root_fragment2);
//        srlReload = view.findViewById(R.id.srl_reload);
//        btnPrev = view.findViewById(R.id.btn_prev);
//        btnDate = view.findViewById(R.id.btn_date);
//        btnNext = view.findViewById(R.id.btn_next);
//        btnClose = view.findViewById(R.id.btn_exit_search_result);
//        txvIncome = view.findViewById(R.id.txv_income);
//        txvExpense = view.findViewById(R.id.txv_expense);
//        txvBalance = view.findViewById(R.id.txv_balance);
//        srlReload.setOnRefreshListener(()-> {
//            new Handler().postDelayed(()-> {
//                srlReload.setRefreshing(false);
//                reset();
//            }, SWIPE_REFRESH_MILLI_SECOND);
//        });
//        btnPrev.setOnClickListener(new ButtonClickListener());
//        btnDate.setOnClickListener(new ButtonClickListener());
//        btnNext.setOnClickListener(new ButtonClickListener());
//        btnClose.setOnClickListener(new ButtonClickListener());
//        btnDate.setOnLongClickListener(new ButtonLongClickListener());
//
//        reset();
//        return view;
//    }
//
//    /*** onResume will be called just upon viewpager page change ***/
//    @Override
//    public void onResume() {
//        super.onResume();
//
////        View view = (View) getView().getRootView().getWindowToken();
////        UtilKeyboard.hideKeyboardFrom(_activity, view);
//
////        ((InputMethodManager) _activity.getSystemService(Activity.INPUT_METHOD_SERVICE))
////                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
//    }
//
////    @Override
////    public void onPause() {
////        super.onPause();
////        UtilKeyboard.hideSoftKeyboard(_activity);
////    }
//
//    /***
//     * called when btnNext or btnPrev is clicked
//     * builds query with new year and month value
//     * ***/
//    private void buildQuery() {
//        String y = String.valueOf(_calYear);
//        String m = UtilDate.convertMtoMM(_calMonth);
//
//        _query = new Query(Query.QUERY_TYPE_NEW);
//        QueryBuilder.init(new ArrayList<>());
//        QueryBuilder.setDate(y+"-"+m+"-01", ""); //todo settings start date of the month
//        QueryBuilder.setCGroupBy(ItemDBAdapter.COL_CATEGORY_CODE);
//        QueryBuilder.setCOrderBy(QueryBuilder.SUM_AMOUNT, QueryBuilder.DESC);
//        QueryBuilder.setCsWhere(ItemDBAdapter.COL_CATEGORY_CODE);
//        QueryBuilder.setDOrderBy(ItemDBAdapter.COL_EVENT_DATE, QueryBuilder.ASC);
//        QueryBuilder.build(_query);
//    }
//
//    class ButtonLongClickListener implements View.OnLongClickListener {
//        @Override
//        public boolean onLongClick (View view) {
//            return true;
//        }
//    }
//
//    class ButtonClickListener implements View.OnClickListener {
//        public void onClick(View view) {
//            switch(view.getId()) {
//                case R.id.btn_date:
//                    toggleViews();
//                    break;
//                case R.id.btn_prev:
//                    _calMonth--;
//                    if(_calMonth <=0) {
//                        _calMonth = 12;
//                        _calYear--;
//                        if (_calYear <= 0) {
//                            _calYear = Calendar.getInstance().get(Calendar.YEAR);
//                        }
//                    }
//                    btnDate.setText(getTextBtnDate());
//                    buildQuery();
//                    if (_fragmentManager.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2C) {
//                        _tabFragment2C = (TabFragment2C) _fragmentManager.findFragmentById(R.id.frl_tab2_container);
//                        _tabFragment2C.setQuery(_query);
//                        _tabFragment2C.loadItemsOrderByCategory();
//                    } else if (_fragmentManager.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2D) {
//                        _tabFragment2D = (TabFragment2D) _fragmentManager.findFragmentById(R.id.frl_tab2_container);
//                        _tabFragment2D.setQuery(_query);
//                        _tabFragment2D.loadItemsOrderByDate();
//                    }
//                    break;
//                case R.id.btn_next:
//                    _calMonth++;
//                    if(_calMonth > 12) {
//                        _calMonth = 1;
//                        _calYear++;
//                    }
//                    btnDate.setText(getTextBtnDate());
//                    buildQuery();
//                    if (_fragmentManager.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2C) {
//                        _tabFragment2C = (TabFragment2C) _fragmentManager.findFragmentById(R.id.frl_tab2_container);
//                        _tabFragment2C.setQuery(_query);
//                        _tabFragment2C.loadItemsOrderByCategory();
//                    } else if (_fragmentManager.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2D) {
//                        _tabFragment2D = (TabFragment2D) _fragmentManager.findFragmentById(R.id.frl_tab2_container);
//                        _tabFragment2D.setQuery(_query);
//                        _tabFragment2D.loadItemsOrderByDate();
//                    }
//                    break;
//                case R.id.btn_exit_search_result:
//                    exitSearchResult();
//                    break;
//            }
//        }
//    }
//
//    private String getTextBtnDate() {
//        int year = _calYear;
//        int month = _calMonth;
//
//        String str;
//        switch (_dateFormat) {
//            case 1: // MDY
//            case 2: // DMY
//                str = (UtilDate.convertMtoMM(month) + "/" + year);
//                break;
//            default:  // YMD
//                str = (year + "/" + UtilDate.convertMtoMM(month));
//        }
//
//        return str;
//    }
//
//    private void makeBalanceTable(){
//        txvIncome.setText(String.valueOf(_balance.getIncome()));
//        txvExpense.setText(String.valueOf(_balance.getExpense()));
//
//        if (_balance.inMinusOut() < 0) {
//            txvBalance.setTextColor(ContextCompat.getColor(_activity, R.color.colorRed));
//            txvBalance.setText(String.valueOf(_balance.getBalance()));
//        }
//        else if (_balance.inMinusOut() > 0) {
//            txvBalance.setTextColor(ContextCompat.getColor(_activity, R.color.colorBlue));
//            String str = "+" + _balance.getBalance();
//            txvBalance.setText(str);
//        }
//        else {
//            txvBalance.setTextColor(ContextCompat.getColor(_activity, R.color.colorBlack));
//            txvBalance.setText(String.valueOf(_balance.getBalance()));
//        }
//    }
//
//    private void reset() {
//        Log.d(TAG, "reset() called");
//
//        switch (_query.getType()) {
//            case Query.QUERY_TYPE_NEW:
//                if (_eventDate == null || "".equals(_eventDate)) {
//                    Calendar cal = Calendar.getInstance();
//                    _calMonth = cal.get(Calendar.MONTH) + 1;
//                    _calYear = cal.get(Calendar.YEAR);
//                } else {
//                    _calMonth = Integer.parseInt(_eventDate.split("-")[1]);
//                    _calYear = Integer.parseInt(_eventDate.split("-")[0]);
//                }
//
//                btnDate.setText(getTextBtnDate());
//                btnNext.setVisibility(View.VISIBLE);
//                btnPrev.setVisibility(View.VISIBLE);
//                btnClose.setVisibility(View.GONE);
//                rootView.setBackgroundColor(getResources().getColor(R.color.colorBackground));
//                break;
//            case Query.QUERY_TYPE_SEARCH:
//                btnDate.setText(getString(R.string.search_result));
//                btnNext.setVisibility(View.INVISIBLE);
//                btnPrev.setVisibility(View.GONE);
//                btnClose.setVisibility(View.VISIBLE);
//                rootView.setBackgroundColor(getResources().getColor(R.color.colorBackground_search));
//                break;
//        }
//    }
//
//    /* Called from MainActivity */
//    public void focusOnSavedItem(Query query, String eventDate) {
//        _query = query;
//        _eventDate = eventDate;
//        reset();
//
//        _ftrDetail = _fragmentManager.beginTransaction();
//        _tabFragment2D = TabFragment2D.newInstance(this, query);
//        _ftrDetail.replace(R.id.frl_tab2_container, _tabFragment2D);
//        _ftrDetail.addToBackStack(null);
//        _ftrDetail.commit();
//    }
//
//    /* Called from MainActivity */
//    public void onSearch(Query query, String fromDate, String toDate) {
//        _query = query;
//        reset();
//
//        _ftrDetail = _fragmentManager.beginTransaction();
//        _tabFragment2D = TabFragment2D.newInstance(this, query);
//        _ftrDetail.replace(R.id.frl_tab2_container, _tabFragment2D);
//        _ftrDetail.addToBackStack(null);
//        _ftrDetail.commit();
//    }
//
//    @Override
//    public void onItemsLoaded(Balance balance) {
//        Log.d(TAG, "onItemsLoaded() called");
//        this._balance = balance;
//
//        makeBalanceTable();
//
//        if (_fragmentManager.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2D) {
//            if (_eventDate == null || "".equals(_eventDate)) {
//                _eventDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB);
//            }
//            _tabFragment2D.focusOnSavedItem(_eventDate);
//        }
//    }
//
//    private void toggleViews() {
//        if (_fragmentManager.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2C) {
//            Log.d(TAG, "2c detail fragment is visible");
//
//            _ftrDetail = _fragmentManager.beginTransaction();
//            _tabFragment2D = TabFragment2D.newInstance(TabFragment2.this, _query);
//            _ftrDetail.replace(R.id.frl_tab2_container, _tabFragment2D);
//            _ftrDetail.addToBackStack(null);
//            _ftrDetail.commit();
//        } else if (_fragmentManager.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2D) {
//            Log.d(TAG, "2d detail fragment is visible");
//
//            _ftrDetail = _fragmentManager.beginTransaction();
//            _tabFragment2C = TabFragment2C.newInstance(TabFragment2.this, _query);
//            _ftrDetail.replace(R.id.frl_tab2_container, _tabFragment2C);
//            _ftrDetail.addToBackStack(null);
//            _ftrDetail.commit();
//        }
//    }
//
//    public void export() {
//        if (_fragmentManager.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2C) {
//            TabFragment2C f = (TabFragment2C) _fragmentManager.findFragmentById(R.id.frl_tab2_container);
//            f.export();
//        } else if (_fragmentManager.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2D) {
//            TabFragment2D f = (TabFragment2D) _fragmentManager.findFragmentById(R.id.frl_tab2_container);
//            f.export();
//        }
//    }
//
//    private boolean exitSearchResult() {
//        AlertDialog.Builder dialogSaveSearch = new AlertDialog.Builder(_activity);
//        dialogSaveSearch.setIcon(R.mipmap.ic_mikan);
//        dialogSaveSearch.setTitle(getString(R.string.returning_to_monthly_report));
//        dialogSaveSearch.setMessage(getString(R.string.msg_exit_search));
//        dialogSaveSearch.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String todaysDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB);
//                _query = new Query(Query.QUERY_TYPE_NEW);
//                QueryBuilder.init(new ArrayList<>());
//                QueryBuilder.setDate(todaysDate, "");
//                QueryBuilder.setCGroupBy(ItemDBAdapter.COL_CATEGORY_CODE);
//                QueryBuilder.setCOrderBy(QueryBuilder.SUM_AMOUNT, QueryBuilder.DESC);
//                QueryBuilder.setCsWhere(ItemDBAdapter.COL_CATEGORY_CODE);
//                QueryBuilder.setDOrderBy(ItemDBAdapter.COL_EVENT_DATE, QueryBuilder.ASC);
//                QueryBuilder.build(_query);
//
//                reset();
//                if (_fragmentManager.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2C) {
//                    _tabFragment2C = (TabFragment2C) _fragmentManager.findFragmentById(R.id.frl_tab2_container);
//                    _tabFragment2C.setQuery(_query);
//                    _tabFragment2C.loadItemsOrderByCategory();
//                } else if (_fragmentManager.findFragmentById(R.id.frl_tab2_container) instanceof TabFragment2D) {
//                    _tabFragment2D = (TabFragment2D) _fragmentManager.findFragmentById(R.id.frl_tab2_container);
//                    _tabFragment2D.setQuery(_query);
//                    _tabFragment2D.loadItemsOrderByDate();
//                }
//            }
//        });
//        dialogSaveSearch.show();
//
//        return true;
//    }
//}
