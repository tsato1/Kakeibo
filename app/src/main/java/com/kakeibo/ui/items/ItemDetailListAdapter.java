//package com.kakeibo.ui.items;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.kakeibo.R;
//import com.kakeibo.data.ItemStatus;
//
//import java.util.List;
//
///**
// * Created by T on 2015/10/08.
// */
//public class ItemDetailListAdapter extends ArrayAdapter<ItemStatus> {
//    private LayoutInflater _layoutInflater;
//    private Context _context;
////todo should be disposable    private TypedArray _trrMipmaps;
//
//    public ItemDetailListAdapter(Context context, int id, List<ItemStatus> objects) {
//        super(context, id, objects);
//        this._context = context;
//        _layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
////todo should be disposable        _trrMipmaps = _context.getResources().obtainTypedArray(R.array.category_drawables);
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
//        ItemStatus itemStatus = getItem(position);
//
//        if (null == convertView) {
//            convertView = _layoutInflater.inflate(R.layout.row_list_category, parent, false);
//            final ImageView imvCategory = convertView.findViewById(R.id.imv_category);
//            final TextView txvCategory = convertView.findViewById(R.id.txv_category);
//            final TextView txvAmount = convertView.findViewById(R.id.txv_amount);
//            final ImageView imvPercent = convertView.findViewById(R.id.imv_percent);
//            final TextView txvPercent = convertView.findViewById(R.id.txv_percent);
//            final ViewHolder viewHolder = new ViewHolder(imvCategory, txvCategory, txvAmount, imvPercent, txvPercent);
//            convertView.setTag(viewHolder);
//        }
//
//        if (itemStatus == null) return convertView;
//
//        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
////
////        /*** category ***/
//////todo should be disposable        viewHolder.imvCategory.setImageResource(_trrMipmaps.getResourceId(item.getCategoryCode(), 0));
////        if (UtilCategory.getCategoryDrawable(_context, itemStatus.getCategoryCode()) == -1) { // ==-1: category is created by user -> use byte array
////            viewHolder.imvCategory.setImageBitmap(
////                    UtilDrawing.bytesToBitmap(UtilCategory.getCategoryImage(_context, itemStatus.getCategoryCode()))
////            );
////        } else { // default category -> use drawable
////            viewHolder.imvCategory.setImageDrawable(
////                    _context.getDrawable(UtilCategory.getCategoryDrawable(_context, itemStatus.getCategoryCode()))
////            );
////        }
////
////        viewHolder.txvCategory.setText(UtilCategory.getCategoryStr(_context, itemStatus.getCategoryCode()));
////
////        /*** amount & percent ***/
////        SpannableString spannableString;
////        if (UtilCategory.getCategoryColor(_context, itemStatus.getCategoryCode())==UtilCategory.CATEGORY_COLOR_INCOME) {
//////todo should be disposable        if (item.getCategoryCode() <= 0) {
////            String string = "+" + itemStatus.getAmount();
////            spannableString = new SpannableString(string);
////            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorBlue)), 0, 1, 0);
////            viewHolder.txvAmount.setText(spannableString);
////
////            viewHolder.imvPercent.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
////        } else if (UtilCategory.getCategoryColor(_context, itemStatus.getCategoryCode())==UtilCategory.CATEGORY_COLOR_EXPENSE) {
////            String string = "-" + itemStatus.getAmount();
////            spannableString = new SpannableString(string);
////            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorRed)), 0, 1, 0);
////            viewHolder.txvAmount.setText(spannableString);
////
////            viewHolder.imvPercent.setBackgroundColor(Color.parseColor(Constants._categoryColor[position]));
////        }
////
////        String percent;
////        if (itemStatus.getMemo().length()==1) { // 5 -> 005
////            percent = "00"+itemStatus.getMemo()+"%";
////        } else if (itemStatus.getMemo().length()==2) { // 48 -> 048
////            percent = "0"+itemStatus.getMemo()+"%";
////        } else { // 100
////            percent = itemStatus.getMemo()+"%";
////        }
////        viewHolder.txvPercent.setText(percent);
//
//        return convertView;
//    }
//
//    private class ViewHolder {
//        ImageView imvCategory;
//        TextView txvCategory;
//        TextView txvAmount;
//        ImageView imvPercent;
//        TextView txvPercent;
//
//        ViewHolder (ImageView imvCategory, TextView txvCategory, TextView txvAmount, ImageView imvPercent, TextView txvPercent) {
//            this.imvCategory = imvCategory;
//            this.txvCategory = txvCategory;
//            this.txvAmount = txvAmount;
//            this.imvPercent = imvPercent;
//            this.txvPercent = txvPercent;
//        }
//    }
//}
