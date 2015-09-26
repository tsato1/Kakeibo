package com.kakeibo;

import android.content.Context;
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

        if (null == v) v = inflater.inflate(R.layout.row_list_search, null);

        TextView txvSearchResult = (TextView)v.findViewById(R.id.txv_search_result);
        txvSearchResult.setText(item.getMemo());

        return v;
    }
}
