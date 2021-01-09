package com.kakeibo.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.kakeibo.R;
import com.kakeibo.SubApp;
import com.kakeibo.data.CategoryStatus;
import com.kakeibo.data.ItemStatus;
import com.kakeibo.databinding.FragmentInputBinding;
import com.kakeibo.db.ItemDBAdapter;
import com.kakeibo.ui.categories.CategoryDspStatusViewModel;
import com.kakeibo.ui.categories.CategoryGridAdapter;
import com.kakeibo.ui.categories.CategoryStatusViewModel;
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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Created by T on 2015/09/14.
 */
public class TabFragment1 extends Fragment implements ItemSaveListener {
    private final static String TAG = TabFragment1.class.getSimpleName();

    private static ItemStatusViewModel _itemStatusViewModel;

    private Activity _activity;
    private Button _btnDate;
    private EditText _edtAmount;
    private EditText _edtMemo;

    private static String[] _weekNames;
    private static int _dateFormat;
    private static int _numColumns;

    private static Query _query;

    public static TabFragment1 newInstance() {
        TabFragment1 tabFragment1 = new TabFragment1();
        Bundle args = new Bundle();
        tabFragment1.setArguments(args);
        return tabFragment1;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _activity = getActivity();
        _weekNames = getResources().getStringArray(R.array.week_name);
        _dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format);
        _numColumns = SubApp.getNumColumns(R.string.pref_key_num_columns);

        BillingViewModel billingViewModel =
                ViewModelProviders.of(requireActivity()).get(BillingViewModel.class);
        SubscriptionStatusViewModel subscriptionViewModel =
                ViewModelProviders.of(requireActivity()).get(SubscriptionStatusViewModel.class);
        _itemStatusViewModel =
                ViewModelProviders.of(requireActivity()).get(ItemStatusViewModel.class);
        CategoryStatusViewModel categoryStatusViewModel =
                ViewModelProviders.of(requireActivity()).get(CategoryStatusViewModel.class);
        CategoryDspStatusViewModel categoryDspStatusViewModel =
                ViewModelProviders.of(requireActivity()).get(CategoryDspStatusViewModel.class);

        FragmentInputBinding fragmentBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_input, container, false);
        fragmentBinding.setLifecycleOwner(this);
        fragmentBinding.setBillingViewModel(billingViewModel);
        fragmentBinding.setSubscriptionViewModel(subscriptionViewModel);
        fragmentBinding.setItemStatusViewModel(_itemStatusViewModel);
        fragmentBinding.setCategoryStatusViewModel(categoryStatusViewModel);
        fragmentBinding.setCategoryDspStatusViewModel(categoryDspStatusViewModel);
        View view = fragmentBinding.getRoot();

        categoryStatusViewModel.getCategoryCodes().observe(getViewLifecycleOwner(), new Observer<List<Integer>>() {
            @Override
            public void onChanged(List<Integer> categoryCodes) {
                _query = new Query(Query.QUERY_TYPE_NEW);
                UtilQuery.init(categoryCodes);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.rcv_input_categories);
        final CategoryGridAdapter categoryGridAdapter = new CategoryGridAdapter(this);
        categoryDspStatusViewModel.getDspCategoryStatuses().observe(getViewLifecycleOwner(), new Observer<List<CategoryStatus>>() {
            @Override
            public void onChanged(List<CategoryStatus> categoryStatuses) {
                if (categoryStatuses!=null && !categoryStatuses.isEmpty()) Log.d("asdf", categoryStatuses.get(0).getName());
                if (categoryStatuses!=null && !categoryStatuses.isEmpty()) Log.d("asdf", categoryStatuses.get(1).getName());
                categoryGridAdapter.setCategoryStatuses(categoryStatuses);
            }
        });
        recyclerView.setAdapter(categoryGridAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(_activity, _numColumns));

        ImageButton btnPrev = view.findViewById(R.id.btn_prev);
        ImageButton btnNext = view.findViewById(R.id.btn_next);
        _btnDate = view.findViewById(R.id.btn_date);
        _edtAmount = view.findViewById(R.id.edt_amount);
        _edtMemo = view.findViewById(R.id.edt_memo);
        btnPrev.setOnClickListener(new ButtonClickListener());
        btnNext.setOnClickListener(new ButtonClickListener());
        _btnDate.setOnClickListener(new ButtonClickListener());
        _edtAmount.addTextChangedListener(new AmountTextWatcher(_edtAmount));

        return view;
    }

    @Override
    public void onResume () {
        super.onResume();
        Log.d(TAG, "onResume() called");
        _btnDate.setText(UtilDate.getTodaysDateWithDay(
                SubApp.getDateFormat(R.string.pref_key_date_format),
                getResources().getStringArray(R.array.week_name)));
        _edtAmount.setText("");
        _edtMemo.setText("");
    }

    @Override
    public void onPause() {
        super.onPause();
//        _activity.getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
//        );
//        ((InputMethodManager) _activity.getSystemService(Activity.INPUT_METHOD_SERVICE))
//                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    boolean checkBeforeSave() {
        if ("".equals(_edtAmount.getText().toString())) {
            Toast.makeText(getActivity(), R.string.err_please_enter_amount, Toast.LENGTH_SHORT).show();
            return false;
        }
        if ("0".equals(_edtAmount.getText().toString()) ||
                "0.0".equals(_edtAmount.getText().toString()) ||
                "0.00".equals(_edtAmount.getText().toString()) ||
                "0.000".equals(_edtAmount.getText().toString())) {
            Toast.makeText(getActivity(), R.string.err_amount_cannot_be_0, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!UtilCurrency.checkAmount(_edtAmount.getText().toString())) {
            Toast.makeText(getActivity(), R.string.err_amount_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onItemSaved(int categoryCode) {
        Log.d("onItemSaved", "categoryCode=" + categoryCode);

        if (!checkBeforeSave()) return;

        String eventDate = UtilDate.convertDateFormat(
                _btnDate.getText().toString().split("\\s+")[0], _dateFormat, 3);
        String updateDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS);

        String amount = _edtAmount.getText().toString();

        ItemStatus itemStatus = new ItemStatus(
                new BigDecimal(amount),
                UtilCurrency.CURRENCY_NONE,
                categoryCode,
                _edtMemo.getText().toString(),
                eventDate,
                updateDate
        );

        _itemStatusViewModel.insert(itemStatus);

        Toast.makeText(getActivity(), getResources().getString(R.string.msg_item_successfully_saved), Toast.LENGTH_SHORT).show();

        UtilQuery.setDate(eventDate, "");
        UtilQuery.setCGroupBy(ItemDBAdapter.COL_CATEGORY_CODE);
        UtilQuery.setCOrderBy(UtilQuery.SUM_AMOUNT, UtilQuery.DESC);
        UtilQuery.setCsWhere(ItemDBAdapter.COL_CATEGORY_CODE);
        UtilQuery.setDOrderBy(ItemDBAdapter.COL_EVENT_DATE, UtilQuery.ASC);
        UtilQuery.build(_query);

        ((MainActivity) _activity).onItemSaved(_query, eventDate);

        _btnDate.setText(UtilDate.getTodaysDateWithDay(_dateFormat, _weekNames));
    }

    class ButtonClickListener implements View.OnClickListener {
        public void onClick(View view) {
            String sourceDate = _btnDate.getText().toString().substring(0, 10);

            Log.d("sourceDate=", sourceDate);

            SimpleDateFormat format = new SimpleDateFormat(
                    UtilDate.DATE_FORMATS[_dateFormat],
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
                    String str = new SimpleDateFormat(UtilDate.DATE_FORMATS[_dateFormat],
                            Locale.getDefault()).format(date)
                            + " [" + _weekNames[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
                    _btnDate.setText(str);
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
                    str = new SimpleDateFormat(UtilDate.DATE_FORMATS[_dateFormat],
                            Locale.getDefault()).format(date)
                            + " [" + _weekNames[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
                    _btnDate.setText(str);
                    break;
            }
        }
    }

    private void showYMDPickerDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(_activity, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker picker, int year, int month, int day){
                GregorianCalendar cal = new GregorianCalendar(year, month, day);
                Date date = cal.getTime();
                String str = new SimpleDateFormat(UtilDate.DATE_FORMATS[_dateFormat],
                        Locale.getDefault()).format(date)
                        + " [" + _weekNames[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
                _btnDate.setText(str);
            }
        }, year, month-1, day);
        dialog.show();
    }
}
