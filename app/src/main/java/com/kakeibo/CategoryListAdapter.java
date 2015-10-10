package com.kakeibo;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by T on 2015/10/08.
 */
public class CategoryListAdapter extends ArrayAdapter<Item> {
    private LayoutInflater layoutInflater_;
    private Context _context;

    public CategoryListAdapter(Context context, int id, List<Item> objects) {
        super(context, id, objects);
        this._context = context;
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = (Item)getItem(position);

        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.row_list_category, null);
        }

        ImageView imvCategory = (ImageView)convertView.findViewById(R.id.imv_category);
        TextView txvCategory = (TextView)convertView.findViewById(R.id.txv_category);
        if (item.getCategory().equals(MainActivity.defaultCategory[0])) {
            imvCategory.setImageResource(R.mipmap.ic_category_income);
            txvCategory.setText(MainActivity.defaultCategory[0]);
        } else if (item.getCategory().equals(MainActivity.defaultCategory[1])) {
            imvCategory.setImageResource(R.mipmap.ic_category_meal);
            txvCategory.setText(MainActivity.defaultCategory[1]);
        } else if (item.getCategory().equals(MainActivity.defaultCategory[2])) {
            imvCategory.setImageResource(R.mipmap.ic_category_util);
            txvCategory.setText(MainActivity.defaultCategory[2]);
        } else if (item.getCategory().equals(MainActivity.defaultCategory[3])) {
            imvCategory.setImageResource(R.mipmap.ic_category_health);
            txvCategory.setText(MainActivity.defaultCategory[3]);
        } else if (item.getCategory().equals(MainActivity.defaultCategory[4])) {
            imvCategory.setImageResource(R.mipmap.ic_category_edu);
            txvCategory.setText(MainActivity.defaultCategory[4]);
        } else if (item.getCategory().equals(MainActivity.defaultCategory[5])) {
            imvCategory.setImageResource(R.mipmap.ic_category_cloth);
            txvCategory.setText(MainActivity.defaultCategory[5]);
        } else if (item.getCategory().equals(MainActivity.defaultCategory[6])) {
            imvCategory.setImageResource(R.mipmap.ic_category_trans);
            txvCategory.setText(MainActivity.defaultCategory[6]);
        } else if (item.getCategory().equals(MainActivity.defaultCategory[7])) {
            imvCategory.setImageResource(R.mipmap.ic_category_other);
            txvCategory.setText(MainActivity.defaultCategory[7]);
        }

        /*** amount ***/
        TextView txvAmount = (TextView)convertView.findViewById(R.id.txv_amount);
        SpannableString spannableString;
        if (Integer.parseInt(item.getAmount()) > 0) {
            String string = "+" + item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.ColorBlue)), 0, 1, 0);
            txvAmount.setText(spannableString);
        } else {
            String string = item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.ColorRed)), 0, 1, 0);
            txvAmount.setText(spannableString);
        }

        return convertView;
    }
}
