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
import com.kakeibo.db.ItemsDBAdapter;
import com.kakeibo.export.CreateFileInFolderActivity;
import com.kakeibo.util.UtilFiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TabFragment2C extends Fragment {
    private final static String TAG = TabFragment2C.class.getSimpleName();

    private static Query _query;
    private static ItemLoadListener _itemLoadListener;

    private Activity _activity;
    private View _view;
    private ItemsDBAdapter _itemsDbAdapter;
    private StringBuilder _stringBuilder;

    private List<Item> lstCategory;
    private CategoryListAdapter lsaCategory;
    private ListView lsvCategory;

    private PieGraph graph;

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
        _view = inflater.inflate(R.layout.tab_fragment_2c, container, false);

        findViews();

        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        loadItemsOrderByCategory(); /*** <- to handle come back from settings ***/
    }

    @SuppressLint("ClickableViewAccessibility")
    void findViews() {
        graph = _view.findViewById(R.id.graph_subtotal);
        lsvCategory = _view.findViewById(R.id.lsv_subtotal);
        lsvCategory.setOnTouchListener((View v, MotionEvent event)-> {
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

        lsvCategory.setOnItemClickListener(new CategoryListItemClickListener());

        _stringBuilder = new StringBuilder();
        _itemsDbAdapter = new ItemsDBAdapter();
        lstCategory = new ArrayList<>();
        lsaCategory = new CategoryListAdapter(_activity, 0, lstCategory);
        lsvCategory.setAdapter(lsaCategory);
    }

    class CategoryListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lsvCat = (ListView) parent;
            Item tmp = (Item) lsvCat.getItemAtPosition(position);

            List<Item> searchResultList = new ArrayList<>();
            searchResultList.clear();

            String[] queries = _query.getQueryCs();

            Log.d(TAG, "loadItems: " + queries[tmp.getCategoryCode()]);

            _itemsDbAdapter.open();
            Cursor c = _itemsDbAdapter.getItemsByRawQuery(queries[tmp.getCategoryCode()]);

            if (c.moveToFirst()) {
                do {
                    Item item = new Item(
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_ID)),
                            c.getLong(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
                            "",
                            MainActivity.sFractionDigits,
                            c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_UPDATE_DATE))
                    );

                    searchResultList.add(item);
                } while (c.moveToNext());
            }
            _itemsDbAdapter.close();

            CategoryDetailListAdapter categoryDetailListAdapter =
                    new CategoryDetailListAdapter(_activity, 0, searchResultList);
            ListView listView = new ListView(_activity);
            listView.setAdapter(categoryDetailListAdapter);
            AlertDialog.Builder dialog = new AlertDialog.Builder(_activity);
            dialog.setIcon(R.mipmap.ic_mikan);
            dialog.setTitle(MainActivity.sCategories[tmp.getCategoryCode()]);
            dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.setView(listView).create();
            dialog.show();
        }
    }

    public void setQuery (Query query) {
        _query = query;
    }

    public void loadItemsOrderByCategory () {
        Log.d(TAG, "loadItemsOrderByCategory() "+_query.getQueryC());

        Balance balance = Balance.newInstance(MainActivity.sFractionDigits);

        lstCategory.clear();
        _itemsDbAdapter.open();
        Cursor c = _itemsDbAdapter.getItemsByRawQuery(_query.getQueryC());

        if (c!=null && c.moveToFirst()) {
            BigDecimal balanceDay = new BigDecimal(0)
                    .setScale(MainActivity.sFractionDigits, RoundingMode.UNNECESSARY);

            do {
                Item item = new Item(
                        "",
                        c.getLong(c.getColumnIndex("SUM(amount)")),
                        "",
                        MainActivity.sFractionDigits,
                        c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                        "",
                        "",
                        ""
                );

                if(c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)) == 0) {
                    balance.addIncome(item.getAmount());
                    balanceDay = balanceDay.add(item.getAmount());
                } else {
                    balance.addExpense(item.getAmount());
                    balanceDay = balanceDay.subtract(item.getAmount());
                }

                lstCategory.add(item);
            } while (c.moveToNext());
        }

        _itemsDbAdapter.close();

        if (lstCategory.size() > 0) {
            calculatePercentage(balance);
        }

        lsaCategory.notifyDataSetChanged();
        makePieGraph();

        _itemLoadListener.onItemsLoaded(balance);
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

        _itemsDbAdapter.open();
        Cursor c = _itemsDbAdapter.getItemsByRawQuery(query);

        if (c!=null && c.moveToFirst()) {
            do {
                Item item = new Item(
                        "",
                        c.getLong(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
                        "",
                        MainActivity.sFractionDigits,
                        c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_UPDATE_DATE))
                );

                _stringBuilder.append(MainActivity.sCategories[item.getCategoryCode()]);
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

    void calculatePercentage(Balance balance) {
        BigDecimal sum = balance.getIncome().add(balance.getExpense());
        for (int i = 0; i < lstCategory.size(); i++) {
            BigDecimal out = lstCategory.get(i).getAmount()
                    .multiply(new BigDecimal(100))
                    .divide(sum, RoundingMode.DOWN)
                    .setScale(0, RoundingMode.DOWN);

            lstCategory.get(i).setMemo(String.valueOf(out));
        }
    }

    private void makePieGraph() {
        graph.removeSlices();
        for (int i = 0; i < lstCategory.size(); i++) {
            PieSlice slice = new PieSlice();
            if (lstCategory.get(i).getCategoryCode() == 0) {
                slice.setColor(ContextCompat.getColor(_activity, R.color.colorPrimary));
            } else if (lstCategory.get(i).getCategoryCode() > 0) {
                slice.setColor(Color.parseColor(MainActivity.categoryColor[i]));
            }
            slice.setValue(lstCategory.get(i).getAmount().intValue());
            graph.addSlice(slice);
        }
        graph.setThickness(100);
    }

    public void export() {
        Log.d(TAG, "export() called");

        if (lstCategory.size()==0) {
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
}
