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

//        disposable
//        if (item.getCategoryCode() == 0) {
//            imvCategory.setImageResource(R.mipmap.ic_category_income);
//            txvCategory.setText(defaultCategory[0]);
//        } else if (item.getCategoryCode().equals(defaultCategory[1])) {
//            imvCategory.setImageResource(R.mipmap.ic_category_comm);
//            txvCategory.setText(defaultCategory[1]);
//        } else if (item.getCategoryCode().equals(defaultCategory[2])) {
//            imvCategory.setImageResource(R.mipmap.ic_category_meal);
//            txvCategory.setText(defaultCategory[2]);
//        } else if (item.getCategory().equals(defaultCategory[3])) {
//            imvCategory.setImageResource(R.mipmap.ic_category_util);
//            txvCategory.setText(defaultCategory[3]);
//        } else if (item.getCategory().equals(defaultCategory[4])) {
//            imvCategory.setImageResource(R.mipmap.ic_category_health);
//            txvCategory.setText(defaultCategory[4]);
//        } else if (item.getCategory().equals(defaultCategory[5])) {
//            imvCategory.setImageResource(R.mipmap.ic_category_edu);
//            txvCategory.setText(defaultCategory[5]);
//        } else if (item.getCategory().equals(defaultCategory[6])) {
//            imvCategory.setImageResource(R.mipmap.ic_category_cloth);
//            txvCategory.setText(defaultCategory[6]);
//        } else if (item.getCategory().equals(defaultCategory[7])) {
//            imvCategory.setImageResource(R.mipmap.ic_category_trans);
//            txvCategory.setText(defaultCategory[7]);
//        } else if (item.getCategory().equals(defaultCategory[8])) {
//            imvCategory.setImageResource(R.mipmap.ic_category_ent);
//            txvCategory.setText(defaultCategory[8]);
//        } else if (item.getCategory().equals(defaultCategory[9])) {
//            imvCategory.setImageResource(R.mipmap.ic_category_ins);
//            txvCategory.setText(defaultCategory[9]);
//        } else if (item.getCategory().equals(defaultCategory[10])) {
//            imvCategory.setImageResource(R.mipmap.ic_category_tax);
//            txvCategory.setText(defaultCategory[10]);
//        } else if (item.getCategory().equals(defaultCategory[11])) {
//            imvCategory.setImageResource(R.mipmap.ic_category_other);
//            txvCategory.setText(defaultCategory[11]);
//        }

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
        percentImageView.setBackgroundColor(Color.parseColor(MainActivity.categoryColor[position]));

        TextView percentTextView;
        percentTextView = (TextView) convertView.findViewById(R.id.txv_percent);
        String percent = item.getMemo();
        if (percent.length() == 1) percent = "0" + percent;
        percentTextView.setText(percent + "%");

        return convertView;
    }
}
