package com.kakeibo;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
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

    public SearchListAdapter (Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _context = context;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        String amountColon = _context.getResources().getString(R.string.amount_colon);
        String memoColon = _context.getResources().getString(R.string.memo_colon);
        String categoryColon = _context.getResources().getString(R.string.category_colon);
        String savedOnColon = _context.getResources().getString(R.string.saved_on_colon);
        Item item = (Item) getItem(position);

        if (null == v) v = inflater.inflate(R.layout.dialog_row_search, null);

        TextView txvEventDate = (TextView) v.findViewById(R.id.txv_event_date);
        txvEventDate.setText(item.getEventYM() + "/" + item.getEventD());

        TextView txvCategory = (TextView) v.findViewById(R.id.txv_category);
        String categoryText = categoryColon + item.getCategory();
        txvCategory.setText(categoryText);

        TextView txvAmount = (TextView) v.findViewById(R.id.txv_amount);
        String amountText = amountColon + item.getAmount();
        txvAmount.setText(amountText);

        TextView txvMemo = (TextView) v.findViewById(R.id.txv_memo);
        SpannableString spannableString;
        if ("Income".equals(item.getCategory())) {
            String string = amountColon + "+" + item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorBlue)), 8, 9, 0);
        } else {
            String string = amountColon + item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorRed)), 8, 9, 0);
        }
        txvAmount.setText(spannableString);
        String memoText = memoColon + item.getMemo();
        txvMemo.setText(memoText);

        TextView txvUpdateDate = (TextView)v.findViewById(R.id.txv_update_date);
        String updateDateText = savedOnColon + item.getUpdateDate();
        txvUpdateDate.setText(updateDateText);

        return v;
    }
}
