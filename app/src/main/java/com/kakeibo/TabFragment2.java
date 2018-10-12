package com.kakeibo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.kakeibo.db.ItemsDBAdapter;
import com.kakeibo.export.CreateFileInFolderActivity;
import com.kakeibo.export.UtilFiles;
import com.kakeibo.util.UtilDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by T on 2015/09/14.
 */
public class TabFragment2 extends Fragment {
    private static final String TAG = TabFragment2.class.getSimpleName();

    private static final int MENU_ITEM_ID_DELETE = 0;
    private static final int MENU_ITEM_ID_EDIT = 1;

    private static int REPORT_VIEW_TYPE = 0; // 0:by date, 1:category-wise price desc, 2: price desc

    private FrameLayout frlRoot;
    private Activity _activity;
    private ItemsDBAdapter itemsDbAdapter;
    private List<String> dateHeaderList;
    private HashMap<String, List<Item>> childDataHashMap;
    private ExpandableListAdapter expandableListAdapter;
    private ExpandableListView expandableListView;
    private List<Item> categoryList;
    private CategoryListAdapter categoryListAdapter;
    private FrameLayout categoryLayout;
    private ListView categoryListView;
    private PieGraph graph;
    private ImageButton btnPrev, btnNext;
    private Button btnDate;
    private TextView txvIncome, txvExpense, txvBalance;
    private FloatingActionButton fabDiscard, fabExport;
    private BigDecimal income, expense, balance;
    public  int _calMonth, _calYear;
    private String[] weekName;
    private String[] defaultCategory;
    private String amountColon, memoColon, categoryColon, savedOnColon;
    private View _view;
    private Query _query;
    private StringBuilder mStringBuilder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _activity = getActivity();
        _view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        weekName = getResources().getStringArray(R.array.week_name);
        defaultCategory = getResources().getStringArray((R.array.default_category));
        amountColon = getResources().getString(R.string.amount_colon);
        memoColon = getResources().getString(R.string.memo_colon);
        categoryColon = getResources().getString(R.string.category_colon);
        savedOnColon = getResources().getString(R.string.updated_on_colon);

        findViews();
        setListeners();

        makeDefaultQuery();
        reset();

        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reset();
        loadItemsOrderByDate(); /*** <- to handle come back from settings ***/
    }

    void findViews(){
        frlRoot = _view.findViewById(R.id.frl_root_fragment2);
        btnPrev = _view.findViewById(R.id.btn_prev);
        btnDate = _view.findViewById(R.id.btn_date);
        btnNext = _view.findViewById(R.id.btn_next);
        txvIncome = _view.findViewById(R.id.txv_income);
        txvExpense = _view.findViewById(R.id.txv_expense);
        txvBalance = _view.findViewById(R.id.txv_balance);
        expandableListView = _view.findViewById(R.id.lsv_expandable);
        categoryLayout = _view.findViewById(R.id.scv_subtotal);
        graph = _view.findViewById(R.id.graph_subtotal);
        categoryListView = _view.findViewById(R.id.lsv_subtotal);
        fabDiscard = _view.findViewById(R.id.fab_discard);
        fabExport = _view.findViewById(R.id.fab_export);

        categoryLayout.setVisibility(View.GONE);
    }

    void setListeners(){
        btnPrev.setOnClickListener(new ButtonClickListener());
        btnDate.setOnClickListener(new ButtonClickListener());
        btnNext.setOnClickListener(new ButtonClickListener());
        btnDate.setOnLongClickListener(new ButtonLongClickListener());
        expandableListView.setOnChildClickListener(new ChildClickListener());
        expandableListView.setOnCreateContextMenuListener(new ChildClickContextMenuListener());
        categoryListView.setOnItemClickListener(new CategoryListItemClickListener());
        fabDiscard.setOnClickListener(new ButtonClickListener());
        fabExport.setOnClickListener(new ButtonClickListener());
        
        mStringBuilder = new StringBuilder();
        itemsDbAdapter = new ItemsDBAdapter(_activity);
        dateHeaderList = new ArrayList<>();
        childDataHashMap = new HashMap<>();
        expandableListAdapter = new ExpandableListAdapter(_activity, dateHeaderList, childDataHashMap);
        expandableListView.setAdapter(expandableListAdapter);
        categoryList = new ArrayList<>();
        categoryListAdapter = new CategoryListAdapter(_activity, 0, categoryList);
        categoryListView.setAdapter(categoryListAdapter);
    }

    private void makeDefaultQuery() {
        String[] ymd = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB).split("-");
        _calYear = Integer.parseInt(ymd[0]);
        _calMonth = Integer.parseInt(ymd[1]);
        _query = new Query(ymd[0], ymd[1], ymd[2], MainActivity.sDateFormat);
        _query.buildQuery();
    }

    private void buildQuery() {
        String y = String.valueOf(_calYear);
        String m = UtilDate.convertMtoMM(_calMonth);
        _query = new Query(Query.QUERY_TYPE_NEW);
        _query.setValDate(y, m, "01", MainActivity.sDateFormat);
        _query.buildQuery();
    }

    class CategoryListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lsvCat = (ListView) parent;
            Item tmp = (Item) lsvCat.getItemAtPosition(position);

            List<Item> searchResultList = new ArrayList<>();
            searchResultList.clear();

            String[] ym = btnDate.getText().toString().split("[/]");
            String y, m;
            switch (MainActivity.sDateFormat) {
                case 1: // MDY
                case 2: // DMY
                    y = ym[1];
                    m = ym[0];
                    break;
                default: // YMD
                    y = ym[0];
                    m = ym[1];
            }

            itemsDbAdapter.open();
            Cursor c = itemsDbAdapter.getAllItemsInCategoryInMonth(y, m, tmp.getCategoryCode());
            if (c.moveToFirst()) {
                do {
                    Item item = new Item(
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_ID)),
                            c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_CURRENCY_CODE)),
                            c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_UPDATE_DATE))
                    );

                    searchResultList.add(item);
                } while (c.moveToNext());
            }
            itemsDbAdapter.close();

            CategoryDetailListAdapter categoryDetailListAdapter = new CategoryDetailListAdapter(_activity, 0, searchResultList);
            ListView listView = new ListView(_activity);
            listView.setAdapter(categoryDetailListAdapter);
            AlertDialog.Builder dialog = new AlertDialog.Builder(_activity);
            dialog.setIcon(R.mipmap.ic_mikan);
            dialog.setTitle(defaultCategory[tmp.getCategoryCode()]);
            dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.setView(listView).create();
            dialog.show();
        }
    }

    class ButtonLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick (View view) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(_activity);
            dialog.setIcon(R.mipmap.ic_mikan);
            dialog.setTitle(getString(R.string.title_search_criteria));
            dialog.setMessage(_query.getSearchCriteria());
            dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.show();

            return true;
        }
    }

    class ChildClickListener implements ExpandableListView.OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
            Object child = (expandableListAdapter.getChild(groupPosition, childPosition));
            Item item = (Item)child;

            LayoutInflater inflater = (LayoutInflater)_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.dialog_item_detail, view.findViewById(R.id.layout_root));

            TextView txvCategory = layout.findViewById(R.id.txv_detail_category);
            TextView txvAmount = layout.findViewById(R.id.txv_detail_amount);
            TextView txvMemo = layout.findViewById(R.id.txv_detail_memo);
            TextView txvRegistrationDate = layout.findViewById(R.id.txv_detail_registration);

            String categoryText = categoryColon + defaultCategory[item.getCategoryCode()];
            txvCategory.setText(categoryText);
            SpannableString span1, span2;
            if (item.getCategoryCode() <= 0) {
                span1 = new SpannableString(amountColon);
                span2 = new SpannableString("+" + item.getAmount());
                span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_activity, R.color.colorBlue)), 0, 1, 0);
            } else {
                span1 = new SpannableString(amountColon);
                span2 = new SpannableString("-" + item.getAmount());
                span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_activity, R.color.colorRed)), 0, 1, 0);
            }
            txvAmount.setText(TextUtils.concat(span1, span2));
            String memoText = memoColon + item.getMemo();
            txvMemo.setText(memoText);
            String savedOnText = savedOnColon + UtilDate.getDateFromDBDate(item.getUpdateDate(), weekName, MainActivity.sDateFormat);
            txvRegistrationDate.setText(savedOnText);

            new AlertDialog.Builder(_activity)
                    .setIcon(R.mipmap.ic_mikan)
                    .setTitle(getResources().getString(R.string.item_detail))
                    .setView(layout)
                    .show();

            return false;
        }
    }

    class ChildClickContextMenuListener implements ExpandableListView.OnCreateContextMenuListener {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
            int type = ExpandableListView.getPackedPositionType(info.packedPosition);
            if(type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                menu.setHeaderIcon(R.mipmap.ic_mikan);
                menu.add(0, MENU_ITEM_ID_EDIT, 0, R.string.edit);
                menu.add(0, MENU_ITEM_ID_DELETE, 1, R.string.delete);
            }
        }
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuItem.getMenuInfo();
        final int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        final int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);
        final Item item = (Item)expandableListAdapter.getChild(groupPosition, childPosition);

        switch(menuItem.getItemId()) {
            case MENU_ITEM_ID_DELETE:
                new AlertDialog.Builder(_activity)
                        .setIcon(R.mipmap.ic_mikan)
                        .setTitle(getString(R.string.quest_do_you_want_to_delete_item))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.d("groupPosition", String.valueOf(groupPosition));
                                //Log.d("childPosition", String.valueOf(childPosition));
                                itemsDbAdapter.open();
                                final int itemId = Integer.parseInt(item.getId());

                                if(itemsDbAdapter.deleteItem(itemId)) {
                                    Toast.makeText(_activity, getString(R.string.msg_item_successfully_deleted), Toast.LENGTH_SHORT).show();
                                }

                                loadItemsOrderByDate();
                                itemsDbAdapter.close();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            case MENU_ITEM_ID_EDIT:
                LayoutInflater layoutInflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = layoutInflater.inflate(R.layout.dialog_item_edit, _view.findViewById(R.id.layout_root));
                TextView txvEventDate = layout.findViewById(R.id.txv_event_date);
                txvEventDate.setText(item.getEventDate());
                TextView txvCategory = layout.findViewById(R.id.txv_category);
                String categoryText = getString(R.string.category_colon) + defaultCategory[item.getCategoryCode()];
                txvCategory.setText(categoryText);
                EditText edtAmount = layout.findViewById(R.id.edt_amount);
                edtAmount.addTextChangedListener(new AmountTextWatcher(edtAmount, MainActivity.sCurrency.getDefaultFractionDigits()));
                edtAmount.setText(String.valueOf(item.getAmount()));
                EditText edtMemo = layout.findViewById(R.id.edt_memo);
                edtMemo.setText(item.getMemo());

                new AlertDialog.Builder(_activity)
                        .setIcon(R.mipmap.ic_mikan)
                        .setTitle(getString(R.string.title_edit_item))
                        .setView(layout)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                itemsDbAdapter.open();
                                final int itemId = Integer.parseInt(item.getId());

                                if (checkBeforeSave(edtAmount)) {
                                    if(itemsDbAdapter.deleteItem(itemId)) {
                                        String amount = edtAmount.getText().toString();

                                        Item tmp = new Item(
                                                "",
                                                new BigDecimal(amount),
                                                item.getCurrencyCode(),
                                                item.getCategoryCode(),
                                                edtMemo.getText().toString(),
                                                item.getEventDate(),
                                                UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS)
                                        );

                                        itemsDbAdapter.saveItem(tmp);

                                        Toast.makeText(_activity, getString(R.string.msg_change_successfully_saved), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                loadItemsOrderByDate();
                                itemsDbAdapter.close();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
        }
        return super.onContextItemSelected(menuItem);
    }

    //todo can make util file
    boolean checkBeforeSave(EditText edt_amount) {
        if ("".equals(edt_amount.getText().toString())) {
            Toast.makeText(_activity, getString(R.string.err_please_enter_amount), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    class ButtonClickListener implements View.OnClickListener {
        public void onClick(View view)
        {
            switch(view.getId())
            {
                case R.id.btn_date:
                    loadItemsOrderByDate();
                    if (_view.findViewById(R.id.lsv_expandable).getVisibility() != View.GONE) {
                        expandableListView.setVisibility(View.GONE);
                        categoryLayout.setVisibility(View.VISIBLE);
                        REPORT_VIEW_TYPE = 1;
                    } else {
                        expandableListView.setVisibility(View.VISIBLE);
                        categoryLayout.setVisibility(View.GONE);
                        REPORT_VIEW_TYPE = 0;
                    }
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
                    loadItemsOrderByDate();
                    break;
                case R.id.btn_next:
                    _calMonth++;
                    if(_calMonth > 12) {
                        _calMonth = 1;
                        _calYear++;
                    }
                    btnDate.setText(getTextBtnDate());
                    buildQuery();
                    loadItemsOrderByDate();
                    break;
                case R.id.fab_discard:
                    AlertDialog.Builder dialogSaveSearch = new AlertDialog.Builder(_activity);
                    dialogSaveSearch.setIcon(R.mipmap.ic_mikan);
                    dialogSaveSearch.setTitle(getString(R.string.title_returning_to_monthly_report));
                    //todo saved search
//                    dialog.setMessage(getString(R.string.quest_save_this_search));
//                    dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            saveSearch();
//                        }
//                    });
                    dialogSaveSearch.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            makeDefaultQuery();
                            reset();
                            loadItemsOrderByDate();
                        }
                    });
                    dialogSaveSearch.show();
                    break;
                case R.id.fab_export:
                    AlertDialog.Builder dialogExport = new AlertDialog.Builder(_activity);
                    dialogExport.setIcon(R.mipmap.ic_mikan);
                    dialogExport.setTitle(getString(R.string.export));
                    dialogExport.setMessage(getString(R.string.quest_export_this_report));
                    dialogExport.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (REPORT_VIEW_TYPE == 1) {
                                createOutFileByCategory();
                            }

                            Intent intent = new Intent(_activity, CreateFileInFolderActivity.class);
                            intent.putExtra("REPORT_VIEW_TYPE", REPORT_VIEW_TYPE);
                            startActivity(intent);
                        }
                    });
                    dialogExport.show();
                    break;
            }
        }
    }

    private void createOutFileByCategory() {
        Calendar cal = Calendar.getInstance();
        cal.set(_query.getValY(), _query.getValM(), 1);
        int noOfLastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        itemsDbAdapter.open();
        Cursor c = itemsDbAdapter.getItems(_query.getValY(), _query.getValM(), 1,
                _query.getValY(), _query.getValM(),noOfLastDay, "",
                ItemsDBAdapter.COL_CATEGORY_CODE, ItemsDBAdapter.ASC,
                ItemsDBAdapter.COL_AMOUNT, ItemsDBAdapter.DESC);

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

        if (c.moveToFirst()) {
            do {
                Item item = new Item(
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_ID)),
                        c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_CURRENCY_CODE)),
                        c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_UPDATE_DATE))
                );

                mStringBuilder.append(defaultCategory[item.getCategoryCode()]);
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

        UtilFiles.writeToFile(CreateFileInFolderActivity.TMP_FILE_ORDER_CATEGORY, mStringBuilder.toString(), _activity, Context.MODE_PRIVATE);
        mStringBuilder.setLength(0);

        itemsDbAdapter.close();
    }

    /*** todo
    private void saveSearch() {
        QueriesDBAdapter queriesDBAdapter = new QueriesDBAdapter(getActivity());
        queriesDBAdapter.open();
        queriesDBAdapter.saveItem(_query);
        queriesDBAdapter.close();
    }***/

    public void loadItemsOrderByDate() {
        Log.d(TAG, "loadItemsOrderByDate() " + _query.getQuery());

        dateHeaderList.clear();
        childDataHashMap.clear();
        income = expense = balance = new BigDecimal(0);
        int sameDateCounter = 0;
        mStringBuilder.setLength(0);
        mStringBuilder.append(getResources().getString(R.string.event_date));
        mStringBuilder.append(",");
        mStringBuilder.append(getResources().getString(R.string.amount));
        mStringBuilder.append(",");
        mStringBuilder.append(getResources().getString(R.string.category));
        mStringBuilder.append(",");
        mStringBuilder.append(getResources().getString(R.string.memo));
        mStringBuilder.append(",");
        mStringBuilder.append(getResources().getString(R.string.updated_date));
        mStringBuilder.append("\n");

        categoryList.clear();

        itemsDbAdapter.open();

        Cursor c = itemsDbAdapter.getItemsByRawQuery(_query.getQuery());

        if (c!=null && c.moveToFirst()) {
            String eventDate = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE));
            BigDecimal balanceDay = new BigDecimal(0);
            List<Item> tmpItemList = new ArrayList<>();

            do {
                //Log.d("item(memo)", c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)));

                if (!c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)).equals(eventDate)){ // if the event day of an item increases
                    dateHeaderList.add(eventDate.replace('-', ',') + "," + String.valueOf(balanceDay)); // comma is deliminator
                    childDataHashMap.put(dateHeaderList.get(sameDateCounter), tmpItemList); // set the header of the old day
                    balanceDay = BigDecimal.valueOf(0);
                    /*** change of the date ***/
                    eventDate = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)); // set a new date
                    tmpItemList = new ArrayList<>(); // empty the array list of items
                    sameDateCounter++;
                }

                Item item = new Item(
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_ID)),
                        c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_CURRENCY_CODE)),
                        c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_UPDATE_DATE))
                );

                if(c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)) == 0) {
                    income = income.add(item.getAmount());
                    balanceDay = balanceDay.add(item.getAmount());
                } else {
                    expense = expense.add(item.getAmount());
                    balanceDay = balanceDay.subtract(item.getAmount());
                }

                mStringBuilder.append(item.getEventDate());
                mStringBuilder.append(",");
                mStringBuilder.append(item.getAmount());
                mStringBuilder.append(",");
                mStringBuilder.append(defaultCategory[item.getCategoryCode()]);
                mStringBuilder.append(",");
                mStringBuilder.append(item.getMemo());
                mStringBuilder.append(",");
                mStringBuilder.append(item.getUpdateDate());
                mStringBuilder.append("\n");

                /************* For CategoryList *************/
                boolean flag = false;
                for (int i = 0; i < categoryList.size(); i++) {
                    Item tmp = categoryList.get(i);
                    if (tmp.getCategoryCode() == c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE))) {
                        int amount = tmp.getIntAmount() + c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT));

                        // todo when currency is different for each item, handle it
                        Log.d("asdf", "a: " + categoryList.get(i).getId()+" " +amount
                        + " "+c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)));

                        tmp = new Item(categoryList.get(i).getId(),
                                amount,
                                c.getString(c.getColumnIndex(ItemsDBAdapter.COL_CURRENCY_CODE)),
                                c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                                "", "", "");
                        categoryList.remove(i);
                        categoryList.add(tmp);
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    int id = 0;
                    String tmpCategory = defaultCategory[c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE))];

                    for (int i = 0; i < defaultCategory.length; i++) {
                        if (tmpCategory.equals(defaultCategory[i])) {
                            id = i;
                            break;
                        }
                    }
                    Item tmp = new Item(String.valueOf(id),
                            c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_CURRENCY_CODE)),
                            c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                            "", "", "");
                    categoryList.add(tmp);
                }

                /*********** for categoryList end ************/

                tmpItemList.add(item);
            } while (c.moveToNext());

            dateHeaderList.add(eventDate.replace('-', ',') + "," + String.valueOf(balanceDay)); // set what to show on the header
            childDataHashMap.put(dateHeaderList.get(sameDateCounter), tmpItemList);
        }

        if (categoryList.size() > 0) {
            categoryListSortByAmount();
            calculatePercentage();
        }
        makePieGraph();

        itemsDbAdapter.close();
        expandableListAdapter.notifyDataSetChanged();
        categoryListAdapter.notifyDataSetChanged();

        UtilFiles.writeToFile(CreateFileInFolderActivity.TMP_FILE_ORDER_DATE, mStringBuilder.toString(), _activity, Context.MODE_PRIVATE);
        mStringBuilder.setLength(0);

        makeBalanceTable();
    }

    //todo separate it from main loading function
    private void loadItemsOrderByCategory () {
        Log.d(TAG, "loadItemsOrderByDate() " + _query.getQuery());

        categoryList.clear();

        itemsDbAdapter.open();
        Cursor c = itemsDbAdapter.getItemsByRawQuery(_query.getQuery());

        if (c!=null && c.moveToFirst()) {
            String eventDate = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE));
            BigDecimal balanceDay = new BigDecimal(0);
            List<Item> tmpItemList = new ArrayList<>();

            do {
                Item item = new Item(
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_ID)),
                        c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_CURRENCY_CODE)),
                        c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_UPDATE_DATE))
                );

                if(c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)) == 0) {
                    income = income.add(item.getAmount());
                    balanceDay = balanceDay.add(item.getAmount());
                } else {
                    expense = expense.add(item.getAmount());
                    balanceDay = balanceDay.subtract(item.getAmount());
                }

                mStringBuilder.append(item.getEventDate());
                mStringBuilder.append(",");
                mStringBuilder.append(item.getAmount());
                mStringBuilder.append(",");
                mStringBuilder.append(defaultCategory[item.getCategoryCode()]);
                mStringBuilder.append(",");
                mStringBuilder.append(item.getMemo());
                mStringBuilder.append(",");
                mStringBuilder.append(item.getUpdateDate());
                mStringBuilder.append("\n");

                /************* For CategoryList *************/
                boolean flag = false;
                for (int i = 0; i < categoryList.size(); i++) {
                    Item tmp = categoryList.get(i);
                    if (tmp.getCategoryCode() == c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE))) {
                        int amount = tmp.getIntAmount() + c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT));

                        Log.d("asdf", "a: " + categoryList.get(i).getId()+" " +amount
                                + " "+c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)));

                        tmp = new Item(categoryList.get(i).getId(),
                                amount,
                                c.getString(c.getColumnIndex(ItemsDBAdapter.COL_CURRENCY_CODE)),
                                c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                                "", "", "");
                        categoryList.remove(i);
                        categoryList.add(tmp);
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    int id = 0;
                    String tmpCategory = defaultCategory[c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE))];

                    for (int i = 0; i < defaultCategory.length; i++) {
                        if (tmpCategory.equals(defaultCategory[i])) {
                            id = i;
                            break;
                        }
                    }
                    Item tmp = new Item(String.valueOf(id),
                            c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_CURRENCY_CODE)),
                            c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                            "", "", "");
                    categoryList.add(tmp);
                }
            } while (c.moveToNext());
        }

        if (categoryList.size() > 0) {
            categoryListSortByAmount();
            calculatePercentage();
        }
        makePieGraph();

        itemsDbAdapter.close();
        categoryListAdapter.notifyDataSetChanged();

        UtilFiles.writeToFile(CreateFileInFolderActivity.TMP_FILE_ORDER_DATE, mStringBuilder.toString(), _activity, Context.MODE_PRIVATE);
        mStringBuilder.setLength(0);

        makeBalanceTable();
    }

    void calculatePercentage() {
        Log.d("asdf", income + " " + expense);
        for (int i = 0; i < categoryList.size(); i++) {
            BigDecimal sum = income.add(expense);
            BigDecimal out = categoryList.get(i).getAmount()
                    .divide(sum, RoundingMode.DOWN)
                    .setScale(0, RoundingMode.DOWN);

            categoryList.get(i).setMemo(String.valueOf(out));
        }
    }

    void categoryListSortByAmount() {
        for (int i = 0; i < categoryList.size() - 1; i++) {
            for (int j = categoryList.size() - 1; j > i; j--) {
                BigDecimal amount_j = categoryList.get(j).getAmount();
                BigDecimal amount_j_1 = categoryList.get(j-1).getAmount();
                if (amount_j.compareTo(amount_j_1) > 0) {
                    Item tmp = categoryList.get(j);
                    categoryList.set(j, categoryList.get(j-1));
                    categoryList.set(j-1, tmp);
                }
            }
        }
    }

    private void makePieGraph() {
        graph.removeSlices();
        for (int i = 0; i < categoryList.size(); i++) {
            PieSlice slice = new PieSlice();
            if (categoryList.get(i).getCategoryCode() == 0) {
                slice.setColor(ContextCompat.getColor(_activity, R.color.colorPrimary));
            } else {
                slice.setColor(Color.parseColor(MainActivity.categoryColor[i]));
            }
            slice.setValue(categoryList.get(i).getAmount().intValue());
            graph.addSlice(slice);
        }
        graph.setThickness(100);
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
        txvIncome.setText(String.valueOf(income));
        txvExpense.setText(String.valueOf(expense));
        balance = income.subtract(expense);

        if (balance.compareTo(new BigDecimal(0)) < 0) {
            txvBalance.setTextColor(ContextCompat.getColor(_activity, R.color.colorRed));
            txvBalance.setText(String.valueOf(balance));
        }
        else if (balance.compareTo(new BigDecimal(0)) > 0) {
            txvBalance.setTextColor(ContextCompat.getColor(_activity, R.color.colorBlue));
            String str = "+" + String.valueOf(balance);
            txvBalance.setText(str);
        }
        else {
            txvBalance.setTextColor(ContextCompat.getColor(_activity, R.color.colorBlack));
            txvBalance.setText(String.valueOf(balance));
        }
    }

    public void reset() {
        //Log.d(TAG, "reset()");

        switch (_query.getType()) {
            case Query.QUERY_TYPE_NEW:
                Calendar cal = Calendar.getInstance();
                _calMonth = cal.get(Calendar.MONTH) + 1;
                _calYear = cal.get(Calendar.YEAR);

                btnDate.setText(getTextBtnDate());
                btnNext.setVisibility(View.VISIBLE);
                btnPrev.setVisibility(View.VISIBLE);
                fabDiscard.setVisibility(View.INVISIBLE);
                frlRoot.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                break;
            case Query.QUERY_TYPE_SEARCH:
                btnDate.setText(getString(R.string.title_search_criteria));
                btnNext.setVisibility(View.INVISIBLE);
                btnPrev.setVisibility(View.INVISIBLE);
                fabDiscard.setVisibility(View.VISIBLE);
                frlRoot.setBackgroundColor(getResources().getColor(R.color.colorBackground_search));
                break;
        }
    }

    public void focusOnSavedItem(Query query) {
        int m = query.getValM();
        int y = query.getValY();
        int d = query.getValD();
        _calMonth = m;
        _calYear = y;
        _query = query;

        btnDate.setText(getTextBtnDate());
        btnNext.setVisibility(View.VISIBLE);
        btnPrev.setVisibility(View.VISIBLE);
        fabDiscard.setVisibility(View.INVISIBLE);
        frlRoot.setBackgroundColor(getResources().getColor(R.color.colorBackground));

        loadItemsOrderByDate();

        for (int i = 0; i < dateHeaderList.size(); i++) {
            String[] header = dateHeaderList.get(i).split("[,]"); // ex. "2018,04,30,-700"

            //Log.d("TabFragment2","focusOnSavedItem() y:"+y+" m:"+m+" d:"+d);

            if (Integer.parseInt(header[1]) == m && Integer.parseInt(header[2]) == d) {
                expandableListView.setVisibility(View.VISIBLE);
                categoryLayout.setVisibility(View.GONE);
                REPORT_VIEW_TYPE = 0;
                expandableListView.expandGroup(i);
                expandableListView.smoothScrollToPositionFromTop(i, 0);
            } else {
                expandableListView.collapseGroup(i);
            }
        }
    }

    public void onSearch(Query query) {
        //Log.d(TAG, "onSearch() " + query.getQuery());

        _query = query;

        btnDate.setText(getString(R.string.title_search_result));
        btnNext.setVisibility(View.INVISIBLE);
        btnPrev.setVisibility(View.INVISIBLE);
        fabDiscard.setVisibility(View.VISIBLE);
        frlRoot.setBackgroundColor(getResources().getColor(R.color.colorBackground_search));

        loadItemsOrderByDate();
    }
}
