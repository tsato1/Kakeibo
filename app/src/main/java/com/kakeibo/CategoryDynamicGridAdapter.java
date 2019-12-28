package com.kakeibo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.takahidesato.android.dynamicgrid.BaseDynamicGridAdapter;

public class CategoryDynamicGridAdapter extends BaseDynamicGridAdapter {

    public CategoryDynamicGridAdapter(Context context, List<?> items, int columnCount) {
        super(context, items, columnCount);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheeseViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_grid, null);
            holder = new CheeseViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CheeseViewHolder) convertView.getTag();
        }

        KkbCategory item = (KkbCategory) getItem(position);
        holder.build(item.getName(), item.getDrawable());
        return convertView;
    }

    private class CheeseViewHolder {
        private TextView titleText;
        private ImageView image;

        private CheeseViewHolder(View view) {
            titleText = view.findViewById(R.id.item_title);
            image = view.findViewById(R.id.item_img);
        }

        void build(String title, int drawable) {
            titleText.setText(title);
            image.setImageResource(drawable);
        }
    }
}