package com.kakeibo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.kakeibo.util.UtilCategory;

import java.util.List;

public class SearchCardCategoryListAdapter extends ArrayAdapter<KkbCategory> {
    private LayoutInflater inflater;
    private Context _context;

    SearchCardCategoryListAdapter(Context context, int resource, List<KkbCategory> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _context = context;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        KkbCategory kkbCategory = getItem(position);

        if (null == v) v = inflater.inflate(R.layout.dialog_row_search_category, null);

        TextView txvCategory = v.findViewById(R.id.txv_category);
        String categoryText = UtilCategory.getCategoryStrFromCode(getContext(), kkbCategory.getCode());
//                disposable !!!!!!!!!!!!!!!!!!!!!!!!!!! MainActivity.sCategories[kkbCategory.getCode()];
        txvCategory.setText(categoryText);

        ImageView imvCategory = v.findViewById(R.id.imv_category);
        imvCategory.setImageDrawable(ContextCompat.getDrawable(_context, kkbCategory.getDrawable()));

        return v;
    }
}
