package com.kakeibo.ui.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kakeibo.R
import com.kakeibo.data.ItemStatus

/**
 * Created by T on 2015/10/08.
 */
class ItemDetailListAdapter(private val _context: Context, id: Int, objects: List<ItemStatus?>?) : ArrayAdapter<ItemStatus?>(_context, id, objects!!) {
    private val _layoutInflater: LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val itemStatus = getItem(position)
        if (null == convertView) {
            convertView = _layoutInflater.inflate(R.layout.row_list_category, parent, false)
            val imvCategory = convertView.findViewById<ImageView>(R.id.imv_category)
            val txvCategory = convertView.findViewById<TextView>(R.id.txv_category)
            val txvAmount = convertView.findViewById<TextView>(R.id.txv_amount)
            val imvPercent = convertView.findViewById<ImageView>(R.id.imv_percent)
            val txvPercent = convertView.findViewById<TextView>(R.id.txv_percent)
            val viewHolder: ViewHolder = ViewHolder(imvCategory, txvCategory, txvAmount, imvPercent, txvPercent)
            convertView.tag = viewHolder
        }
        if (itemStatus == null) return convertView!!
        val viewHolder = convertView!!.tag as ViewHolder
        //
//        /*** category ***/
////todo should be disposable        viewHolder.imvCategory.setImageResource(_trrMipmaps.getResourceId(item.getCategoryCode(), 0));
//        if (UtilCategory.getCategoryDrawable(_context, itemStatus.getCategoryCode()) == -1) { // ==-1: category is created by user -> use byte array
//            viewHolder.imvCategory.setImageBitmap(
//                    UtilDrawing.bytesToBitmap(UtilCategory.getCategoryImage(_context, itemStatus.getCategoryCode()))
//            );
//        } else { // default category -> use drawable
//            viewHolder.imvCategory.setImageDrawable(
//                    _context.getDrawable(UtilCategory.getCategoryDrawable(_context, itemStatus.getCategoryCode()))
//            );
//        }
//
//        viewHolder.txvCategory.setText(UtilCategory.getCategoryStr(_context, itemStatus.getCategoryCode()));
//
//        /*** amount & percent ***/
//        SpannableString spannableString;
//        if (UtilCategory.getCategoryColor(_context, itemStatus.getCategoryCode())==UtilCategory.CATEGORY_COLOR_INCOME) {
////todo should be disposable        if (item.getCategoryCode() <= 0) {
//            String string = "+" + itemStatus.getAmount();
//            spannableString = new SpannableString(string);
//            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorBlue)), 0, 1, 0);
//            viewHolder.txvAmount.setText(spannableString);
//
//            viewHolder.imvPercent.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//        } else if (UtilCategory.getCategoryColor(_context, itemStatus.getCategoryCode())==UtilCategory.CATEGORY_COLOR_EXPENSE) {
//            String string = "-" + itemStatus.getAmount();
//            spannableString = new SpannableString(string);
//            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorRed)), 0, 1, 0);
//            viewHolder.txvAmount.setText(spannableString);
//
//            viewHolder.imvPercent.setBackgroundColor(Color.parseColor(Constants._categoryColor[position]));
//        }
//
//        String percent;
//        if (itemStatus.getMemo().length()==1) { // 5 -> 005
//            percent = "00"+itemStatus.getMemo()+"%";
//        } else if (itemStatus.getMemo().length()==2) { // 48 -> 048
//            percent = "0"+itemStatus.getMemo()+"%";
//        } else { // 100
//            percent = itemStatus.getMemo()+"%";
//        }
//        viewHolder.txvPercent.setText(percent);
        return convertView
    }

    private inner class ViewHolder internal constructor(var imvCategory: ImageView, var txvCategory: TextView, var txvAmount: TextView, var imvPercent: ImageView, var txvPercent: TextView)

    //todo should be disposable    private TypedArray _trrMipmaps;
    init {
        _layoutInflater = _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

//todo should be disposable        _trrMipmaps = _context.getResources().obtainTypedArray(R.array.category_drawables);
    }
}