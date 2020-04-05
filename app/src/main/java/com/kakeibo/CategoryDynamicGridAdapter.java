package com.kakeibo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.kakeibo.util.UtilDrawing;
import com.takahidesato.android.dynamicgrid.BaseDynamicGridAdapter;

public class CategoryDynamicGridAdapter extends BaseDynamicGridAdapter {
    private Context _context;

    public CategoryDynamicGridAdapter(Context context, List<?> items, int columnCount) {
        super(context, items, columnCount);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_grid, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        KkbCategory item = (KkbCategory) getItem(position);

        if (item.getDrawable() == -1) { // ==-1: category is created by user -> use byte array
            holder.build(item.getName(), item.getImage());
        } else { // default category -> use drawable
            holder.build(item.getName(), item.getDrawable());
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView titleText;
        private ImageView image;

        private ViewHolder(View view) {
            titleText = view.findViewById(R.id.item_title);
            image = view.findViewById(R.id.item_img);
        }

        void build(String title, int drawable) {
            titleText.setText(title);
            image.setImageResource(drawable);
        }

        void build(String title, byte[] img) {
            titleText.setText(title);
            image.setImageBitmap(UtilDrawing.bytesToBitmap(img));
        }
    }
}