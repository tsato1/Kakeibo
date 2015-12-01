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

    public SearchListAdapter (Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        Item item = (Item) getItem(position);

        if (null == v) v = inflater.inflate(R.layout.dialog_row_search, null);

        TextView txvEventDate = (TextView) v.findViewById(R.id.txv_event_date);
        txvEventDate.setText(item.getEventYM() + "/" + item.getEventD());

        TextView txvCategory = (TextView) v.findViewById(R.id.txv_category);
        txvCategory.setText("Category:" + item.getCategory());

        TextView txvAmount = (TextView) v.findViewById(R.id.txv_amount);
        txvAmount.setText("Amount:" + item.getAmount());

        TextView txvMemo = (TextView) v.findViewById(R.id.txv_memo);
        SpannableString spannableString;
        if ("Income".equals(item.getCategory())) {
            String string = "Amount: " + "+" + item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.ColorBlue)), 8, 9, 0);
        } else {
            String string = "Amount: " + item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.ColorRed)), 8, 9, 0);
        }
        txvAmount.setText(spannableString);
        txvMemo.setText("Memo: " + item.getMemo());

        TextView txvUpdateDate = (TextView)v.findViewById(R.id.txv_update_date);
        txvUpdateDate.setText("Registered on " + item.getUpdateDate());

        return v;
    }
}
