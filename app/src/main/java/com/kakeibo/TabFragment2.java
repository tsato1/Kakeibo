package com.kakeibo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.kakeibo.settings.SettingsActivity;

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
    private int income, expense, balance;
    public  int _calMonth, _calYear;
    private String[] weekName;
    private String[] defaultCategory;
    private String amountColon, memoColon, categoryColon, savedOnColon;
    private int mDateFormat;
    private View _view;
    private Query _query;
    private SharedPreferences mPref;

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
        loadSharedPreferences();

        makeDefaultQuery();
        reset();
        loadItems();

        return _view;
    }

//    @Override
//    public void onResume() {
//        Log.d(TAG, "onResume() called");
//        super.onResume();
//        makeDefaultQuery();
//        reset();
//        loadItems();
//    }

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

        itemsDbAdapter = new ItemsDBAdapter(_activity);
        dateHeaderList = new ArrayList<>();
        childDataHashMap = new HashMap<>();
        expandableListAdapter = new ExpandableListAdapter(_activity, dateHeaderList, childDataHashMap);
        expandableListView.setAdapter(expandableListAdapter);
        categoryList = new ArrayList<>();
        categoryListAdapter = new CategoryListAdapter(_activity, 0, categoryList);
        categoryListView.setAdapter(categoryListAdapter);
    }

    private void loadSharedPreferences() {
        PreferenceManager.setDefaultValues(_activity, R.xml.pref_general, false);
        mPref = PreferenceManager.getDefaultSharedPreferences(_activity);
        String f = mPref.getString(SettingsActivity.PREF_KEY_DATE_FORMAT, Util.DATE_FORMAT_YMD);
        mDateFormat = Integer.parseInt(f);
    }

    private void makeDefaultQuery() {
        String[] ymd = Util.getTodaysDate(Util.DATE_FORMAT_DB).split("-");
        _calYear = Integer.parseInt(ymd[0]);
        _calMonth = Integer.parseInt(ymd[1]);
        _query = new Query(ymd[0], ymd[1], ymd[2], mDateFormat);
        _query.buildQuery();
    }

    private void buildQuery() {
        String y = String.valueOf(_calYear);
        String m = Util.convertMtoMM(_calMonth);
        _query = new Query(Query.QUERY_TYPE_NEW);
        _query.setValDate(y, m, "01", mDateFormat);
        _query.buildQuery();
    }

    class CategoryListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lsvCat = (ListView) parent;
            Item tmp = (Item) lsvCat.getItemAtPosition(position);

            List<Item> searchResultList = new ArrayList<>();
            searchResultList.clear();

            itemsDbAdapter.open();

            String[] ym = btnDate.getText().toString().split("[/]");
            String y, m;
            switch (mDateFormat) {
                case 1: // MDY
                case 2: // DMY
                    y = ym[1];
                    m = ym[0];
                    break;
                default:  // YMD
                    y = ym[0];
                    m = ym[1];
            }

            Cursor c = itemsDbAdapter.getAllItemsInCategoryInMonth(y, m, tmp.getCategoryCode());

            if (c.moveToFirst()) {
                do {
                    Item item = new Item(
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_ID)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
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
            String savedOnText = savedOnColon + Util.getDateFromDBDate(item.getUpdateDate(), weekName, mDateFormat);
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

    @Override
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

                                loadItems();
                                itemsDbAdapter.close();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            case MENU_ITEM_ID_EDIT:
                LayoutInflater layoutInflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = layoutInflater.inflate(R.layout.dialog_item_edit, (ViewGroup) _view.findViewById(R.id.layout_root));
                TextView txvEventDate = layout.findViewById(R.id.txv_event_date);
                txvEventDate.setText(item.getEventDate());
                TextView txvCategory = layout.findViewById(R.id.txv_category);
                String categoryText = getString(R.string.category_colon) + defaultCategory[item.getCategoryCode()];
                txvCategory.setText(categoryText);
                final EditText edtAmount = layout.findViewById(R.id.edt_amount);
                edtAmount.setText(String.valueOf(Math.abs(Integer.parseInt(item.getAmount()))));
                final EditText edtMemo = layout.findViewById(R.id.edt_memo);
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
                                                amount,
                                                item.getCategoryCode(),
                                                edtMemo.getText().toString(),
                                                item.getEventDate(),
                                                Util.getTodaysDateWithDay(mDateFormat, weekName)
                                        );

                                        itemsDbAdapter.saveItem(tmp);

                                        Toast.makeText(_activity, getString(R.string.msg_change_successfully_saved), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                loadItems();
                                itemsDbAdapter.close();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
        }
        return super.onContextItemSelected(menuItem);
    }

    boolean checkBeforeSave(EditText edt_amount) {
        if ("".equals(edt_amount.getText().toString())) {
            Toast.makeText(_activity, getString(R.string.err_please_enter_amount), Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(edt_amount.getText().toString()) == 0) {
            Toast.makeText(_activity, getString(R.string.err_amount_cannot_be_0), Toast.LENGTH_SHORT).show();
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
                    loadItems();
                    if (_view.findViewById(R.id.lsv_expandable).getVisibility() != View.GONE) {
                        expandableListView.setVisibility(View.GONE);
                        categoryLayout.setVisibility(View.VISIBLE);
                    } else {
                        expandableListView.setVisibility(View.VISIBLE);
                        categoryLayout.setVisibility(View.GONE);
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
                    loadItems();
                    break;
                case R.id.btn_next:
                    _calMonth++;
                    if(_calMonth > 12) {
                        _calMonth = 1;
                        _calYear++;
                    }
                    btnDate.setText(getTextBtnDate());
                    buildQuery();
                    loadItems();
                    break;
                case R.id.fab_discard:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(_activity);
                    dialog.setIcon(R.mipmap.ic_mikan);
                    dialog.setTitle(getString(R.string.quest_do_you_want_to_discard_search));
                    dialog.setMessage(getString(R.string.msg_going_back_to_report));
                    dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            makeDefaultQuery();
                            reset();
                            loadItems();
                        }
                    });
                    dialog.show();
                    break;
                case R.id.fab_export:
                    startActivity(new Intent(_activity, CreateFileInFolderActivity.class));
                    break;
            }
        }
    }

    public void loadItems() {
        Log.d(TAG, "loadItems() " + _query.getQuery());

        dateHeaderList.clear();
        childDataHashMap.clear();
        income = expense = balance = 0;
        int sameDateCounter = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getResources().getString(R.string.event_date));
        stringBuilder.append(",");
        stringBuilder.append(getResources().getString(R.string.amount));
        stringBuilder.append(",");
        stringBuilder.append(getResources().getString(R.string.category));
        stringBuilder.append(",");
        stringBuilder.append(getResources().getString(R.string.memo));
        stringBuilder.append(",");
        stringBuilder.append(getResources().getString(R.string.updated_date));
        stringBuilder.append("\n");

        categoryList.clear();

        itemsDbAdapter.open();

        Cursor c = itemsDbAdapter.getItemsByRawQuery(_query.getQuery());

        if (c!=null && c.moveToFirst()) {
            String eventDate = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE));
            int balanceDay = 0;
            List<Item> tmpItemList = new ArrayList<>();

            do {
                //Log.d("item(memo)", c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)));

                if (!c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)).equals(eventDate)){ // if the event day of an item increases
                    dateHeaderList.add(eventDate.replace('-', ',') + "," + String.valueOf(balanceDay)); // comma is deliminator
                    childDataHashMap.put(dateHeaderList.get(sameDateCounter), tmpItemList); // set the header of the old day
                    balanceDay = 0;
                    /*** change of the date ***/
                    eventDate = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)); // set a new date
                    tmpItemList = new ArrayList<>(); // empty the array list of items
                    sameDateCounter++;
                }

                if(c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)) == 0) {
                    income += c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT));
                    balanceDay += c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT));
                } else {
                    expense += c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT));
                    balanceDay -= c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT));
                }

                Item item = new Item(
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_ID)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
                        c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_UPDATE_DATE))
                );

                stringBuilder.append(item.getEventDate());
                stringBuilder.append(",");
                stringBuilder.append(item.getAmount());
                stringBuilder.append(",");
                stringBuilder.append(defaultCategory[item.getCategoryCode()]);
                stringBuilder.append(",");
                stringBuilder.append(item.getMemo());
                stringBuilder.append(",");
                stringBuilder.append(item.getUpdateDate());
                stringBuilder.append("\n");

                /************* For CategoryList *************/
                boolean flag = false;
                for (int i = 0; i < categoryList.size(); i++) {
                    Item tmp = categoryList.get(i);
                    if (tmp.getCategoryCode() == c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE))) {
                        int amount = Integer.parseInt(tmp.getAmount()) + c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT));
                        tmp = new Item(categoryList.get(i).getId(),
                                String.valueOf(amount),
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
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
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

        UtilFiles.writeToFile(CreateFileInFolderActivity.TMP_FILE_ORDER_DATE, stringBuilder.toString(), _activity, Context.MODE_PRIVATE);
        stringBuilder.setLength(0);

        makeBalanceTable();
    }

    void calculatePercentage() {
        for (int i = 0; i < categoryList.size(); i++) {
            int in = Math.abs(income);
            int out = Math.abs(expense);
            int sum = in + out;

            categoryList.get(i).setMemo(String.valueOf(Math.abs(Integer.parseInt(categoryList.get(i).getAmount())) * 100 / sum));
        }
    }

    void categoryListSortByAmount() {
        for (int i = 0; i < categoryList.size() - 1; i++) {
            for (int j = categoryList.size() - 1; j > i; j--) {
                int amount_j = Math.abs(Integer.parseInt(categoryList.get(j).getAmount()));
                int amount_j_1 = Math.abs(Integer.parseInt(categoryList.get(j-1).getAmount()));
                if (amount_j > amount_j_1) {
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
            slice.setValue(Math.abs(Integer.parseInt(categoryList.get(i).amount)));
            graph.addSlice(slice);
        }
        graph.setThickness(200);
    }

    public String getTextBtnDate() {
        int year = _calYear;
        int month = _calMonth;

        String str;
        switch (mDateFormat) {
            case 1: // MDY
            case 2: // DMY
                str = (Util.convertMtoMM(month) + "/" + year);
                break;
            default:  // YMD
                str = (year + "/" + Util.convertMtoMM(month));
        }

        return str;
    }

    public void makeBalanceTable(){
        txvIncome.setText(String.valueOf(income));
        txvExpense.setText(String.valueOf(expense));
        balance = income - expense;

        if (balance < 0) {
            txvBalance.setTextColor(ContextCompat.getColor(_activity, R.color.colorRed));
            txvBalance.setText(String.valueOf(balance));
        }
        else if (balance > 0) {
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
        Log.d(TAG, "reset()");

        switch (_query.getType()) {
            case Query.QUERY_TYPE_NEW:
                Calendar cal = Calendar.getInstance();
                _calMonth = cal.get(Calendar.MONTH) + 1;
                _calYear = cal.get(Calendar.YEAR);

                btnDate.setText(getTextBtnDate());
                btnNext.setVisibility(View.VISIBLE);
                btnPrev.setVisibility(View.VISIBLE);
                fabDiscard.setVisibility(View.INVISIBLE);
                break;
            case Query.QUERY_TYPE_SEARCH:
                btnDate.setText(getString(R.string.title_search_criteria));
                btnNext.setVisibility(View.INVISIBLE);
                btnPrev.setVisibility(View.INVISIBLE);
                fabDiscard.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void focusOnSavedItem(Query query) {
        String m = query.getValM();
        String y = query.getValY();
        String d = query.getValD();
        _calMonth = Integer.parseInt(m);
        _calYear = Integer.parseInt(y);
        _query = query;

        btnDate.setText(getTextBtnDate());
        btnNext.setVisibility(View.VISIBLE);
        btnPrev.setVisibility(View.VISIBLE);
        fabDiscard.setVisibility(View.INVISIBLE);

        loadItems();

        for (int i = 0; i < dateHeaderList.size(); i++) {
            String[] header = dateHeaderList.get(i).split("[,]"); // ex. "2018,04,30,-700"

            //Log.d("TabFragment2","focusOnSavedItem() y:"+y+" m:"+m+" d:"+d);

            if (header[1].equals(m) && header[2].equals(d)) {
                expandableListView.setVisibility(View.VISIBLE);
                categoryLayout.setVisibility(View.GONE);
                expandableListView.expandGroup(i);
                expandableListView.smoothScrollToPositionFromTop(i, 0);
            } else {
                expandableListView.collapseGroup(i);
            }
        }
    }

    public void onSearch(Query query) {
        Log.d(TAG, "onSearch() " + query.getQuery());

        _query = query;

        //todo save query to db

        btnDate.setText(getString(R.string.title_search_result));
        btnNext.setVisibility(View.INVISIBLE);
        btnPrev.setVisibility(View.INVISIBLE);
        fabDiscard.setVisibility(View.VISIBLE);

        loadItems();
    }
}
