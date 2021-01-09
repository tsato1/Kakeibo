package com.kakeibo.ui.items;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kakeibo.R;
import com.kakeibo.data.ItemStatus;
import com.kakeibo.util.UtilDate;

import java.util.List;

/**
 * Created by T on 2015/09/24.
 */
public class CategoryDetailListAdapter extends ArrayAdapter<ItemStatus> {
    private LayoutInflater inflater;
    private Context _context;
    private String[] weekName;
    private int mDateFormat;
    private SharedPreferences mPref;

    CategoryDetailListAdapter(Context context, int resource, List<ItemStatus> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _context = context;

        weekName = _context.getResources().getStringArray(R.array.week_name);

        loadSharedPreference();
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        String eventDateColon = _context.getResources().getString(R.string.event_date_colon);
        String amountColon = _context.getResources().getString(R.string.amount_colon);
        String memoColon = _context.getResources().getString(R.string.memo_colon);
        String categoryColon = _context.getResources().getString(R.string.category_colon);
        String savedOnColon = _context.getResources().getString(R.string.updated_on_colon);
        ItemStatus item = getItem(position);

        if (null == convertView) {
            convertView = inflater.inflate(R.layout.row_dialog_category_detail_item, null);

            final TextView txvEventDate = convertView.findViewById(R.id.btn_event_date);
            final TextView txvCategory = convertView.findViewById(R.id.txv_category);
            final TextView txvAmount = convertView.findViewById(R.id.txv_amount);
            final TextView txvMemo = convertView.findViewById(R.id.txv_memo);
            final TextView txvUpdateDate = convertView.findViewById(R.id.txv_update_date);
            final ViewHolder viewHolder = new ViewHolder(txvEventDate, txvCategory, txvAmount, txvMemo, txvUpdateDate);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();

//        String eventDateText = eventDateColon + UtilDate.getDateWithDayFromDBDate(item.getEventDate(), weekName, mDateFormat);
//        viewHolder.txvEventDate.setText(eventDateText);
//
//        String categoryText = categoryColon + UtilCategory.getCategoryStr(_context, item.getCategoryCode());
//        viewHolder.txvCategory.setText(categoryText);
//
//        String amountText = amountColon + item.getAmount();
//        viewHolder.txvAmount.setText(amountText);
//
//        SpannableString span1 = new SpannableString("");
//        SpannableString span2 = new SpannableString("");
//
//        if (UtilCategory.getCategoryColor(_context, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_INCOME) {
////todo 0 is not the only income        if (0 == (item.getCategoryCode())) {
//            span1 = new SpannableString(amountColon);
//            span2 = new SpannableString("+" + item.getAmount());
//            span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorBlue)), 0, 1, 0);
//        } else if (UtilCategory.getCategoryColor(_context, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_EXPENSE) {
//            span1 = new SpannableString(amountColon);
//            span2 = new SpannableString("-" + item.getAmount());
//            span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorRed)), 0, 1, 0);
//        }
//        viewHolder.txvAmount.setText(TextUtils.concat(span1, span2));
//
//        String memoText = memoColon + item.getMemo();
//        viewHolder.txvMemo.setText(memoText);
//
//        String updateDateText = savedOnColon + UtilDate.getDateWithDayFromDBDate(item.getUpdateDate(), weekName, mDateFormat);
//        viewHolder.txvUpdateDate.setText(updateDateText);

        return convertView;
    }

    private class ViewHolder {
        private TextView txvEventDate;
        private TextView txvCategory;
        private TextView txvAmount;
        private TextView txvMemo;
        private TextView txvUpdateDate;

        ViewHolder (TextView txvEventDate, TextView txvCategory, TextView txvAmount, TextView txvMemo, TextView txvUpdateDate) {
            this.txvEventDate = txvEventDate;
            this.txvCategory = txvCategory;
            this.txvAmount = txvAmount;
            this.txvMemo = txvMemo;
            this.txvUpdateDate = txvUpdateDate;
        }
    }

    private void loadSharedPreference() {
        mPref = PreferenceManager.getDefaultSharedPreferences(_context);
        String f = mPref.getString(_context.getString(R.string.pref_key_date_format), UtilDate.DATE_FORMAT_YMD);
        mDateFormat = Integer.parseInt(f);
    }
}
