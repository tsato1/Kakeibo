package com.kakeibo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kakeibo.db.ItemsDBAdapter;
import com.kakeibo.util.UtilCurrency;
import com.kakeibo.util.UtilKeyboard;
import com.kakeibo.util.UtilDate;
import com.kakeibo.util.UtilQuery;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by T on 2015/09/14.
 */
public class TabFragment1 extends Fragment {
    private final static String TAG = TabFragment1.class.getSimpleName();

    private Activity _activity;
    private ImageButton btnPrev, btnNext;
    private Button btnDate;
    private EditText edtAmount;
    private EditText edtMemo;
    private ArrayList<Button> btnsCategory;

    private static Query _query;
    private static String _eventDate;
    private static String selectedCategory = "";
    private static int selectedCategoryCode;

    public static TabFragment1 newInstance() {
        TabFragment1 tabFragment1 = new TabFragment1();
        Bundle args = new Bundle();
        tabFragment1.setArguments(args);
        return tabFragment1;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _activity = getActivity();
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        findViews(view);
        setListeners();

        return view;
    }

    @Override
    public void onResume () {
        super.onResume();
        Log.d(TAG, "onResume() called");
        btnDate.setText(UtilDate.getTodaysDateWithDay(MainActivity.sDateFormat, MainActivity.sWeekName));
        edtAmount.setText("");
    }

    void findViews(View view)
    {
        btnPrev = view.findViewById(R.id.btn_prev);
        btnDate = view.findViewById(R.id.btn_date);
        btnNext = view.findViewById(R.id.btn_next);

        btnsCategory = new ArrayList<>();
        btnsCategory.add(view.findViewById(R.id.btn_category1));
        btnsCategory.add(view.findViewById(R.id.btn_category2));
        btnsCategory.add(view.findViewById(R.id.btn_category3));
        btnsCategory.add(view.findViewById(R.id.btn_category4));
        btnsCategory.add(view.findViewById(R.id.btn_category5));
        btnsCategory.add(view.findViewById(R.id.btn_category6));
        btnsCategory.add(view.findViewById(R.id.btn_category7));
        btnsCategory.add(view.findViewById(R.id.btn_category8));
        btnsCategory.add(view.findViewById(R.id.btn_category9));
        btnsCategory.add(view.findViewById(R.id.btn_category10));
        btnsCategory.add(view.findViewById(R.id.btn_category11));
        btnsCategory.add(view.findViewById(R.id.btn_category12));
        setButtonContent();

        edtAmount = view.findViewById(R.id.edt_amount);
        edtMemo = view.findViewById(R.id.edt_memo);
    }

    void setButtonContent() {
        for (int i = 0; i < MainActivity.sCategories.length; i++) {
            btnsCategory.get(i).setText(MainActivity.sCategories[i]);
        }
    }

    void setListeners() {
        btnPrev.setOnClickListener(new DateButtonClickListener());
        btnDate.setOnClickListener(new DateButtonClickListener());
        btnNext.setOnClickListener(new DateButtonClickListener());
        edtAmount.addTextChangedListener(new AmountTextWatcher(edtAmount));
        for (int i = 0; i < MainActivity.sCategories.length; i++) {
            btnsCategory.get(i).setOnClickListener(new CategoryButtonClickListener());
        }
    }

    class DateButtonClickListener implements View.OnClickListener {
        public void  onClick(View view) {
            String sourceDate = btnDate.getText().toString().substring(0, 10);
            //Log.d("sourceDate", sourceDate);
            SimpleDateFormat format = new SimpleDateFormat(UtilDate.DATE_FORMATS[MainActivity.sDateFormat],
                    Locale.getDefault());
            Date date = null;
            Calendar cal = Calendar.getInstance();

            switch (view.getId()) {
                case R.id.btn_prev:
                    try {
                        date = format.parse(sourceDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    cal.setTime(date);
                    cal.add(Calendar.DATE, -1);
                    date = cal.getTime();
                    String str = new SimpleDateFormat(UtilDate.DATE_FORMATS[MainActivity.sDateFormat],
                            Locale.getDefault()).format(date)
                            + " [" + MainActivity.sWeekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
                    btnDate.setText(str);
                    break;
                case R.id.btn_date:
                    showYMDPickerDialog();
                    break;
                case R.id.btn_next:
                    try {
                        date = format.parse(sourceDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    cal.setTime(date);
                    cal.add(Calendar.DATE, 1);
                    date = cal.getTime();
                    str = new SimpleDateFormat(UtilDate.DATE_FORMATS[MainActivity.sDateFormat],
                            Locale.getDefault()).format(date)
                            + " [" + MainActivity.sWeekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
                    btnDate.setText(str);
                    break;
            }
        }
    }

    class CategoryButtonClickListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_category1:
                    selectedCategory = btnsCategory.get(0).getText().toString();
                    selectedCategoryCode = 0;
                    break;
                case R.id.btn_category2:
                    selectedCategory = btnsCategory.get(1).getText().toString();
                    selectedCategoryCode = 1;
                    break;
                case R.id.btn_category3:
                    selectedCategory = btnsCategory.get(2).getText().toString();
                    selectedCategoryCode = 2;
                    break;
                case R.id.btn_category4:
                    selectedCategory = btnsCategory.get(3).getText().toString();
                    selectedCategoryCode = 3;
                    break;
                case R.id.btn_category5:
                    selectedCategory = btnsCategory.get(4).getText().toString();
                    selectedCategoryCode = 4;
                    break;
                case R.id.btn_category6:
                    selectedCategory = btnsCategory.get(5).getText().toString();
                    selectedCategoryCode = 5;
                    break;
                case R.id.btn_category7:
                    selectedCategory = btnsCategory.get(6).getText().toString();
                    selectedCategoryCode = 6;
                    break;
                case R.id.btn_category8:
                    selectedCategory = btnsCategory.get(7).getText().toString();
                    selectedCategoryCode = 7;
                    break;
                case R.id.btn_category9:
                    selectedCategory = btnsCategory.get(8).getText().toString();
                    selectedCategoryCode = 8;
                    break;
                case R.id.btn_category10:
                    selectedCategory = btnsCategory.get(9).getText().toString();
                    selectedCategoryCode = 9;
                    break;
                case R.id.btn_category11:
                    selectedCategory = btnsCategory.get(10).getText().toString();
                    selectedCategoryCode = 10;
                    break;
                case R.id.btn_category12:
                    selectedCategory = btnsCategory.get(11).getText().toString();
                    selectedCategoryCode = 11;
                    break;
            }

            if (checkBeforeSave()) {
                _query = new Query(Query.QUERY_TYPE_NEW);

                saveItem();
                ((MainActivity)_activity).getViewPager().setCurrentItem(1); // 1 = Fragment2
                ((MainActivity)_activity).onItemSaved(_query, _eventDate);
                reset();
            }
        }
    }

    boolean checkBeforeSave()
    {
        if ("".equals(selectedCategory)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.err_please_select_category), Toast.LENGTH_SHORT).show();
            return false;
        }
        if ("".equals(edtAmount.getText().toString())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.err_please_enter_amount), Toast.LENGTH_SHORT).show();
            return false;
        }
        if ("0".equals(edtAmount.getText().toString()) ||
                "0.0".equals(edtAmount.getText().toString()) ||
                "0.00".equals(edtAmount.getText().toString()) ||
                "0.000".equals(edtAmount.getText().toString())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.err_amount_cannot_be_0), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    void saveItem()
    {
        ItemsDBAdapter itemsDBAdapter = new ItemsDBAdapter();

        String[] ymd = btnDate.getText().toString().split("\\s+")[0].split("/");
        String y, m, d;

        switch (MainActivity.sDateFormat) {
            case 1: // MDY
                y = ymd[2];
                m = ymd[0];
                d = ymd[1];
                break;
            case 2: // DMY
                y = ymd[2];
                m = ymd[1];
                d = ymd[0];
                break;
            default:  // YMD
                y = ymd[0];
                m = ymd[1];
                d = ymd[2];
        }

        String eventDate = y + "-" + m + "-" + d;
        String updateDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS);

        String amount = edtAmount.getText().toString();

        Item item = new Item(
                "",
                new BigDecimal(amount),
                MainActivity.sFractionDigits,
                selectedCategoryCode,
                edtMemo.getText().toString(),
                eventDate,
                updateDate
        );

        itemsDBAdapter.open();
        itemsDBAdapter.saveItem(item);
        Toast.makeText(getActivity(), getResources().getString(R.string.msg_item_successfully_saved), Toast.LENGTH_SHORT).show();
        itemsDBAdapter.close();

        _query = new Query(Query.QUERY_TYPE_NEW);
        UtilQuery.init();
        UtilQuery.setDate(eventDate, "");
        UtilQuery.setCGroupBy(ItemsDBAdapter.COL_CATEGORY_CODE);
        UtilQuery.setCOrderBy(UtilQuery.SUM_AMOUNT, UtilQuery.DESC);
        UtilQuery.setCsWhere(ItemsDBAdapter.COL_CATEGORY_CODE);
        UtilQuery.setDOrderBy(ItemsDBAdapter.COL_EVENT_DATE, UtilQuery.ASC);
        _query.setQueryC(UtilQuery.buildQueryC());
        _query.setQueryCs(UtilQuery.buildQueryCs());
        _query.setQueryD(UtilQuery.buildQueryD());

        _eventDate = eventDate;
    }

    private void showYMDPickerDialog()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(_activity, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker picker, int year, int month, int day){
                GregorianCalendar cal = new GregorianCalendar(year, month, day);
                Date date = cal.getTime();
                String str = new SimpleDateFormat(UtilDate.DATE_FORMATS[MainActivity.sDateFormat],
                        Locale.getDefault()).format(date)
                        + " [" + MainActivity.sWeekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
                btnDate.setText(str);
            }
        }, year, month-1, day);
        dialog.show();
    }

    private void reset()
    {
        edtAmount.setText("");
        edtMemo.setText("");
        btnDate.setText(UtilDate.getTodaysDateWithDay(MainActivity.sDateFormat, MainActivity.sWeekName));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                //Log.d(TAG, "Not visible anymore.");
                UtilKeyboard.hideSoftKeyboard(_activity);
            }
        }
    }
}
