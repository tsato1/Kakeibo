package com.kakeibo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.kakeibo.db.ItemsDBAdapter;
import com.kakeibo.settings.SettingsActivity;
import com.kakeibo.settings.UtilKeyboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class TabFragment3 extends Fragment {
    private final static String TAG = TabFragment3.class.getSimpleName();

    private String[] weekName;
    private ItemsDBAdapter itemsDbAdapter;
    private Button btnFromDate, btnToDate;
    private ImageButton btnSearch;
    private EditText edtSearch;
    private View _view;
    private int mDateFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        weekName = getActivity().getResources().getStringArray(R.array.weekName);

        loadSharedPreference();
        findViews();
        setListeners();
        reset();

        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSharedPreference();
        findViews();
        setListeners();
        reset();
    }

    private void loadSharedPreference() {
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_general, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String f = pref.getString(SettingsActivity.PREF_KEY_DATE_FORMAT, Util.DATE_FORMAT_YMD);
        mDateFormat = Integer.parseInt(f);
    }

    private void findViews() {
        //btnVoice = _view.findViewById(R.id.btn_voice_search);
        btnSearch = _view.findViewById(R.id.btn_search);
        btnFromDate = _view.findViewById(R.id.btn_from_date);
        btnToDate = _view.findViewById(R.id.btn_to_date);
        edtSearch = _view.findViewById(R.id.edt_memo_search);

        itemsDbAdapter = new ItemsDBAdapter(getActivity());
    }

    private void setListeners() {
        btnFromDate.setOnClickListener(new ButtonClickListener());
        btnToDate.setOnClickListener(new ButtonClickListener());
        btnSearch.setOnClickListener(new ButtonClickListener());
    }

    private void reset() {
        switch (mDateFormat) {
            case 1: // MDY
                btnFromDate.setText(Util.getTodaysDate(Util.DATE_FORMAT_MDY));
                btnToDate.setText(Util.getTodaysDate(Util.DATE_FORMAT_MDY));
                break;
            case 2: // DMY
                btnFromDate.setText(Util.getTodaysDate(Util.DATE_FORMAT_DMY));
                btnToDate.setText(Util.getTodaysDate(Util.DATE_FORMAT_DMY));
                break;
            default:  // YMD
                btnFromDate.setText(Util.getTodaysDate(Util.DATE_FORMAT_YMD));
                btnToDate.setText(Util.getTodaysDate(Util.DATE_FORMAT_YMD));
        }
    }

    class ButtonClickListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_from_date:
                    showYMDPickerDialog(btnFromDate);
                    break;
                case R.id.btn_to_date:
                    showYMDPickerDialog(btnToDate);
                    break;
                case R.id.btn_search:
                    searchItem();
                    break;
            }
        }
    }

    private void showYMDPickerDialog(final Button button) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker picker, int year, int month, int day){
                GregorianCalendar cal = new GregorianCalendar(year, month, day);
                Date date = cal.getTime();
                String str = new SimpleDateFormat(Util.DATE_FORMATS[mDateFormat],
                        Locale.getDefault()).format(date);
                button.setText(str);
            }
        }, year, month-1, day);
        dialog.show();
    }

    void searchItem() {
        String searchItem = edtSearch.getText().toString();

//        if ("".equals(searchItem.trim())) {
//            Toast.makeText(getActivity(), getString(R.string.err_search_word_empty), Toast.LENGTH_SHORT).show();
//            return;
//        }

        List<Item> searchResultList = new ArrayList<>();

        itemsDbAdapter.open();

        String[] fromDateYMD = btnFromDate.getText().toString().split("[/]");
        String[] toDateYMD = btnToDate.getText().toString().split("[/]");
        String fromDateY, fromDateM, fromDateD, toDateY, toDateM, toDateD;
        switch (mDateFormat) {
            case 1: // MDY
                fromDateY = fromDateYMD[2];
                fromDateM = fromDateYMD[0];
                fromDateD = fromDateYMD[1];
                toDateY = toDateYMD[2];
                toDateM = toDateYMD[0];
                toDateD = toDateYMD[1];
                break;
            case 2: // DMY
                fromDateY = fromDateYMD[2];
                fromDateM = fromDateYMD[1];
                fromDateD = fromDateYMD[0];
                toDateY = toDateYMD[2];
                toDateM = toDateYMD[1];
                toDateD = toDateYMD[0];
                break;
            default:  // YMD
                fromDateY = fromDateYMD[0];
                fromDateM = fromDateYMD[1];
                fromDateD = fromDateYMD[2];
                toDateY = toDateYMD[0];
                toDateM = toDateYMD[1];
                toDateD = toDateYMD[2];
        }

        //todo check if from date is younger thant to date

        String argMemo = edtSearch.getText().toString();

        Cursor c = itemsDbAdapter.getItems(
                fromDateY, fromDateM, fromDateD,
                toDateY, toDateM, toDateD,
                argMemo);

        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)).contains(searchItem)) {
                    Item item = new Item(
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_ID)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
                            c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)),
                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_UPDATE_DATE))
                    );

                    searchResultList.add(item);
                }
            } while (c.moveToNext());
        }

        itemsDbAdapter.close();

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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                //Log.d(TAG, "Not visible anymore.");
                UtilKeyboard.hideSoftKeyboard(getActivity());
            }
        }
    }
}
