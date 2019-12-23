package com.kakeibo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilDate;

import java.util.List;

/**
 * Created by T on 2015/09/24.
 */
public class CategoryDetailListAdapter extends ArrayAdapter<Item> {
    private LayoutInflater inflater;
    private Context _context;
    private String[] weekName;
    private int mDateFormat;
    private SharedPreferences mPref;

    CategoryDetailListAdapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _context = context;

        weekName = _context.getResources().getStringArray(R.array.week_name);

        loadSharedPreference();
    }

    @Override
    @NonNull
    public View getView(int position, View v, @NonNull ViewGroup parent) {
        String eventDateColon = _context.getResources().getString(R.string.event_date_colon);
        String amountColon = _context.getResources().getString(R.string.amount_colon);
        String memoColon = _context.getResources().getString(R.string.memo_colon);
        String categoryColon = _context.getResources().getString(R.string.category_colon);
        String savedOnColon = _context.getResources().getString(R.string.updated_on_colon);
        Item item = getItem(position);

        if (null == v) v = inflater.inflate(R.layout.dialog_row_search, null);

        TextView txvEventDate = v.findViewById(R.id.btn_event_date);
        String eventDateText = eventDateColon + UtilDate.getDateWithDayFromDBDate(item.getEventDate(), weekName, mDateFormat);
        txvEventDate.setText(eventDateText);

        TextView txvCategory = v.findViewById(R.id.txv_category);
        String categoryText = categoryColon + UtilCategory.getCategory(_context, item.getCategoryCode());
        txvCategory.setText(categoryText);

        TextView txvAmount = v.findViewById(R.id.txv_amount);
        String amountText = amountColon + item.getAmount();
        txvAmount.setText(amountText);

        TextView txvMemo = v.findViewById(R.id.txv_memo);
        SpannableString span1, span2;
        if (0 == (item.getCategoryCode())) {
            span1 = new SpannableString(amountColon);
            span2 = new SpannableString("+" + item.getAmount());
            span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorBlue)), 0, 1, 0);
        } else {
            span1 = new SpannableString(amountColon);
            span2 = new SpannableString("-" + item.getAmount());
            span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorRed)), 0, 1, 0);
        }
        txvAmount.setText(TextUtils.concat(span1, span2));
        String memoText = memoColon + item.getMemo();
        txvMemo.setText(memoText);

        TextView txvUpdateDate = v.findViewById(R.id.txv_update_date);
        String updateDateText = savedOnColon + UtilDate.getDateWithDayFromDBDate(item.getUpdateDate(), weekName, mDateFormat);
        txvUpdateDate.setText(updateDateText);

        return v;
    }

    private void loadSharedPreference() {
        mPref = PreferenceManager.getDefaultSharedPreferences(_context);
        String f = mPref.getString(_context.getString(R.string.pref_key_date_format), UtilDate.DATE_FORMAT_YMD);
        mDateFormat = Integer.parseInt(f);
    }
}
