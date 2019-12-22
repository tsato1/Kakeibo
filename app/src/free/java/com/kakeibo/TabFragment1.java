package com.kakeibo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kakeibo.db.ItemsDBAdapter;
import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilCurrency;
import com.kakeibo.util.UtilDate;
import com.kakeibo.util.UtilKeyboard;
import com.kakeibo.util.UtilQuery;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
    private GridView grvCategories;

    private static Query _query;
    private static String _eventDate;
    private static KkbCategory _selectedKkbCategory;
    private static List<KkbCategory> _kkbCategoryList;

    static TabFragment1 newInstance() {
        TabFragment1 tabFragment1 = new TabFragment1();
        Bundle args = new Bundle();
        tabFragment1.setArguments(args);
        return tabFragment1;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _activity = getActivity();
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        _kkbCategoryList = UtilCategory.getDspKkbCategoryList(_activity);

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
        edtMemo.setText("");
    }

    @Override
    public void onPause() {
        super.onPause();
        UtilKeyboard.hideSoftKeyboard(_activity);
    }

    void findViews(View view)
    {
        btnPrev = view.findViewById(R.id.btn_prev);
        btnDate = view.findViewById(R.id.btn_date);
        btnNext = view.findViewById(R.id.btn_next);
        edtAmount = view.findViewById(R.id.edt_amount);
        edtMemo = view.findViewById(R.id.edt_memo);

        grvCategories = view.findViewById(R.id.grv_categories);
        final CategoryGridAdapter categoryGridAdapter = new CategoryGridAdapter(_activity, _kkbCategoryList);
        grvCategories.setAdapter(categoryGridAdapter);
        grvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _selectedKkbCategory = _kkbCategoryList.get(position);
                if (checkBeforeSave()) {
                    _query = new Query(Query.QUERY_TYPE_NEW);
                    saveItem();
                }
            }
        });
    }

    void setListeners() {
        btnPrev.setOnClickListener(new DateButtonClickListener());
        btnDate.setOnClickListener(new DateButtonClickListener());
        btnNext.setOnClickListener(new DateButtonClickListener());
        edtAmount.addTextChangedListener(new AmountTextWatcher(edtAmount));
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

    /*** same functionality is in TabFragment2D too ***/
    boolean checkBeforeSave()
    {
        if ("".equals(_selectedKkbCategory.getName())) {
            Toast.makeText(getActivity(), R.string.err_please_select_category, Toast.LENGTH_SHORT).show();
            return false;
        }
        if ("".equals(edtAmount.getText().toString())) {
            Toast.makeText(getActivity(), R.string.err_please_enter_amount, Toast.LENGTH_SHORT).show();
            return false;
        }
        if ("0".equals(edtAmount.getText().toString()) ||
                "0.0".equals(edtAmount.getText().toString()) ||
                "0.00".equals(edtAmount.getText().toString()) ||
                "0.000".equals(edtAmount.getText().toString())) {
            Toast.makeText(getActivity(), R.string.err_amount_cannot_be_0, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!UtilCurrency.checkAmount(edtAmount.getText().toString())) {
            Toast.makeText(getActivity(), R.string.err_amount_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    void saveItem() {
        Log.d(TAG, "saveItem() called");

        ItemsDBAdapter itemsDBAdapter = new ItemsDBAdapter();

        String eventDate = UtilDate.convertDateFormat(
                btnDate.getText().toString().split("\\s+")[0], MainActivity.sDateFormat, 3);
        String updateDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS);

        String amount = edtAmount.getText().toString();

        Item item = new Item(
                "",
                new BigDecimal(amount),
                MainActivity.sFractionDigits,
                _selectedKkbCategory.getCode(),
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

        ((MainActivity) _activity).getViewPager().setCurrentItem(1); // 1 = Fragment2
        ((MainActivity) _activity).onItemSaved(_query, _eventDate);

        btnDate.setText(UtilDate.getTodaysDateWithDay(MainActivity.sDateFormat, MainActivity.sWeekName));
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
}
