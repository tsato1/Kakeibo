package com.kakeibo;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by T on 2015/09/14.
 */
public class TabFragment1 extends Fragment
{
    private Button btnDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        findViews(view);
        setListeners();
        setLabel();


        return view;
    }

    void findViews(View view)
    {
        btnDate = (Button)view.findViewById(R.id.btn_date);

    }

    void setListeners()
    {
        btnDate.setOnClickListener(new ButtonClickListener());
    }

    class ButtonClickListener implements View.OnClickListener
    {
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.btn_date:
                    showYMDPickerDialog();
                    break;
                default:
                    break;
            }
        }
    }

    void showYMDPickerDialog()
    {
        int calYear = 0;
        int calMonth = 0;
        int calDay = 0;

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker picker, int year, int month, int day){
                int calYear = year;
                int calMonth = month+1;
                int calDay = day;
                GregorianCalendar cal = new GregorianCalendar(year, month, day);
                Date date = cal.getTime();
                String str = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date)
                        + " [" + MainActivity.weekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
                btnDate.setText(str);
            }
        }, calYear, calMonth-1, calDay);
        dialog.show();
    }

    void setLabel()
    {
        Calendar cal = Calendar.getInstance();
        int calYear = cal.get(Calendar.YEAR);
        int calMonth = cal.get(Calendar.MONTH);
        int calDay = cal.get(Calendar.DAY_OF_MONTH);
        btnDate.setText(calYear+"/"+calMonth+"/"+calDay+" [" + MainActivity.weekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]");
    }

}
