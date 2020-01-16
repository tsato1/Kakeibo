package com.kakeibo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class CategoryGridAdapter extends BaseAdapter {
    private static final String TAG = CategoryGridAdapter.class.getSimpleName();

    private Context _context;
    private List<KkbCategory> _kkbCategoryList;

    public CategoryGridAdapter(Context context, List<KkbCategory> objects) {
        this._context = context;
        this._kkbCategoryList = objects;
    }

    public Object getItem(int position) {
        return _kkbCategoryList.get(position);
    }

    public int getCount() {
        return _kkbCategoryList.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final KkbCategory kkbCategory = _kkbCategoryList.get(position);

        if (convertView==null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(_context);
            convertView = layoutInflater.inflate(R.layout.f1_grid_cell_category, null);
            final RelativeLayout rllCategoryCell = convertView.findViewById(R.id.rll_category_cell);
            final ImageView imvCategory = convertView.findViewById(R.id.imv_category);
            final TextView txvCategory = convertView.findViewById(R.id.txv_category);
            final ViewHolder viewHolder = new ViewHolder(rllCategoryCell, imvCategory, txvCategory);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.imvCategory.setImageDrawable(_context.getDrawable(kkbCategory.getDrawable()));
        viewHolder.txvCategory.setText(kkbCategory.getName());

        viewHolder.rllCategoryCell.setOnClickListener((View v) -> {
            ((GridView) parent).performItemClick(v, position, 0);
        });

        return convertView;
    }

    private class ViewHolder {
        private RelativeLayout rllCategoryCell;
        private ImageView imvCategory;
        private TextView txvCategory;

        ViewHolder (RelativeLayout rllCategoryCell, ImageView imvCategory, TextView txvCategory) {
            this.rllCategoryCell = rllCategoryCell;
            this.imvCategory = imvCategory;
            this.txvCategory = txvCategory;
        }
    }
}
