package com.kakeibo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.kakeibo.db.ItemDBAdapter;
import com.kakeibo.export.CreateFileInFolderActivity;
import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilFiles;
import com.kakeibo.util.UtilKeyboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TabFragment2C extends Fragment {
    private final static String TAG = TabFragment2C.class.getSimpleName();

    private static Query sQuery;
    private static ItemLoadListener sItemLoadListener;

    private Activity _activity;
    private View _view;
    private ItemDBAdapter _itemDbAdapter;
    private StringBuilder _stringBuilder;
    private Balance _balance;
    private PieGraph _inPieGraph;
    private PieGraph _exPieGraph;
    private List<Item> _itemCategoryInList;
    private List<Item> _itemCategoryExList;
    private CategoryListAdapter _itemCategoryInAdapter;
    private CategoryListAdapter _itemCategoryExAdapter;
    private ListView _itemCategoryInListView;
    private ListView _itemCategoryExListView;
//    private int _toggleView = 0; //0=expense, 1=income, 2=both

    public static TabFragment2C newInstance(ItemLoadListener itemLoadListener, Query query) {
        TabFragment2C tabFragment2C = new TabFragment2C();
        Bundle args = new Bundle();
        args.putParcelable("query", query);
        sQuery = query;
        sItemLoadListener = itemLoadListener;
        tabFragment2C.setArguments(args);
        return tabFragment2C;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _activity = getActivity();
        _view = inflater.inflate(R.layout.fragment_tab_2c, container, false);

        findViews();

        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        loadItemsOrderByCategory(); /*** <- to handle come back from settings ***/
    }

    @Override
    public void onPause() {
        super.onPause();
        UtilKeyboard.hideSoftKeyboard(_activity);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void findViews() {
        _inPieGraph = _view.findViewById(R.id.pie_graph_income);
        _exPieGraph = _view.findViewById(R.id.pie_graph_expense);
        _inPieGraph.setThickness(150);
        _exPieGraph.setThickness(150);

        _itemCategoryInListView = _view.findViewById(R.id.lsv_income);
        _itemCategoryExListView = _view.findViewById(R.id.lsv_expense);
        ViewCompat.setNestedScrollingEnabled(_itemCategoryInListView, true);
        ViewCompat.setNestedScrollingEnabled(_itemCategoryExListView, true);
        _itemCategoryInListView.setOnTouchListener((View v, MotionEvent event)-> {
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
        _itemCategoryExListView.setOnTouchListener((View v, MotionEvent event)-> {
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

        _itemCategoryInListView.setOnItemClickListener(new CategoryListItemClickListener());
        _itemCategoryExListView.setOnItemClickListener(new CategoryListItemClickListener());

        _stringBuilder = new StringBuilder();
        _itemDbAdapter = new ItemDBAdapter();
        _itemCategoryInList = new ArrayList<>();
        _itemCategoryExList = new ArrayList<>();
        _itemCategoryInAdapter = new CategoryListAdapter(_activity, 0, _itemCategoryInList);
        _itemCategoryExAdapter = new CategoryListAdapter(_activity, 0, _itemCategoryExList);
        _itemCategoryInListView.setAdapter(_itemCategoryInAdapter);
        _itemCategoryExListView.setAdapter(_itemCategoryExAdapter);
    }

    class CategoryListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lsvCat = (ListView) parent;
            Item tmp = (Item) lsvCat.getItemAtPosition(position);

            List<Item> searchResultList = new ArrayList<>();
            searchResultList.clear();

            String[] queries = sQuery.getQueryCs();

            Log.d(TAG, "loadItems: " + queries[tmp.getCategoryCode()]);

            _itemDbAdapter.open();
            Cursor c = _itemDbAdapter.getItemsByRawQuery(queries[tmp.getCategoryCode()]);

            if (c.moveToFirst()) {
                do {
                    Item item = new Item(
                            c.getString(c.getColumnIndex(ItemDBAdapter.COL_ID)),
                            c.getLong(c.getColumnIndex(ItemDBAdapter.COL_AMOUNT)),
                            "",
                            MainActivity.sFractionDigits,
                            c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)),
                            c.getString(c.getColumnIndex(ItemDBAdapter.COL_MEMO)),
                            c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE)),
                            c.getString(c.getColumnIndex(ItemDBAdapter.COL_UPDATE_DATE))
                    );

                    searchResultList.add(item);
                } while (c.moveToNext());
            }
            _itemDbAdapter.close();

            CategoryDetailListAdapter categoryDetailListAdapter =
                    new CategoryDetailListAdapter(_activity, 0, searchResultList);
            ListView listView = new ListView(_activity);
            listView.setAdapter(categoryDetailListAdapter);
            AlertDialog.Builder dialog = new AlertDialog.Builder(_activity);
            dialog.setIcon(R.mipmap.ic_mikan);
            dialog.setTitle(UtilCategory.getCategory(getContext(), tmp.getCategoryCode()));
            dialog.setPositiveButton(R.string.ok, (DialogInterface d, int which) -> {
            });
            dialog.setView(listView).create();
            dialog.show();
        }
    }

    public void setQuery (Query query) {
        sQuery = query;
    }

    protected void loadItemsOrderByCategory () {
        Log.d(TAG, "loadItemsOrderByCategory() "+ sQuery.getQueryC());

        _balance = Balance.newInstance(MainActivity.sFractionDigits);

        _itemCategoryInList.clear();
        _itemCategoryExList.clear();
        _itemDbAdapter.open();
        Cursor c = _itemDbAdapter.getItemsByRawQuery(sQuery.getQueryC());

        if (c!=null && c.moveToFirst()) {
            BigDecimal balanceDay = new BigDecimal(0)
                    .setScale(MainActivity.sFractionDigits, RoundingMode.UNNECESSARY);

            do {
                Item item = new Item(
                        "",
                        c.getLong(c.getColumnIndex("SUM(amount)")),
                        "",
                        MainActivity.sFractionDigits,
                        c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)),
                        "",
                        "",
                        ""
                );

                if(c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)) == 0) {
                    _balance.addIncome(item.getAmount());
                    balanceDay = balanceDay.add(item.getAmount());
                    _itemCategoryInList.add(item);
                } else {
                    _balance.addExpense(item.getAmount());
                    balanceDay = balanceDay.subtract(item.getAmount());
                    _itemCategoryExList.add(item);
                }
            } while (c.moveToNext());
        }

        _itemDbAdapter.close();

        _itemCategoryInAdapter.notifyDataSetChanged();
        _itemCategoryExAdapter.notifyDataSetChanged();
        adjustItemCategoryListViewHeight();
        calculatePercentage();
        makePieGraph();
        sItemLoadListener.onItemsLoaded(_balance);
    }

    private void adjustItemCategoryListViewHeight() {
        ViewGroup.LayoutParams lpmsIncome = _itemCategoryInListView.getLayoutParams();
        ViewGroup.LayoutParams lpmsExpens = _itemCategoryExListView.getLayoutParams();

        int rowHeightIncome = (int) getResources().getDimension(R.dimen.item_category_list_row_height);
        lpmsIncome.height = _itemCategoryInList.size() * rowHeightIncome;
        _itemCategoryInListView.setLayoutParams(lpmsIncome);

        int rowHeightExpens = (int) getResources().getDimension(R.dimen.item_category_list_row_height);
        lpmsExpens.height = _itemCategoryExList.size() * rowHeightExpens;
        _itemCategoryExListView.setLayoutParams(lpmsExpens);
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
            exPieSlice.setColor(Color.parseColor(MainActivity.categoryColor[i]));
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

                Intent intent = new Intent(_activity, CreateFileInFolderActivity.class);
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
        String query = sQuery.getQueryD()
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

        _itemDbAdapter.open();
        Cursor c = _itemDbAdapter.getItemsByRawQuery(query);

        if (c!=null && c.moveToFirst()) {
            do {
                Item item = new Item(
                        "",
                        c.getLong(c.getColumnIndex(ItemDBAdapter.COL_AMOUNT)),
                        "",
                        MainActivity.sFractionDigits,
                        c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)),
                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_MEMO)),
                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE)),
                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_UPDATE_DATE))
                );

                _stringBuilder.append(UtilCategory.getCategory(getContext(), item.getCategoryCode()));
                _stringBuilder.append(",");
                _stringBuilder.append(item.getAmount());
                _stringBuilder.append(",");
                _stringBuilder.append(item.getMemo());
                _stringBuilder.append(",");
                _stringBuilder.append(item.getEventDate());
                _stringBuilder.append(",");
                _stringBuilder.append(item.getUpdateDate());
                _stringBuilder.append("\n");
            } while (c.moveToNext());
        }

        UtilFiles.writeToFile(CreateFileInFolderActivity.FILE_ORDER_CATEGORY,
                _stringBuilder.toString(), _activity, Context.MODE_PRIVATE);

        //todo tell the CreateFileInFolderActivity that it's ready to upload
    }
}
