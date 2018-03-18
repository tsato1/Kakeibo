package com.kakeibo;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by T on 2015/09/24.
 */
public class SearchListAdapter extends ArrayAdapter<Item> {
    private LayoutInflater inflater;
    private Context _context;
    private String[] defaultCategory;

    public SearchListAdapter (Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _context = context;

        defaultCategory = _context.getResources().getStringArray(R.array.defaultCategory);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        String amountColon = _context.getResources().getString(R.string.amount_colon);
        String memoColon = _context.getResources().getString(R.string.memo_colon);
        String categoryColon = _context.getResources().getString(R.string.category_colon);
        String savedOnColon = _context.getResources().getString(R.string.updated_on_colon);
        Item item = (Item) getItem(position);

        if (null == v) v = inflater.inflate(R.layout.dialog_row_search, null);

        TextView txvEventDate = (TextView) v.findViewById(R.id.txv_event_date);
        txvEventDate.setText(item.getEventDate());

        TextView txvCategory = (TextView) v.findViewById(R.id.txv_category);
        String categoryText = categoryColon + defaultCategory[item.getCategoryCode()];
        txvCategory.setText(categoryText);

        TextView txvAmount = (TextView) v.findViewById(R.id.txv_amount);
        String amountText = amountColon + item.getAmount();
        txvAmount.setText(amountText);

        TextView txvMemo = (TextView) v.findViewById(R.id.txv_memo);
        SpannableString span1, span2;
        if (0 == (item.getCategoryCode())) {
            span1 = new SpannableString(amountColon);
            span2 = new SpannableString("+" + item.getAmount());
            span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorBlue)), 0, 1, 0);
        } else {
            span1 = new SpannableString(amountColon);
            span2 = new SpannableString(item.getAmount());
            span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorRed)), 0, 1, 0);
        }
        txvAmount.setText(TextUtils.concat(span1, span2));
        String memoText = memoColon + item.getMemo();
        txvMemo.setText(memoText);

        TextView txvUpdateDate = (TextView)v.findViewById(R.id.txv_update_date);
        String updateDateText = savedOnColon + item.getUpdateDate();
        txvUpdateDate.setText(updateDateText);

        return v;
    }
}
