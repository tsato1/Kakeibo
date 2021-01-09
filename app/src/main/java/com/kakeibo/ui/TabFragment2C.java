package com.kakeibo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.kakeibo.Constants;
import com.kakeibo.R;
import com.kakeibo.SubApp;
import com.kakeibo.data.ItemStatus;
import com.kakeibo.databinding.FragmentReportCBinding;
import com.kakeibo.db.ItemDBAdapter;
import com.kakeibo.export.ExportActivity;
import com.kakeibo.ui.categories.CategoryStatusViewModel;
import com.kakeibo.ui.items.Balance;
import com.kakeibo.ui.items.ItemDetailListAdapter;
import com.kakeibo.util.UtilKeyboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TabFragment2C extends Fragment implements OnChartValueSelectedListener {
    private final static String TAG = TabFragment2C.class.getSimpleName();

    private final static int PIE_GRAPH_THICKNESS = 120;

    private static Query _query;
    private static ItemLoadListener _itemLoadListener;

    private static int _fractionDigits;

    private Activity _activity;
    private ItemDBAdapter _itemDbAdapter;
    private StringBuilder _stringBuilder;
    private Balance _balance;
    private HorizontalBarChart _horizontalBarChart;
    private PieGraph _inPieGraph;
    private PieGraph _exPieGraph;
    private List<ItemStatus> _itemCategoryInList;
    private List<ItemStatus> _itemCategoryExList;
    private ItemDetailListAdapter _itemListInAdapter;
    private ItemDetailListAdapter _itemListExAdapter;
    private ListView _lsvCategoryIn;
    private ListView _lsvCategoryEx;

    public static TabFragment2C newInstance(ItemLoadListener itemLoadListener, Query query) {
        TabFragment2C tabFragment2C = new TabFragment2C();
        Bundle args = new Bundle();
        args.putParcelable("query", query);
        _query = query;
        _itemLoadListener = itemLoadListener;
        tabFragment2C.setArguments(args);
        return tabFragment2C;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _activity = getActivity();
        _fractionDigits = SubApp.getFractionDigits(R.string.pref_key_fraction_digits);

        BillingViewModel billingViewModel =
                ViewModelProviders.of(requireActivity()).get(BillingViewModel.class);
        SubscriptionStatusViewModel subscriptionViewModel =
                ViewModelProviders.of(requireActivity()).get(SubscriptionStatusViewModel.class);
        CategoryStatusViewModel categoryStatusViewModel =
                ViewModelProviders.of(requireActivity()).get(CategoryStatusViewModel.class);

        FragmentReportCBinding fragmentBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_report_c, container, false);
        fragmentBinding.setLifecycleOwner(this);
        fragmentBinding.setBillingViewModel(billingViewModel);
        fragmentBinding.setSubscriptionViewModel(subscriptionViewModel);
        fragmentBinding.setCategoryStatusViewModel(categoryStatusViewModel);
        View view = fragmentBinding.getRoot();

        _horizontalBarChart = view.findViewById(R.id.horizontal_bar_chart);
        _inPieGraph = view.findViewById(R.id.pie_graph_income);
        _exPieGraph = view.findViewById(R.id.pie_graph_expense);
        _lsvCategoryIn = view.findViewById(R.id.lsv_income);
        _lsvCategoryEx = view.findViewById(R.id.lsv_expense);
        setUpGraphes();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            _query = bundle.getParcelable("query");
        }

        loadItemsOrderByCategory(); /*** <- to handle come back from settings ***/
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        UtilKeyboard.hideSoftKeyboard(_activity);
//    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpGraphes() {
        _horizontalBarChart.setDrawBarShadow(false);
        _horizontalBarChart.setDrawValueAboveBar(true);
        _horizontalBarChart.setDrawGridBackground(false);
        _horizontalBarChart.setMaxVisibleValueCount(2);//income and expense
        _horizontalBarChart.setFitBars(true);
        _horizontalBarChart.setHighlightPerDragEnabled(false);
        _horizontalBarChart.setHighlightPerTapEnabled(false);
        _horizontalBarChart.setDoubleTapToZoomEnabled(false);
        _horizontalBarChart.setNoDataTextColor(R.color.colorBlack);
        _horizontalBarChart.setPinchZoom(false);
        _horizontalBarChart.getDescription().setEnabled(false);
        _horizontalBarChart.setOnChartValueSelectedListener(this);
        _horizontalBarChart.animateY(getResources().getInteger(R.integer.chart_animation_milli_seconds));

        XAxis xl = _horizontalBarChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(10f);
        xl.setEnabled(false);

        YAxis yl = _horizontalBarChart.getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis yr = _horizontalBarChart.getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = _horizontalBarChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setFormSize(8f);
//        l.setXEntrySpace(5f);
        l.setEnabled(false);

        _inPieGraph.setThickness(PIE_GRAPH_THICKNESS);
        _exPieGraph.setThickness(PIE_GRAPH_THICKNESS);

        ViewCompat.setNestedScrollingEnabled(_lsvCategoryIn, true);
        ViewCompat.setNestedScrollingEnabled(_lsvCategoryEx, true);
        _lsvCategoryIn.setOnTouchListener((View v, MotionEvent event)-> {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
        });
        _lsvCategoryEx.setOnTouchListener((View v, MotionEvent event)-> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        });

        _lsvCategoryIn.setOnItemClickListener(new ItemClickListener());
        _lsvCategoryEx.setOnItemClickListener(new ItemClickListener());

        _stringBuilder = new StringBuilder();
        _itemDbAdapter = new ItemDBAdapter();
        _itemCategoryInList = new ArrayList<>();
        _itemCategoryExList = new ArrayList<>();
        _itemListInAdapter = new ItemDetailListAdapter(_activity, 0, _itemCategoryInList);
        _itemListExAdapter = new ItemDetailListAdapter(_activity, 0, _itemCategoryExList);
        _lsvCategoryIn.setAdapter(_itemListInAdapter);
        _lsvCategoryEx.setAdapter(_itemListExAdapter);
    }

    private void setData() {
        TypedValue outValue = new TypedValue();

        getResources().getValue(R.dimen.horizontal_bar_bar_width, outValue, true);
        float barWidth = outValue.getFloat();

        getResources().getValue(R.dimen.horizontal_bar_bar_space, outValue, true);
        float spaceForBar = outValue.getFloat();

        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new BarEntry(0 * spaceForBar, _balance.getIncome().floatValue()));
        values.add(new BarEntry(1 * spaceForBar, _balance.getExpense().floatValue()));

        BarDataSet income;
        BarDataSet expense;

        if (_horizontalBarChart.getData() != null && _horizontalBarChart.getData().getDataSetCount() > 0) {
            _horizontalBarChart.invalidate();
            income = (BarDataSet) _horizontalBarChart.getData().getDataSetByIndex(0);
            income.setValues(values);
            expense = (BarDataSet) _horizontalBarChart.getData().getDataSetByIndex(1);
            expense.setValues(values);
            _horizontalBarChart.getData().notifyDataChanged();
            _horizontalBarChart.notifyDataSetChanged();
        } else {
            income = new BarDataSet(values, getString(R.string.income));
            expense = new BarDataSet(values, getString(R.string.expense_colon));

            income.setDrawIcons(false);
            expense.setDrawIcons(false);
            expense.setColors(getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.colorAccent));

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(income);
            dataSets.add(expense);

            BarData data = new BarData(dataSets);
            getResources().getValue(R.dimen.horizontal_bar_text_size, outValue, true);
            data.setValueTextSize(outValue.getFloat());
            data.setBarWidth(barWidth);
            _horizontalBarChart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null) return;

        _horizontalBarChart.getBarBounds((BarEntry) e, new RectF());

        MPPointF position = _horizontalBarChart.getPosition(e, _horizontalBarChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency());

        PopupWindow popup = new PopupWindow(_activity);
        View layout = getLayoutInflater().inflate(R.layout.popup_bar_chart, null);
        popup.setContentView(layout);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAsDropDown(_horizontalBarChart);
//        popup.showAsDropDown(_view);

        MPPointF.recycleInstance(position);
    }
    @Override
    public void onNothingSelected() {}

    class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lsvCat = (ListView) parent;
            ItemStatus tmp = (ItemStatus) lsvCat.getItemAtPosition(position);

            List<ItemStatus> searchResultList = new ArrayList<>();
            searchResultList.clear();

            Map<Integer, String> queries = _query.getQueryCs();

            Log.d(TAG, "loadItems: " + queries.get(tmp.getCategoryCode()));

//            _itemDbAdapter.open();
//            Cursor c = _itemDbAdapter.getItemsByRawQuery(queries.get(tmp.getCategoryCode()));
//
//            if (c.moveToFirst()) {
//                do {
//                    ItemStatus itemStatus = new ItemStatus(
//                            c.getInt(c.getColumnIndex(ItemDBAdapter.COL_ID)),
//                            c.getLong(c.getColumnIndex(ItemDBAdapter.COL_AMOUNT)),
//                            "",
//                            _fractionDigits,
//                            c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)),
//                            c.getString(c.getColumnIndex(ItemDBAdapter.COL_MEMO)),
//                            c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE)),
//                            c.getString(c.getColumnIndex(ItemDBAdapter.COL_UPDATE_DATE))
//                    );
//
//                    searchResultList.add(itemStatus);
//                } while (c.moveToNext());
//            }
//            _itemDbAdapter.close();
//
//            CategoryDetailListAdapter categoryDetailListAdapter =
//                    new CategoryDetailListAdapter(_activity, 0, searchResultList);
//            ListView listView = new ListView(_activity);
//            listView.setAdapter(categoryDetailListAdapter);
//            AlertDialog.Builder dialog = new AlertDialog.Builder(_activity);
//            dialog.setIcon(R.mipmap.ic_mikan);
//            dialog.setTitle(UtilCategory.getCategoryStr(getContext(), tmp.getCategoryCode()));
//            dialog.setPositiveButton(R.string.ok, (DialogInterface d, int which) -> { });
//            dialog.setView(listView).create();
//            dialog.show();
        }
    }

    public void setQuery (Query query) {
        _query = query;
    }

    protected void loadItemsOrderByCategory () {
        Log.d(TAG, "loadItemsOrderByCategory() "+ _query.getQueryC());

        _balance = Balance.newInstance(_fractionDigits);

        _itemCategoryInList.clear();
        _itemCategoryExList.clear();
//        _itemDbAdapter.open();
//        Cursor c = _itemDbAdapter.getItemsByRawQuery(_query.getQueryC());
//
//        if (c!=null && c.moveToFirst()) {
//            BigDecimal balanceDay = new BigDecimal(0)
//                    .setScale(_fractionDigits, RoundingMode.UNNECESSARY);
//
//            do {
//                ItemStatus itemStatus = new ItemStatus(
//                        0,
//                        c.getLong(c.getColumnIndex("SUM(amount)")),
//                        "",
//                        _fractionDigits,
//                        c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)),
//                        "",
//                        "",
//                        ""
//                );
//
//                if (UtilCategory.getCategoryColor(_activity, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_INCOME) {
////todo should be disposable                if(c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)) == 0) {
//                    _balance.addIncome(itemStatus.getAmount());
//                    balanceDay = balanceDay.add(itemStatus.getAmount());
//                    _itemCategoryInList.add(itemStatus);
//                } else if (UtilCategory.getCategoryColor(_activity, itemStatus.getCategoryCode())==UtilCategory.CATEGORY_COLOR_EXPENSE) {
//                    _balance.addExpense(itemStatus.getAmount());
//                    balanceDay = balanceDay.subtract(itemStatus.getAmount());
//                    _itemCategoryExList.add(itemStatus);
//                }
//            } while (c.moveToNext());
//        }
//
//        _itemDbAdapter.close();

        _itemListInAdapter.notifyDataSetChanged();
        _itemListExAdapter.notifyDataSetChanged();
        adjustItemCategoryListViewHeight();
        calculatePercentage();
        makePieGraph();
        setData();
        _itemLoadListener.onItemsLoaded(_balance);
    }

    private void adjustItemCategoryListViewHeight() {
        ViewGroup.LayoutParams lpmsIncome = _lsvCategoryIn.getLayoutParams();
        ViewGroup.LayoutParams lpmsExpens = _lsvCategoryEx.getLayoutParams();

        int rowHeightIncome = (int) getResources().getDimension(R.dimen.item_category_list_row_height)+_itemCategoryInList.size();
        lpmsIncome.height = _itemCategoryInList.size() * rowHeightIncome;
        _lsvCategoryIn.setLayoutParams(lpmsIncome);

        int rowHeightExpens = (int) getResources().getDimension(R.dimen.item_category_list_row_height)+_itemCategoryExList.size();
        lpmsExpens.height = _itemCategoryExList.size() * rowHeightExpens;
        _lsvCategoryEx.setLayoutParams(lpmsExpens);
    }

    private void calculatePercentage() {
        BigDecimal sumIn = _balance.getIncome();
        BigDecimal sumEx = _balance.getExpense();
        for (int i = 0; i < _itemCategoryInList.size(); i++) {
            BigDecimal percentage = _itemCategoryInList.get(i).getAmount()
                    .multiply(new BigDecimal(100))
                    .divide(sumIn, RoundingMode.HALF_EVEN)
                    .setScale(0, RoundingMode.DOWN);

            _itemCategoryInList.get(i).setMemo(String.valueOf(percentage)); // memo is place holder for percentage
        }
        for (int i = 0; i < _itemCategoryExList.size(); i++) {
            BigDecimal percentage = _itemCategoryExList.get(i).getAmount()
                    .multiply(new BigDecimal(100))
                    .divide(sumEx, RoundingMode.HALF_EVEN)
                    .setScale(0, RoundingMode.DOWN);

            _itemCategoryExList.get(i).setMemo(String.valueOf(percentage)); // memo is place holder for percentage
        }
    }

    private void makePieGraph() {
        _inPieGraph.removeSlices();
        _exPieGraph.removeSlices();
        PieSlice inPieSlice;
        PieSlice exPieSlice;

        for (int i = 0; i < _itemCategoryExList.size(); i++) {
            exPieSlice = new PieSlice();
            exPieSlice.setColor(Color.parseColor(Constants._categoryColor[i]));
            exPieSlice.setValue(_itemCategoryExList.get(i).getAmount().floatValue());
            _exPieGraph.addSlice(exPieSlice);
        }

        for (int i = 0; i < _itemCategoryInList.size(); i++) {
            inPieSlice = new PieSlice();
            inPieSlice.setColor(ContextCompat.getColor(_activity, R.color.colorPrimary));
            inPieSlice.setValue(_itemCategoryInList.get(i).getAmount().floatValue());
            _inPieGraph.addSlice(inPieSlice);
        }
    }

    public void export() {
        Log.d(TAG, "export() called");

        if (_itemCategoryExList.isEmpty() || _itemCategoryInList.isEmpty()) {
            Toast.makeText(_activity, R.string.nothing_to_export, Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder dialogExport = new AlertDialog.Builder(_activity);
        dialogExport.setIcon(R.mipmap.ic_mikan);
        dialogExport.setTitle(getString(R.string.export_category));
        dialogExport.setMessage(getString(R.string.quest_export_this_report_C));
        dialogExport.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Runnable rblSaveToFile = ()->{
                    System.out.println("Runnable running");
                    queryToSaveLocal();
                };
                Thread thread = new Thread(rblSaveToFile);
                thread.start();

                Intent intent = new Intent(_activity, ExportActivity.class);
                intent.putExtra("REPORT_VIEW_TYPE", TabFragment2.REPORT_BY_CATEGORY);
                startActivity(intent);
            }
        });
        dialogExport.show();
    }

    private void queryToSaveLocal() {
        /***
         * expecting: queryD=
         * SELECT * FROM ITEMS WHERE strftime('%Y-%m', event_date) = '2018-11' ORDER BY event_date ASC
         * ***/
        String query = _query.getQueryD()
                .replace("ORDER BY event_date ASC", " ORDER BY category_code, amount DESC");

        Log.d(TAG, "queryToSaveLocal() "+query);

        _stringBuilder.setLength(0);
        _stringBuilder.append(getResources().getString(R.string.category));
        _stringBuilder.append(",");
        _stringBuilder.append(getResources().getString(R.string.amount));
        _stringBuilder.append(",");
        _stringBuilder.append(getResources().getString(R.string.memo));
        _stringBuilder.append(",");
        _stringBuilder.append(getResources().getString(R.string.event_date));
        _stringBuilder.append(",");
        _stringBuilder.append(getResources().getString(R.string.updated_date));
        _stringBuilder.append("\n");

//        _itemDbAdapter.open();
//        Cursor c = _itemDbAdapter.getItemsByRawQuery(query);
//
//        if (c!=null && c.moveToFirst()) {
//            do {
//                ItemStatus itemStatus = new ItemStatus(
//                        0,
//                        c.getLong(c.getColumnIndex(ItemDBAdapter.COL_AMOUNT)),
//                        "",
//                        _fractionDigits,
//                        c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)),
//                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_MEMO)),
//                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE)),
//                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_UPDATE_DATE))
//                );
//
//                _stringBuilder.append(UtilCategory.getCategoryStr(getContext(), itemStatus.getCategoryCode()));
//                _stringBuilder.append(",");
//                _stringBuilder.append(itemStatus.getAmount());
//                _stringBuilder.append(",");
//                _stringBuilder.append(itemStatus.getMemo());
//                _stringBuilder.append(",");
//                _stringBuilder.append(itemStatus.getEventDate());
//                _stringBuilder.append(",");
//                _stringBuilder.append(itemStatus.getUpdateDate());
//                _stringBuilder.append("\n");
//            } while (c.moveToNext());
//        }
//
//        UtilFiles.writeToFile(ExportActivity.FILE_ORDER_CATEGORY,
//                _stringBuilder.toString(), _activity, Context.MODE_PRIVATE);

        //todo tell the CreateFileInFolderActivity that it's ready to upload
    }
}