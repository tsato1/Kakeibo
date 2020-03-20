//package com.kakeibo.settings;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//
//import com.kakeibo.KkbCategory;
//import com.kakeibo.R;
//
//import java.util.List;
//
//public class CategoryHorizontalScrollAdapter extends ArrayAdapter<KkbCategory> {
//    private List<KkbCategory> _kkbCategoryList;
//    private Context _context;
//    private LayoutInflater _inflater;
//
//    CategoryHorizontalScrollAdapter(Context context, int resource, List<KkbCategory> list) {
//        super(context, resource, list);
//        _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        _kkbCategoryList = list;
//        _context = context;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        KkbCategory kkbCategory = getItem(position);
//        ViewHolder viewHolder;
//
//        if (null == convertView) {
//            convertView = _inflater.inflate(R.layout.form_hsv_imv, null);
//            final ImageView icon = convertView.findViewById(R.id.imv_icon);
//            viewHolder = new ViewHolder(icon);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        if (kkbCategory != null) viewHolder.build(kkbCategory);
//
//        return convertView;
//    }
//
//    private static class ViewHolder {
//        private ImageView icon;
//
//        ViewHolder (ImageView icon) {
//            this.icon = icon;
//        }
//
//        void build(KkbCategory kkbCategory) {
//            icon.setImageResource(kkbCategory.getDrawable());
//        }
//    }
//}
