package com.kakeibo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilDrawing;

import java.util.List;

import javax.annotation.Nonnull;

public class CategoryListAdapter extends ArrayAdapter<KkbCategory> {
    private LayoutInflater inflater;
    private Context _context;

    public CategoryListAdapter(Context context, int resource, List<KkbCategory> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, @Nonnull ViewGroup parent) {
        KkbCategory kkbCategory = getItem(position);

        if (null == convertView) {
            convertView = inflater.inflate(R.layout.row_category_selection, null);
            final TextView txvCategory = convertView.findViewById(R.id.txv_category);
            final ImageView imvCategory = convertView.findViewById(R.id.imv_category);
            final ViewHolder viewHolder = new ViewHolder(imvCategory, txvCategory);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        String categoryText = UtilCategory.getCategoryStr(getContext(), kkbCategory.getCode());
        viewHolder.txvCategory.setText(categoryText);

        if (kkbCategory.getDrawable() == -1) { // ==-1: category is created by user -> use byte array
            viewHolder.imvCategory.setImageBitmap(UtilDrawing.bytesToBitmap(kkbCategory.getImage()));
        } else { // default category -> use drawable
            viewHolder.imvCategory.setImageDrawable(_context.getDrawable(kkbCategory.getDrawable()));
        }

        return convertView;
    }

    private class ViewHolder {
        private ImageView imvCategory;
        private TextView txvCategory;

        ViewHolder (ImageView imvCategory, TextView txvCategory) {
            this.imvCategory = imvCategory;
            this.txvCategory = txvCategory;
        }
    }
}
