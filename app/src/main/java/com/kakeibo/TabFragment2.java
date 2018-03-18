package com.kakeibo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by T on 2015/09/14.
 */
public class TabFragment2 extends Fragment {
    private static final int MENUITEM_ID_DELETE = 1;
    private static final int MENUITEM_ID_EDIT = 2;

    private DBAdapter dbAdapter;
    private List<String> dateHeaderList;
    private HashMap<String, List<Item>> childDataHashMap;
    private ExpandableListAdapter expandableListAdapter;
    private ExpandableListView expandableListView;
    private List<Item> categoryList;
    private CategoryListAdapter categoryListAdapter;
    private FrameLayout categoryLayout;
    private ListView categoryListView;
    private PieGraph graph;
    private LinearLayout searchLayout;
    private ImageButton btnPrev, btnNext, btnSearch;
    private Button btnDate;
    private TextView txvIncome, txvExpense, txvBalance;
    private EditText edtSearch;
    private int income, expense, balance;
    public  int calMonth, calYear;
    private String[] weekName;
    private String[] defaultCategory;
    private String amountColon, memoColon, categoryColon, savedOnColon;

    private View _view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        weekName = getActivity().getResources().getStringArray(R.array.weekName);
        defaultCategory = getActivity().getResources().getStringArray((R.array.defaultCategory));
        amountColon = getActivity().getResources().getString(R.string.amount_colon);
        memoColon = getActivity().getResources().getString(R.string.memo_colon);
        categoryColon = getActivity().getResources().getString(R.string.category_colon);
        savedOnColon = getActivity().getResources().getString(R.string.updated_on_colon);

        findViews();
        reset();
        setListeners();
        setAdapters();
        setLabel();
        loadItems();
        makeBalanceTable();

        categoryLayout.setVisibility(View.GONE);

        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reset();
        setLabel();
        loadItems();
        makeBalanceTable();
    }

    void findViews(){
        btnPrev = (ImageButton) _view.findViewById(R.id.btn_prev);
        btnDate = (Button) _view.findViewById(R.id.btn_date);
        btnNext = (ImageButton) _view.findViewById(R.id.btn_next);
        txvIncome = (TextView) _view.findViewById(R.id.txv_income);
        txvExpense = (TextView) _view.findViewById(R.id.txv_expense);
        txvBalance = (TextView) _view.findViewById(R.id.txv_balance);
        expandableListView = (ExpandableListView) _view.findViewById(R.id.lsv_expandable);
        categoryLayout = (FrameLayout) _view.findViewById(R.id.scv_subtotal);
        graph = (PieGraph) _view.findViewById(R.id.graph_subtotal);
        categoryListView = (ListView) _view.findViewById(R.id.lsv_subtotal);

        searchLayout = (LinearLayout) _view.findViewById(R.id.lnl_search);
        //btnVoice = (ImageButton) _view.findViewById(R.id.btn_voice_search);
        btnSearch = (ImageButton) _view.findViewById(R.id.btn_search);
        edtSearch = (EditText) _view.findViewById(R.id.edt_search);
    }

    void setListeners(){
        btnPrev.setOnClickListener(new ButtonClickListener());
        btnDate.setOnClickListener(new ButtonClickListener());
        btnNext.setOnClickListener(new ButtonClickListener());
        expandableListView.setOnChildClickListener(new ChildClickListener());
//        btnVoice.setOnClickListener(new ButtonClickListener());
        btnSearch.setOnClickListener(new ButtonClickListener());
        expandableListView.setOnCreateContextMenuListener(new ChildClickContextMenuListener());
        categoryListView.setOnItemClickListener(new CategoryListItemClickListener());
    }

    class CategoryListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lsvCat = (ListView) parent;
            Item tmp = (Item) lsvCat.getItemAtPosition(position);

            List<Item> searchResultList = new ArrayList<Item>();
            searchResultList.clear();

            dbAdapter.open();
            Cursor c = dbAdapter.getAllItemsInCategoryInMonth(btnDate.getText().toString(), tmp.getCategoryCode());

            if (c.moveToFirst()) {
                do {
                    Item item = new Item(
                            c.getString(c.getColumnIndex(DBAdapter.COL_ID)),
                            c.getString(c.getColumnIndex(DBAdapter.COL_AMOUNT)),
                            c.getInt(c.getColumnIndex(DBAdapter.COL_CATEGORY_CODE)),
                            c.getString(c.getColumnIndex(DBAdapter.COL_MEMO)),
                            c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_DATE)),
                            c.getString(c.getColumnIndex(DBAdapter.COL_UPDATE_DATE))
                    );

                    searchResultList.add(item);
                } while (c.moveToNext());
            }

            dbAdapter.close();

            SearchListAdapter searchListAdapter = new SearchListAdapter(getActivity(), 0, searchResultList);
            ListView listView = new ListView(getActivity());
            listView.setAdapter(searchListAdapter);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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

    class ChildClickListener implements ExpandableListView.OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
            Object child = (expandableListAdapter.getChild(groupPosition, childPosition));
            Item item = (Item)child;

            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.dialog_item_detail, (ViewGroup)view.findViewById(R.id.layout_root));

            TextView txvCategory = (TextView) layout.findViewById(R.id.txv_detail_category);
            TextView txvAmount = (TextView) layout.findViewById(R.id.txv_detail_amount);
            TextView txvMemo = (TextView) layout.findViewById(R.id.txv_detail_memo);
            TextView txvRegistrationDate = (TextView) layout.findViewById(R.id.txv_detail_registration);

            String categoryText = categoryColon + defaultCategory[item.getCategoryCode()];
            txvCategory.setText(categoryText);
            SpannableString span1, span2;
            if (0 == (item.getCategoryCode())) {
                span1 = new SpannableString(amountColon);
                span2 = new SpannableString("+" + item.getAmount());
                span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.colorBlue)), 0, 1, 0);
            } else {
                span1 = new SpannableString(amountColon);
                span2 = new SpannableString(item.getAmount());
                span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.colorRed)), 0, 1, 0);
            }
            txvAmount.setText(TextUtils.concat(span1, span2));
            String memoText = memoColon + item.getMemo();
            txvMemo.setText(memoText);
            String savedOnText = savedOnColon = item.getUpdateDate();
            txvRegistrationDate.setText(savedOnText);

            new AlertDialog.Builder(getActivity())
                    .setIcon(R.mipmap.ic_mikan)
                    .setTitle(getActivity().getResources().getString(R.string.item_detail))
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
                menu.add(0, MENUITEM_ID_DELETE, 0, R.string.delete);
                menu.add(0, MENUITEM_ID_EDIT, 1, R.string.edit);
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
            case MENUITEM_ID_DELETE:
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.ic_mikan)
                        .setTitle(getString(R.string.quest_do_you_want_to_delete_item))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.d("groupPosition", String.valueOf(groupPosition));
                                //Log.d("childPosition", String.valueOf(childPosition));
                                dbAdapter.open();
                                final int itemId = Integer.parseInt(item.getId());

                                if(dbAdapter.deleteItem(itemId)) {
                                    Toast.makeText(getActivity(), getString(R.string.msg_item_successfully_deleted), Toast.LENGTH_SHORT).show();
                                }

                                loadItems();
                                makeBalanceTable();
                                dbAdapter.close();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            case MENUITEM_ID_EDIT:
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = layoutInflater.inflate(R.layout.dialog_item_edit, (ViewGroup) _view.findViewById(R.id.layout_root));
                TextView txvEventDate = (TextView) layout.findViewById(R.id.txv_event_date);
                txvEventDate.setText(item.getEventDate());
                TextView txvCategory = (TextView) layout.findViewById(R.id.txv_category);
                String categoryText = getString(R.string.category_colon) + defaultCategory[item.getCategoryCode()];
                txvCategory.setText(categoryText);
                final EditText edtAmount = (EditText) layout.findViewById(R.id.edt_amount);
                edtAmount.setText(String.valueOf(Math.abs(Integer.parseInt(item.getAmount()))));
                final EditText edtMemo = (EditText) layout.findViewById(R.id.edt_memo);
                edtMemo.setText(item.getMemo());

                new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.ic_mikan)
                        .setTitle(getString(R.string.title_edit_item))
                        .setView(layout)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.d("groupPosition", String.valueOf(groupPosition));
                                //Log.d("childPosition", String.valueOf(childPosition));
                                dbAdapter.open();
                                final int itemId = Integer.parseInt(item.getId());

                                if (checkBeforeSave(edtAmount)) {
                                    if(dbAdapter.deleteItem(itemId)) {
                                        String amount = "";
                                        if (item.getCategoryCode() != 0) {
                                            amount = "-" + edtAmount.getText().toString();
                                        } else {
                                            amount = edtAmount.getText().toString();
                                        }

                                        Item tmp = new Item(
                                                "",
                                                amount,
                                                item.getCategoryCode(),
                                                edtMemo.getText().toString(),
                                                item.getEventDate(),
                                                getTodaysDate().toString()
                                        );

                                        dbAdapter.saveItem(tmp);

                                        Toast.makeText(getActivity(), getString(R.string.msg_change_successfully_saved), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                loadItems();
                                makeBalanceTable();
                                dbAdapter.close();
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
            Toast.makeText(getActivity(), getString(R.string.err_please_enter_amount), Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(edt_amount.getText().toString()) == 0) {
            Toast.makeText(getActivity(), getString(R.string.err_amount_cannot_be_0), Toast.LENGTH_SHORT).show();
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
                        //subtotalScrollView.setVisibility(View.VISIBLE);
                        categoryLayout.setVisibility(View.VISIBLE);
                        searchLayout.setVisibility(View.GONE);
                    } else {
                        expandableListView.setVisibility(View.VISIBLE);
                        //subtotalScrollView.setVisibility(View.GONE);
                        categoryLayout.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.btn_prev:
                    calMonth--;
                    if(calMonth <=0) {
                        calMonth = 12;
                        calYear--;
                        if (calYear <= 0) {
                            calYear = 2015;
                        }
                    }
                    btnDate.setText(calYear + "/" + convertMtoMM());
                    loadItems();
                    makeBalanceTable();
                    break;
                case R.id.btn_next:
                    calMonth++;
                    if(calMonth > 12) {
                        calMonth = 1;
                        calYear++;
                    }
                    btnDate.setText(calYear + "/" + convertMtoMM());
                    loadItems();
                    makeBalanceTable();
                    break;
                //case R.id.btn_voice_search:
                    //break;
                case R.id.btn_search:
                    searchItem();
                    break;
            }
        }
    }

    void setAdapters(){
        dbAdapter = new DBAdapter(getActivity());

        dateHeaderList = new ArrayList<String>();
        childDataHashMap = new HashMap<String, List<Item>>();
        expandableListAdapter = new ExpandableListAdapter(getActivity(), dateHeaderList, childDataHashMap);
        expandableListView.setAdapter(expandableListAdapter);

        categoryList = new ArrayList<Item>();
        categoryListAdapter = new CategoryListAdapter(getActivity(), 0, categoryList);
        categoryListView.setAdapter(categoryListAdapter);
    }

    public void loadItems(){
        dateHeaderList.clear();
        childDataHashMap.clear();
        income = expense = balance = 0;
        int sameDateCounter = 0;

        categoryList.clear();

        dbAdapter.open();

        Cursor c = dbAdapter.getAllItemsInMonth(btnDate.getText().toString());

        if (c.moveToFirst()) {
            String day = c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_DATE));
            int balanceDay = 0;
            List<Item> tmpItemList = new ArrayList();

            do {
                //Log.d("item(memo) = ", c.getString(c.getColumnIndex(DBAdapter.COL_MEMO)));

                if (!c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_DATE)).equals(day)){ // if the event day of an item increases
                    dateHeaderList.add(calYear + convertMtoMM() + "/" + day + "," + String.valueOf(balanceDay)); // set what to show on the header
                    childDataHashMap.put(dateHeaderList.get(sameDateCounter), tmpItemList); // set the header of the old day
                    balanceDay = 0;
                    /*** change of the date ***/
                    day = c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_DATE)); // set a new date
                    tmpItemList = new ArrayList(); // empty the array list of items
                    sameDateCounter++;
                }

                if(c.getInt(c.getColumnIndex(DBAdapter.COL_CATEGORY_CODE)) == 0) {
                    income += c.getInt(c.getColumnIndex(DBAdapter.COL_AMOUNT));
                    balanceDay += c.getInt(c.getColumnIndex(DBAdapter.COL_AMOUNT));
                } else {
                    expense += c.getInt(c.getColumnIndex(DBAdapter.COL_AMOUNT));
                    balanceDay += c.getInt(c.getColumnIndex(DBAdapter.COL_AMOUNT));
                }

                Item item = new Item(
                        c.getString(c.getColumnIndex(DBAdapter.COL_ID)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_AMOUNT)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_CATEGORY_CODE)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_MEMO)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_DATE)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_UPDATE_DATE))
                );

                /************* For CategoryList *************/
                boolean flag = false;
                for (int i = 0; i < categoryList.size(); i++) {
                    Item tmp = categoryList.get(i);
                    if (tmp.getCategoryCode() == c.getInt(c.getColumnIndex(DBAdapter.COL_CATEGORY_CODE))) {
                        int amount = Integer.parseInt(tmp.getAmount()) + c.getInt(c.getColumnIndex(DBAdapter.COL_AMOUNT));
                        tmp = new Item(categoryList.get(i).getId(),
                                String.valueOf(amount),
                                c.getInt(c.getColumnIndex(DBAdapter.COL_CATEGORY_CODE)),
                                "", "", "");
                        categoryList.remove(i);
                        categoryList.add(tmp);
                        flag = true;
                        break;
                    }
                }

                if (flag == false) {
                    int id = 0;
                    String tmpCategory = defaultCategory[c.getInt(c.getColumnIndex(DBAdapter.COL_CATEGORY_CODE))];

                    for (int i = 0; i < defaultCategory.length; i++) {
                        if (tmpCategory.equals(defaultCategory[i])) {
                            id = i;
                            break;
                        }
                    }
                    Item tmp = new Item(String.valueOf(id),
                            c.getString(c.getColumnIndex(DBAdapter.COL_AMOUNT)),
                            c.getInt(c.getColumnIndex(DBAdapter.COL_CATEGORY_CODE)),
                            "", "", "");
                    categoryList.add(tmp);
                }

                /*********** for categoryList end ************/

                tmpItemList.add(item);
            }while (c.moveToNext());

            dateHeaderList.add(calYear + convertMtoMM() + "/" + day + "," + String.valueOf(balanceDay)); // set what to show on the header
            childDataHashMap.put(dateHeaderList.get(sameDateCounter), tmpItemList);
        }

        if (categoryList.size() > 0) {
            categoryListSortByAmount();
            calculatePercentage();
        }
        makePieGraph();

        dbAdapter.close();
        expandableListAdapter.notifyDataSetChanged();
        categoryListAdapter.notifyDataSetChanged();
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
                slice.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            } else {
                slice.setColor(Color.parseColor(MainActivity.categoryColor[i]));
            }
            slice.setValue(Math.abs(Integer.parseInt(categoryList.get(i).amount)));
            graph.addSlice(slice);
        }
        graph.setThickness(200);
    }

    void searchItem() {
        String searchItem = edtSearch.getText().toString();

        if ("".equals(searchItem.trim())) {
            Toast.makeText(getActivity(), getString(R.string.err_search_word_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        List<Item> searchResultList = new ArrayList<Item>();

        dbAdapter.open();

        Cursor c = dbAdapter.getAllItemsInMonth(btnDate.getText().toString());

        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex(DBAdapter.COL_MEMO)).contains(searchItem)) {
                    Item item = new Item(
                            c.getString(c.getColumnIndex(DBAdapter.COL_ID)),
                            c.getString(c.getColumnIndex(DBAdapter.COL_AMOUNT)),
                            c.getInt(c.getColumnIndex(DBAdapter.COL_CATEGORY_CODE)),
                            c.getString(c.getColumnIndex(DBAdapter.COL_MEMO)),
                            c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_DATE)),
                            c.getString(c.getColumnIndex(DBAdapter.COL_UPDATE_DATE))
                    );

                    searchResultList.add(item);
                }
            } while (c.moveToNext());
        }

        dbAdapter.close();

        SearchListAdapter searchListAdapter = new SearchListAdapter(getActivity(), 0, searchResultList);
        ListView listView = new ListView(getActivity());
        listView.setAdapter(searchListAdapter);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setIcon(R.mipmap.ic_mikan);
        dialog.setTitle(getString(R.string.title_search_result));
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setView(listView).create();
        dialog.show();
    }

    public void setLabel()
    {
        int year = calYear;
        int month = calMonth;
        //Log.d("Fragment2", "year = " + year + ", month = " + month);

        String mon = String.valueOf(month);
        if (String.valueOf(month).length() == 1) {  // convert m to mm (ex. 5 -> 05)
            mon = "0" + String.valueOf(month);
        }
        String str = (year+"/"+mon);
        btnDate.setText(str);
    }

    public void makeBalanceTable(){
        txvIncome.setText(String.valueOf(income));
        txvExpense.setText(String.valueOf(expense));
        balance = income + expense;

        if (balance < 0) {
            txvBalance.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
            txvBalance.setText(String.valueOf(balance));
        }
        else if (balance > 0) {
            txvBalance.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlue));
            txvBalance.setText("+" + String.valueOf(balance));
        }
        else {
            txvBalance.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlack));
            txvBalance.setText(String.valueOf(balance));
        }
    }

    public void reset()
    {
        Calendar cal = Calendar.getInstance();
        calMonth = cal.get(Calendar.MONTH) + 1;
        calYear = cal.get(Calendar.YEAR);

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(_view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    String convertMtoMM() {
        String mon = String.valueOf(calMonth);
        if (String.valueOf(calMonth).length() == 1) {  // convert m to mm (ex. 5 -> 05)
            mon = "0" + String.valueOf(calMonth);
        }
        return mon;
    }

    String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String mon = String.valueOf(month);
        if (String.valueOf(month).length() == 1) {  // convert m to mm (ex. 5 -> 05)
            mon = "0" + String.valueOf(month);
        }
        String str = (year+"/"+mon+"/"+day+" [" + weekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]");
        return str;
    }

    public void focusOnSavedItem(String y, String m, String d) {
        calMonth = Integer.parseInt(m);
        calYear = Integer.parseInt(y);

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(_view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        setLabel();
        loadItems();
        makeBalanceTable();

        for (int i = 0; i < dateHeaderList.size(); i++) {
            if (dateHeaderList.get(i).substring(4, 9).equals(m + "/" + d)) { // dateHeaderList.get(i) = 'yyyymm/dd, \\\' (\\\ = balance)
                expandableListView.setVisibility(View.VISIBLE);
                categoryLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);

                expandableListView.expandGroup(i);
                expandableListView.smoothScrollToPositionFromTop(i, 0);
            } else {
                expandableListView.collapseGroup(i);
            }
        }
    }
}
