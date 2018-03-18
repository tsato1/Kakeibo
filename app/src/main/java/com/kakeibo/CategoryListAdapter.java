package com.kakeibo;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by T on 2015/10/08.
 */
public class CategoryListAdapter extends ArrayAdapter<Item> {
    private LayoutInflater layoutInflater_;
    private Context _context;
    private String[] defaultCategory;

    public CategoryListAdapter(Context context, int id, List<Item> objects) {
        super(context, id, objects);
        this._context = context;
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        defaultCategory = _context.getResources().getStringArray(R.array.defaultCategory);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = (Item)getItem(position);

        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.row_list_category, null);
        }

        ImageView imvCategory = (ImageView)convertView.findViewById(R.id.imv_category);
        TextView txvCategory = (TextView)convertView.findViewById(R.id.txv_category);

        int[] arrayMipmaps = _context.getResources().getIntArray(R.array.categoryMipmaps);
        imvCategory.setImageResource(arrayMipmaps[item.getCategoryCode()]);
        txvCategory.setText(defaultCategory[item.getCategoryCode()]);

        /*** amount ***/
        TextView txvAmount = (TextView)convertView.findViewById(R.id.txv_amount);
        SpannableString spannableString;
        if (Integer.parseInt(item.getAmount()) > 0) {
            String string = "+" + item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorBlue)), 0, 1, 0);
            txvAmount.setText(spannableString);
        } else {
            String string = item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorRed)), 0, 1, 0);
            txvAmount.setText(spannableString);
        }

        ImageView percentImageView;
        percentImageView = (ImageView) convertView.findViewById(R.id.imv_percent);
        if (item.getCategoryCode() == 0) {
            percentImageView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else {
            percentImageView.setBackgroundColor(Color.parseColor(MainActivity.categoryColor[position]));
        }

        TextView percentTextView;
        percentTextView = (TextView) convertView.findViewById(R.id.txv_percent);
        String percent = item.getMemo();
        if (percent.length() == 1) percent = "0" + percent;
        percentTextView.setText(percent + "%");

        return convertView;
    }
}
