package com.kakeibo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.lang.reflect.Array;
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
    private ImageButton btnPrev, btnNext, btnVoice, btnSearch;
    private Button btnDate;
    private TextView txvIncome, txvExpense, txvBalance;
    private EditText edtSearch;
    private int income, expense, balance;
    public  int calMonth, calYear;

    private View _view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        findViews();
        setListeners();
        setLabel();
        setAdapters();
        loadItems();
        makeBalanceTable();

        categoryLayout.setVisibility(View.GONE);

        return _view;
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

            txvCategory.setText("Category: " + item.getCategory());
            SpannableString spannableString;
            if ("Income".equals(item.getCategory())) {
                String string = "Amount: " + "+" + item.getAmount();
                spannableString = new SpannableString(string);
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.ColorBlue)), 8, 9, 0);
            } else {
                String string = "Amount: " + item.getAmount();
                spannableString = new SpannableString(string);
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.ColorRed)), 8, 9, 0);
            }
            txvAmount.setText(spannableString);
            txvMemo.setText("Memo: " + item.getMemo());
            txvRegistrationDate.setText("Registered on " + item.getUpdateDate());

            new AlertDialog.Builder(getActivity())
                    .setIcon(R.mipmap.ic_mikan)
                    .setTitle("Item Detail")
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
                menu.setHeaderTitle("Options");
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
                        .setTitle("Do you really want to delete this item?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.d("groupPosition", String.valueOf(groupPosition));
                                //Log.d("childPosition", String.valueOf(childPosition));
                                dbAdapter.open();
                                final int itemId = Integer.parseInt(item.getId());

                                if(dbAdapter.deleteItem(itemId)) {
                                    Toast.makeText(getActivity(), "The item was successfully deleted.", Toast.LENGTH_SHORT).show();
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
                txvEventDate.setText(item.getEventYM() + "/" + item.getEventD());
                TextView txvCategory = (TextView) layout.findViewById(R.id.txv_category);
                txvCategory.setText("Category: " + item.getCategory());
                final EditText edtAmount = (EditText) layout.findViewById(R.id.edt_amount);
                edtAmount.setText(String.valueOf(Math.abs(Integer.parseInt(item.getAmount()))));
                final EditText edtMemo = (EditText) layout.findViewById(R.id.edt_memo);
                edtMemo.setText(item.getMemo());

                new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.ic_mikan)
                        .setTitle("Edit item")
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
                                        if (!item.getCategory().equals(MainActivity.defaultCategory[0])) {
                                            amount = "-" + edtAmount.getText().toString();
                                        } else {
                                            amount = edtAmount.getText().toString();
                                        }

                                        Item tmp = new Item(
                                                "",
                                                amount,
                                                item.getCategory(),
                                                edtMemo.getText().toString(),
                                                item.getEventD(),
                                                item.getEventYM(),
                                                getTodaysDate().toString()
                                        );

                                        dbAdapter.saveItem(tmp);

                                        Toast.makeText(getActivity(), "The change was successfully saved.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                loadItems();
                                makeBalanceTable();
                                dbAdapter.close();
                            }
                        })
                        .setNegativeButton(R.string.quit, null)
                        .show();
                return true;
        }
        return super.onContextItemSelected(menuItem);
    }

    boolean checkBeforeSave(EditText edt_amount) {
        if ("".equals(edt_amount.getText().toString())) {
            Toast.makeText(getActivity(), "Please enter amount.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(edt_amount.getText().toString()) == 0) {
            Toast.makeText(getActivity(), "Amount cannot be 0.", Toast.LENGTH_SHORT).show();
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
            String day = c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_D));
            int balanceDay = 0;
            List<Item> tmpItemList = new ArrayList<Item>();

            do {
                //Log.d("item(memo) = ", c.getString(c.getColumnIndex(DBAdapter.COL_MEMO)));

                if (!c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_D)).equals(day)){ // if the event day of an item increases
                    dateHeaderList.add(convertMtoMM() + "/" + day + "," + String.valueOf(balanceDay)); // set what to show on the header
                    childDataHashMap.put(dateHeaderList.get(sameDateCounter), tmpItemList); // set the header of the old day
                    balanceDay = 0;
                    /*** change of the date ***/
                    day = c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_D)); // set a new date
                    tmpItemList = new ArrayList<Item>(); // empty the array list of items
                    sameDateCounter++;
                }

                if(c.getString(c.getColumnIndex(DBAdapter.COL_CATEGORY)).equals(getResources().getString(R.string.income))) {
                    income += c.getInt(c.getColumnIndex(DBAdapter.COL_AMOUNT));
                    balanceDay += c.getInt(c.getColumnIndex(DBAdapter.COL_AMOUNT));
                } else {
                    expense += c.getInt(c.getColumnIndex(DBAdapter.COL_AMOUNT));
                    balanceDay += c.getInt(c.getColumnIndex(DBAdapter.COL_AMOUNT));
                }

                Item item = new Item(
                        c.getString(c.getColumnIndex(DBAdapter.COL_ID)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_AMOUNT)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_CATEGORY)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_MEMO)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_D)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_YM)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_UPDATE_DATE))
                );

                /************* For CategoryList *************/
                boolean flag = false;
                for (int i = 0; i < categoryList.size(); i++) {
                    Item tmp = categoryList.get(i);
                    if (tmp.getCategory().equals(c.getString(c.getColumnIndex(DBAdapter.COL_CATEGORY)))) {
                        int amount = Integer.parseInt(tmp.getAmount()) + c.getInt(c.getColumnIndex(DBAdapter.COL_AMOUNT));
                        tmp = new Item(categoryList.get(i).getId(),
                                String.valueOf(amount),
                                c.getString(c.getColumnIndex(DBAdapter.COL_CATEGORY)),
                                "", "", "", "");
                        categoryList.remove(i);
                        categoryList.add(tmp);
                        flag = true;
                        break;
                    }
                }

                if (flag == false) {
                    int id = 0;
                    String tmpCategory = c.getString(c.getColumnIndex(DBAdapter.COL_CATEGORY));

                    for (int i = 0; i < MainActivity.defaultCategory.length; i++) {
                        if (tmpCategory.equals(MainActivity.defaultCategory[i])) {
                            id = i;
                            break;
                        }
                    }
                    Item tmp = new Item(String.valueOf(id),
                            c.getString(c.getColumnIndex(DBAdapter.COL_AMOUNT)),
                            c.getString(c.getColumnIndex(DBAdapter.COL_CATEGORY)),
                            "", "", "", "");
                    categoryList.add(tmp);
                }

                /*********** for categoryList end ************/

                tmpItemList.add(item);
            }while (c.moveToNext());

            dateHeaderList.add(convertMtoMM() + "/" + day + "," + String.valueOf(balanceDay)); // set what to show on the header
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
                int amount_j = Integer.parseInt(categoryList.get(j).getAmount());
                int amount_j_1 = Integer.parseInt(categoryList.get(j-1).getAmount());
                if (amount_j < amount_j_1) {
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
            if (categoryList.get(i).getCategory().equals(getString(R.string.income))) {
                slice.setColor(ContextCompat.getColor(getContext(), R.color.ColorPrimary));
            } else {
                slice.setColor(Color.parseColor(MainActivity.categoryColor[i]));
            }
            slice.setValue(Integer.parseInt(categoryList.get(i).amount));
            graph.addSlice(slice);
        }
        graph.setThickness(200);
    }

    void searchItem() {
        String searchItem = edtSearch.getText().toString();

        if ("".equals(searchItem.trim())) {
            Toast.makeText(getActivity(), "Search word is empty", Toast.LENGTH_SHORT).show();
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
                            c.getString(c.getColumnIndex(DBAdapter.COL_CATEGORY)),
                            c.getString(c.getColumnIndex(DBAdapter.COL_MEMO)),
                            c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_D)),
                            c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_YM)),
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
        dialog.setTitle("Search Result");
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
            txvBalance.setTextColor(ContextCompat.getColor(getActivity(), R.color.ColorRed));
            txvBalance.setText(String.valueOf(balance));
        }
        else if (balance > 0) {
            txvBalance.setTextColor(ContextCompat.getColor(getActivity(), R.color.ColorBlue));
            txvBalance.setText("+" + String.valueOf(balance));
        }
        else {
            txvBalance.setTextColor(ContextCompat.getColor(getActivity(), R.color.ColorBlack));
            txvBalance.setText(String.valueOf(balance));
        }
    }

    public void reset()
    {
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
        String str = (year+"/"+mon+"/"+day+" [" + MainActivity.weekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]");
        return str;
    }
}