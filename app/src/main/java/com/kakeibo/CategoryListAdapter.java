package com.kakeibo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
    private LayoutInflater _layoutInflater;
    private Context _context;
    private String[] _strsDefaultCategory;
    private TypedArray _trrMipmaps;

    public CategoryListAdapter(Context context, int id, List<Item> objects) {
        super(context, id, objects);
        this._context = context;
        _layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        _strsDefaultCategory = _context.getResources().getStringArray(R.array.default_category);
        _trrMipmaps = _context.getResources().obtainTypedArray(R.array.category_drawables);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Item item = getItem(position);

        if (null == convertView) {
            convertView = _layoutInflater.inflate(R.layout.row_list_category, parent, false);
        }

        if (item == null) return convertView;

        ImageView imvCategory = convertView.findViewById(R.id.imv_category);
        TextView txvCategory = convertView.findViewById(R.id.txv_category);

        imvCategory.setImageResource(_trrMipmaps.getResourceId(item.getCategoryCode(), 0));
        txvCategory.setText(_strsDefaultCategory[item.getCategoryCode()]);

        /*** amount ***/
        TextView txvAmount = convertView.findViewById(R.id.txv_amount);
        SpannableString spannableString;
        if (item.getCategoryCode() <= 0) {
            String string = "+" + item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorBlue)), 0, 1, 0);
            txvAmount.setText(spannableString);
        } else {
            String string = "-" + item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorRed)), 0, 1, 0);
            txvAmount.setText(spannableString);
        }

        ImageView percentImageView;
        percentImageView = convertView.findViewById(R.id.imv_percent);
        if (item.getCategoryCode() <= 0) {
            percentImageView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else {
            percentImageView.setBackgroundColor(Color.parseColor(MainActivity.categoryColor[position]));
        }

        TextView percentTextView;
        percentTextView = convertView.findViewById(R.id.txv_percent);
        String percent = item.getMemo();
        if (percent.length() == 1) percent = "0" + percent;
        percentTextView.setText(percent + "%");

        return convertView;
    }
}
