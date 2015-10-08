package com.kakeibo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;

/**
 * Created by T on 2015/10/08.
 */
public class CategoryListAdapter extends ArrayAdapter<String> {
    private LayoutInflater layoutInflater_;

    public CategoryListAdapter(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String amount = (String)getItem(position);

        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.row_list_category, null);
        }

        Log.d("CategoryListAdapter", "position: " + String.valueOf(position));

        ImageView imvCategory = (ImageView)convertView.findViewById(R.id.imv_category);
        if (position == 0) {
            imvCategory.setImageResource(R.mipmap.ic_category_income);
        } else if (position == 1) {
            imvCategory.setImageResource(R.mipmap.ic_category_meal);
        } else if (position == 2) {
            imvCategory.setImageResource(R.mipmap.ic_category_util);
        } else if (position == 3) {
            imvCategory.setImageResource(R.mipmap.ic_category_health);
        } else if (position == 4) {
            imvCategory.setImageResource(R.mipmap.ic_category_edu);
        } else if (position == 5) {
            imvCategory.setImageResource(R.mipmap.ic_category_cloth);
        } else if (position == 6) {
            imvCategory.setImageResource(R.mipmap.ic_category_trans);
        } else if (position == 7) {
            imvCategory.setImageResource(R.mipmap.ic_category_other);
        }

        TextView textView = (TextView)convertView.findViewById(R.id.txv_category);
        textView.setText(amount);

        return convertView;
    }
}
