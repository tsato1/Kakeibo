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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.kakeibo.db.ItemsDBAdapter;
import com.kakeibo.export.CreateFileInFolderActivity;
import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilFiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TabFragment2C extends Fragment {
    private final static String TAG = TabFragment2C.class.getSimpleName();

    private static Query sQuery;
    private static ItemLoadListener sItemLoadListener;

    private Activity mActivity;
    private View mView;
    private ItemsDBAdapter mItemsDbAdapter;
    private StringBuilder mStringBuilder;
    private Balance mBalance;
    private PieGraph mGraph;
    private ImageButton mImbToggleGraph;
    private List<Item> mLstCategory;
    private List<Item> mLstInCategory;
    private List<Item> mLstExCategory;
    private CategoryListAdapter mLsaCategory;
    private CategoryListAdapter mLsaInCategory;
    private CategoryListAdapter mLsaExCategory;
    private ListView mLsvCategory;
    private ListView mLsvInCategory;
    private ListView mLsvExCategory;

    private int _toggleView = 0; //0=expense, 1=income, 2=both

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
        mActivity = getActivity();
        mView = inflater.inflate(R.layout.tab_fragment_2c, container, false);

        findViews();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        loadItemsOrderByCategory(); /*** <- to handle come back from settings ***/
    }

    @SuppressLint("ClickableViewAccessibility")
    void findViews() {
        mGraph = mView.findViewById(R.id.pie_graph);
        mGraph.setThickness(150);
        mLsvCategory = mView.findViewById(R.id.lsv_subtotal);
        mLsvInCategory = mView.findViewById(R.id.lsv_income);
        mLsvExCategory = mView.findViewById(R.id.lsv_expense);//todo  sasdfasdfasdf
        mLsvCategory.setOnTouchListener((View v, MotionEvent event)-> {
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

        mImbToggleGraph = mView.findViewById(R.id.imb_toggle);
        mImbToggleGraph.setOnClickListener(new ToggleGraphClickListener());

        mLsvCategory.setOnItemClickListener(new CategoryListItemClickListener());
        mLsvInCategory.setOnItemClickListener(new CategoryListItemClickListener());
        mLsvExCategory.setOnItemClickListener(new CategoryListItemClickListener());

        mStringBuilder = new StringBuilder();
        mItemsDbAdapter = new ItemsDBAdapter();
        mLstCategory = new ArrayList<>();
        mLstInCategory = new ArrayList<>();
        mLstExCategory = new ArrayList<>();
        mLsaCategory = new CategoryListAdapter(mActivity, 0, mLstCategory);
        mLsaInCategory = new CategoryListAdapter(mActivity, 0, mLstInCategory);
        mLsaExCategory = new CategoryListAdapter(mActivity, 0, mLstExCategory);
        mLsvCategory.setAdapter(mLsaCategory);
        mLsvInCategory.setAdapter(mLsaInCategory);
        mLsvExCategory.setAdapter(mLsaExCategory);
    }

    class ToggleGraphClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            _toggleView = (_toggleView + 1) % 3;

            if (mLstCategory.isEmpty()) {
                mImbToggleGraph.setVisibility(View.GONE);
            } else {
                mImbToggleGraph.setVisibility(View.VISIBLE);
                calculatePercentage();
            }

            switch (_toggleView) {
                case 0:
                    mLsvCategory.setVisibility(View.GONE);
                    mLsvExCategory.setVisibility(View.VISIBLE);
                    mLsvInCategory.setVisibility(View.GONE);
                    break;
                case 1:
                    mLsvCategory.setVisibility(View.GONE);
                    mLsvExCategory.setVisibility(View.GONE);
                    mLsvInCategory.setVisibility(View.VISIBLE);
                    break;
                default: // _toggleView==2
                    mLsvCategory.setVisibility(View.VISIBLE);
                    mLsvExCategory.setVisibility(View.GONE);
                    mLsvInCategory.setVisibility(View.GONE);
                    break;
            }
            mLsaCategory.notifyDataSetChanged();
            mLsaInCategory.notifyDataSetChanged();
            mLsaExCategory.notifyDataSetChanged();
            makePieGraph();
            sItemLoadListener.onItemsLoaded(mBalance);
        }
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

            mItemsDbAdapter.open();
            Cursor c = mItemsDbAdapter.getItemsByRawQuery(queries[tmp.getCategoryCode()]);

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
            mItemsDbAdapter.close();

            CategoryDetailListAdapter categoryDetailListAdapter =
                    new CategoryDetailListAdapter(mActivity, 0, searchResultList);
            ListView listView = new ListView(mActivity);
            listView.setAdapter(categoryDetailListAdapter);
            AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
            dialog.setIcon(R.mipmap.ic_mikan);
            dialog.setTitle(UtilCategory.getCategoryStrFromCode(getContext(), tmp.getCategoryCode()));
            dialog.setPositiveButton(R.string.ok, (DialogInterface d, int which) -> {
            });
            dialog.setView(listView).create();
            dialog.show();
        }
    }

    public void setQuery (Query query) {
        sQuery = query;
    }

    public void loadItemsOrderByCategory () {
        Log.d(TAG, "loadItemsOrderByCategory() "+ sQuery.getQueryC());

        mBalance = Balance.newInstance(MainActivity.sFractionDigits);

        mLstCategory.clear();
        mLstInCategory.clear();
        mLstExCategory.clear();
        mItemsDbAdapter.open();
        Cursor c = mItemsDbAdapter.getItemsByRawQuery(sQuery.getQueryC());

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
                    mBalance.addIncome(item.getAmount());
                    balanceDay = balanceDay.add(item.getAmount());
                    mLstInCategory.add(item);
                } else {
                    mBalance.addExpense(item.getAmount());
                    balanceDay = balanceDay.subtract(item.getAmount());
                    mLstExCategory.add(item);
                }

                mLstCategory.add(item);
            } while (c.moveToNext());
        }

        mItemsDbAdapter.close();

        if (mLstCategory.isEmpty()) {
            mImbToggleGraph.setVisibility(View.GONE);
        } else {
            mImbToggleGraph.setVisibility(View.VISIBLE);
            calculatePercentage();
        }

        switch (_toggleView) {
            case 0:
                mLsvCategory.setVisibility(View.GONE);
                mLsvExCategory.setVisibility(View.VISIBLE);
                mLsvInCategory.setVisibility(View.GONE);
                break;
            case 1:
                mLsvCategory.setVisibility(View.GONE);
                mLsvExCategory.setVisibility(View.GONE);
                mLsvInCategory.setVisibility(View.VISIBLE);
                break;
            default: // _toggleView==2
                mLsvCategory.setVisibility(View.VISIBLE);
                mLsvExCategory.setVisibility(View.GONE);
                mLsvInCategory.setVisibility(View.GONE);
                break;
        }

        mLsaCategory.notifyDataSetChanged();
        mLsaInCategory.notifyDataSetChanged();
        mLsaExCategory.notifyDataSetChanged();
        makePieGraph();
        sItemLoadListener.onItemsLoaded(mBalance);
    }

    void calculatePercentage() {
        BigDecimal sumIn = mBalance.getIncome();
        BigDecimal sumEx = mBalance.getExpense();
        for (int i = 0; i < mLstInCategory.size(); i++) {
            BigDecimal percentage = mLstInCategory.get(i).getAmount()
                    .multiply(new BigDecimal(100))
                    .divide(sumIn, RoundingMode.HALF_EVEN)
                    .setScale(0, RoundingMode.DOWN);

            mLstInCategory.get(i).setMemo(String.valueOf(percentage)); // memo is place holder for percentage
        }
        for (int i = 0; i < mLstExCategory.size(); i++) {
            BigDecimal percentage = mLstExCategory.get(i).getAmount()
                    .multiply(new BigDecimal(100))
                    .divide(sumEx, RoundingMode.HALF_EVEN)
                    .setScale(0, RoundingMode.DOWN);

            mLstExCategory.get(i).setMemo(String.valueOf(percentage)); // memo is place holder for percentage
        }
    }

    private void makePieGraph() {
        mGraph.removeSlices();
        PieSlice slice;

        if (_toggleView == 0) {
            for (int i = 0; i < mLstExCategory.size(); i++) {
                slice = new PieSlice();
                slice.setColor(Color.parseColor(MainActivity.categoryColor[i]));
                slice.setValue(mLstExCategory.get(i).getAmount().floatValue());
                mGraph.addSlice(slice);
            }
        } else if (_toggleView == 1) {
            for (int i = 0; i < mLstInCategory.size(); i++) {
                slice = new PieSlice();
                slice.setColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                slice.setValue(mLstInCategory.get(i).getAmount().floatValue());
                mGraph.addSlice(slice);
            }
        } else if (_toggleView == 2) {
            for (int i = 0; i < mLstCategory.size(); i++) {
                slice = new PieSlice();
                if (mLstCategory.get(i).getCategoryCode()==0)
                    slice.setColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                else
                    slice.setColor(Color.parseColor(MainActivity.categoryColor[i]));
                slice.setValue(mLstCategory.get(i).getAmount().floatValue());
                mGraph.addSlice(slice);
            }
        }
    }

    public void export() {
        Log.d(TAG, "export() called");

        if (mLstInCategory.size()==0) {
            Toast.makeText(mActivity, R.string.nothing_to_export, Toast.LENGTH_SHORT).show();
        }

        AlertDialog.Builder dialogExport = new AlertDialog.Builder(mActivity);
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

                Intent intent = new Intent(mActivity, CreateFileInFolderActivity.class);
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

        mStringBuilder.setLength(0);
        mStringBuilder.append(getResources().getString(R.string.category));
        mStringBuilder.append(",");
        mStringBuilder.append(getResources().getString(R.string.amount));
        mStringBuilder.append(",");
        mStringBuilder.append(getResources().getString(R.string.memo));
        mStringBuilder.append(",");
        mStringBuilder.append(getResources().getString(R.string.event_date));
        mStringBuilder.append(",");
        mStringBuilder.append(getResources().getString(R.string.updated_date));
        mStringBuilder.append("\n");

        mItemsDbAdapter.open();
        Cursor c = mItemsDbAdapter.getItemsByRawQuery(query);

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

                mStringBuilder.append(UtilCategory.getCategoryStrFromCode(getContext(), item.getCategoryCode()));
                mStringBuilder.append(",");
                mStringBuilder.append(item.getAmount());
                mStringBuilder.append(",");
                mStringBuilder.append(item.getMemo());
                mStringBuilder.append(",");
                mStringBuilder.append(item.getEventDate());
                mStringBuilder.append(",");
                mStringBuilder.append(item.getUpdateDate());
                mStringBuilder.append("\n");
            } while (c.moveToNext());
        }

        UtilFiles.writeToFile(CreateFileInFolderActivity.FILE_ORDER_CATEGORY,
                mStringBuilder.toString(), mActivity, Context.MODE_PRIVATE);

        //todo tell the CreateFileInFolderActivity that it's ready to upload
    }
}
