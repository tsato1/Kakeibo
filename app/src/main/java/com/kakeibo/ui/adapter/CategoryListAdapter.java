//package com.kakeibo.ui.categories;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.core.content.ContextCompat;
//
//import com.kakeibo.R;
//import com.kakeibo.data.CategoryStatus;
//import com.kakeibo.util.UtilCategory;
//import com.kakeibo.util.UtilDrawing;
//
//import java.util.List;
//
//import javax.annotation.Nonnull;
//
//public class CategoryListAdapter extends ArrayAdapter<CategoryStatus> {
//    private LayoutInflater inflater;
//    private Context _context;
//
//    public CategoryListAdapter(Context context, int resource, List<CategoryStatus> objects) {
//        super(context, resource, objects);
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        _context = context;
//    }
//
//    @Override
//    public View getView(int position, View convertView, @Nonnull ViewGroup parent) {
//        CategoryStatus categoryStatus = getItem(position);
//
//        if (null == convertView) {
//            convertView = inflater.inflate(R.layout.row_category_selection, null);
//            final TextView txvCategory = convertView.findViewById(R.id.txv_category);
//            final ImageView imvCategory = convertView.findViewById(R.id.imv_category);
//            final ViewHolder viewHolder = new ViewHolder(imvCategory, txvCategory);
//            convertView.setTag(viewHolder);
//        }
//
//        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//
////        String categoryText = UtilCategory.getCategoryStr(getContext(), categoryStatus.getCode());
////        viewHolder.txvCategory.setText(categoryText);
//
//        //should be disposable
////        if (categoryStatus.getDrawable() == -1) { // ==-1: category is created by user -> use byte array
////            viewHolder.imvCategory.setImageBitmap(
////                    UtilDrawing.bytesToBitmap(categoryStatus.getImage()));
////        } else { // default category -> use drawable
////            viewHolder.imvCategory.setImageDrawable(
////                    ContextCompat.getDrawable(_context, categoryStatus.getDrawable()));
////        }
//        if (categoryStatus.getCode() < UtilCategory.CUSTOM_CATEGORY_CODE_START) { // default category -> use drawable
////            viewHolder.build(categoryStatus.getName(),
////                    UtilDrawing.getDrawableIdFromIconName(getContext(), categoryStatus.getDrawable()));
//        } else { // category is created by user -> use byte array
////            viewHolder.build(categoryStatus.getName(), categoryStatus.getImage());
//        }
//
//        return convertView;
//    }
//
//    private class ViewHolder {
//        private ImageView imvCategory;
//        private TextView txvCategory;
//
//        ViewHolder (ImageView imvCategory, TextView txvCategory) {
//            this.imvCategory = imvCategory;
//            this.txvCategory = txvCategory;
//        }
//    }
//}
