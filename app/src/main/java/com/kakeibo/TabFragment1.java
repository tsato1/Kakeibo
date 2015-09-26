package com.kakeibo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by T on 2015/09/14.
 */
public class TabFragment1 extends Fragment
{
    public  Button btnDate;

    private ImageButton btnPrev, btnNext;
    private ArrayList<Button> btnsCategory;
    private EditText edt_amount;
    private EditText edt_memo;
    private String selectedCategory = "";
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        findViews(view);
        setListeners();
        btnDate.setText(getTodaysDate());
        reset();

        return view;
    }

    void findViews(View view)
    {
        btnPrev = (ImageButton)view.findViewById(R.id.btn_prev);
        btnDate = (Button)view.findViewById(R.id.btn_date);
        btnNext = (ImageButton)view.findViewById(R.id.btn_next);

        btnsCategory = new ArrayList<Button>();
        btnsCategory.add((Button) view.findViewById(R.id.btn_category1));
        btnsCategory.add((Button) view.findViewById(R.id.btn_category2));
        btnsCategory.add((Button) view.findViewById(R.id.btn_category3));
        btnsCategory.add((Button) view.findViewById(R.id.btn_category4));
        btnsCategory.add((Button) view.findViewById(R.id.btn_category5));
        btnsCategory.add((Button) view.findViewById(R.id.btn_category6));
        btnsCategory.add((Button) view.findViewById(R.id.btn_category7));
        btnsCategory.add((Button) view.findViewById(R.id.btn_category8));
        setButtonContent();

        edt_amount = (EditText)view.findViewById(R.id.edt_amount);
        edt_memo = (EditText)view.findViewById(R.id.edt_memo);
    }

    void setButtonContent()
    {
        for (int i = 0; i < 8; i++)
        {
            btnsCategory.get(i).setText(MainActivity.defaultCategory[i]);
        }
    }

    void setListeners()
    {
        btnPrev.setOnClickListener(new DateButtonClickListener());
        btnDate.setOnClickListener(new DateButtonClickListener());
        btnNext.setOnClickListener(new DateButtonClickListener());

        for (int i = 0; i < 8; i++) {
            btnsCategory.get(i).setOnClickListener(new CategoryButtonClickListener());
        }
    }

    class DateButtonClickListener implements View.OnClickListener {
        public void  onClick(View view) {
            String sourceDate = btnDate.getText().toString().substring(0, 10);
            //Log.d("sourceDate", sourceDate);
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
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
                    String str = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date)
                            + " [" + MainActivity.weekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
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
                    str = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date)
                            + " [" + MainActivity.weekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
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
                    break;
                case R.id.btn_category2:
                    selectedCategory = btnsCategory.get(1).getText().toString();
                    break;
                case R.id.btn_category3:
                    selectedCategory = btnsCategory.get(2).getText().toString();
                    break;
                case R.id.btn_category4:
                    selectedCategory = btnsCategory.get(3).getText().toString();
                    break;
                case R.id.btn_category5:
                    selectedCategory = btnsCategory.get(4).getText().toString();
                    break;
                case R.id.btn_category6:
                    selectedCategory = btnsCategory.get(5).getText().toString();
                    break;
                case R.id.btn_category7:
                    selectedCategory = btnsCategory.get(6).getText().toString();
                    break;
                case R.id.btn_category8:
                    selectedCategory = btnsCategory.get(7).getText().toString();
                    break;
                default:
                    break;
            }

            if (checkBeforeSave()) {
                saveItem();
                reset();
                ((MainActivity)getActivity()).getViewPager().setCurrentItem(1); // 1 = Fragment2
            }
        }
    }

    boolean checkBeforeSave()
    {
        if ("".equals(selectedCategory))
        {
            Toast.makeText(getActivity(), "Please select category.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if ("".equals(edt_amount.getText().toString()))
        {
            Toast.makeText(getActivity(), "Please enter amount.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    void saveItem()
    {
        DBAdapter dbAdapter = new DBAdapter(getActivity());

        String ym = btnDate.getText().toString().substring(0, 7);;
        String d = btnDate.getText().toString().substring(8, btnDate.getText().toString().indexOf(" "));
        Log.d("fragment1", "d = " + d + ", ym = " + ym);

        Item item = new Item(
                "",
                edt_amount.getText().toString(),
                selectedCategory,
                edt_memo.getText().toString(),
                d,
                ym,
                getTodaysDate().toString()
        );

        Log.d("stored item = ", edt_amount.getText().toString()
        + " " + selectedCategory
        + " " + edt_memo.getText().toString() + " " + d
        + " " + ym +" " + getTodaysDate().toString());

        dbAdapter.open();
        dbAdapter.saveItem(item);
        dbAdapter.close();
    }

    void showYMDPickerDialog()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker picker, int year, int month, int day){
                GregorianCalendar cal = new GregorianCalendar(year, month, day);
                Date date = cal.getTime();
                String str = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date)
                        + " [" + MainActivity.weekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
                btnDate.setText(str);
            }
        }, year, month-1, day);
        dialog.show();
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

    void reset()
    {
        edt_amount.setText("");
        edt_memo.setText("");
        btnDate.setText(getTodaysDate());

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
