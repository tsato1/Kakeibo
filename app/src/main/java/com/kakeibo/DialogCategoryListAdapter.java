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

import javax.annotation.Nonnull;

public class DialogCategoryListAdapter extends ArrayAdapter<KkbCategory> {
    private LayoutInflater inflater;
    private Context _context;

    DialogCategoryListAdapter(Context context, int resource, List<KkbCategory> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, @Nonnull ViewGroup parent) {
        KkbCategory kkbCategory = getItem(position);

        if (null == convertView) {
            convertView = inflater.inflate(R.layout.dialog_row_category, null);
            final TextView txvCategory = convertView.findViewById(R.id.txv_category);
            final ImageView imvCategory = convertView.findViewById(R.id.imv_category);
            final ViewHolder viewHolder = new ViewHolder(imvCategory, txvCategory);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        String categoryText = UtilCategory.getCategoryStr(getContext(), kkbCategory.getCode());
        viewHolder.txvCategory.setText(categoryText);

        viewHolder.imvCategory.setImageDrawable(ContextCompat.getDrawable(_context, kkbCategory.getDrawable()));

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
