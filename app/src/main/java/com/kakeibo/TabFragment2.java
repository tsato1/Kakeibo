package com.kakeibo;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by T on 2015/09/14.
 */
public class TabFragment2 extends Fragment {
    private DBAdapter dbAdapter;
    private List<String> dateHeaderList;
    private HashMap<String, List<Item>> childDataHashMap;
    private ExpandableListAdapter expandableListAdapter;
    private ExpandableListView expandableListView;

    private Button btnPrev, btnDate, btnNext;
    private TextView txvIncome, txvExpense, txvBalance;
    private int income, expense, balance;
    public  int calMonth, calYear;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        findViews(view);
        setListeners();
        setLabel();
        setAdapters();
        loadItems();
        makeBalanceTable();

        return view;
    }

    void findViews(View view){
        btnPrev = (Button)view.findViewById(R.id.btn_prev);
        btnDate = (Button)view.findViewById(R.id.btn_date);
        btnNext = (Button)view.findViewById(R.id.btn_next);
        txvIncome = (TextView)view.findViewById(R.id.txv_income);
        txvExpense = (TextView)view.findViewById(R.id.txv_expense);
        txvBalance = (TextView)view.findViewById(R.id.txv_balance);
        expandableListView = (ExpandableListView)view.findViewById(R.id.lsv_expandable);
    }

    void setListeners(){
        btnPrev.setOnClickListener(new ButtonClickListener());
        btnDate.setOnClickListener(new ButtonClickListener());
        btnNext.setOnClickListener(new ButtonClickListener());
        expandableListView.setOnChildClickListener(new ChildClickListener());
    }

    public void setLabel()
    {
        int year = calYear;
        int month = calMonth;
        Log.d("Fragment2", "year = " + year + ", month = " + month);

        String mon = String.valueOf(month);
        if (String.valueOf(month).length() == 1) {  // convert m to mm (ex. 5 -> 05)
            mon = "0" + String.valueOf(month);
        }
        String str = (year+"/"+mon);
        btnDate.setText(str);
    }

    class ChildClickListener implements ExpandableListView.OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
            Object child = (expandableListAdapter.getChild(groupPosition, childPosition));
            Item item = (Item)child;

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(
                    "Category :\n " + item.getCategory() + "\n"
                    + "Amount :\n " + item.getAmount() + "\n"
                    + "Memo :\n " + item.getMemo() + "\n"
                    + "Registration Date :\n " + item.getUpdateDate()
            );
            dialog.show();

            return false;
        }
    }

    class ButtonClickListener implements View.OnClickListener {
        public void onClick(View view)
        {
            switch(view.getId())
            {
                case R.id.btn_date:
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
                default:
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
    }

    public void loadItems(){
        dateHeaderList.clear();
        childDataHashMap.clear();
        income = expense = balance = 0;
        int sameDateCounter = 0;

        dbAdapter.open();

        Cursor c = dbAdapter.getAllItemsInMonth(btnDate.getText().toString());

        if (c.moveToFirst())
        {
            String day = c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_D));

            dateHeaderList.add(convertMtoMM()+"/"+day);

            List<Item> tmpItemList = new ArrayList<Item>();

            do{
                //Log.d("item(memo) = ", c.getString(c.getColumnIndex(DBAdapter.COL_MEMO)));

                if(c.getString(c.getColumnIndex(DBAdapter.COL_CATEGORY)).equals(getResources().getString(R.string.income))) {
                    income += c.getInt(c.getColumnIndex(DBAdapter.COL_AMOUNT));
                } else {
                    expense += c.getInt(c.getColumnIndex(DBAdapter.COL_AMOUNT));
                }

                Item item = new Item(
                        c.getString(c.getColumnIndex(DBAdapter.COL_AMOUNT)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_CATEGORY)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_MEMO)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_D)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_YM)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_UPDATE_DATE))
                );

                /*** increment day by 1 if there is no item on that day ***/
                if (!c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_D)).equals(day)){
                    childDataHashMap.put(dateHeaderList.get(sameDateCounter), tmpItemList);
                    day = c.getString(c.getColumnIndex(DBAdapter.COL_EVENT_D));
                    dateHeaderList.add(convertMtoMM()+"/"+day);
                    tmpItemList = new ArrayList<Item>();
                    sameDateCounter++;
                }

                tmpItemList.add(item);
            }while(c.moveToNext());

            childDataHashMap.put(dateHeaderList.get(sameDateCounter), tmpItemList);
        }

        dbAdapter.close();
        expandableListAdapter.notifyDataSetChanged();
    }

    public void makeBalanceTable(){
        txvIncome.setText(String.valueOf(income));
        txvExpense.setText(String.valueOf(expense));
        balance = income - expense;
        txvBalance.setText(String.valueOf(balance));
    }

    public void reset()
    {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    String convertMtoMM() {
        String mon = String.valueOf(calMonth);
        if (String.valueOf(calMonth).length() == 1) {  // convert m to mm (ex. 5 -> 05)
            mon = "0" + String.valueOf(calMonth);
        }
        return mon;
    }
}