package com.kakeibo.ui.model

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.data.ItemStatus

/**
 * Created by T on 2015/09/24.
 */
class CategoryDetailListAdapter internal constructor(
        context: Context,
        resource: Int,
        objects: List<ItemStatus?>?
) : ArrayAdapter<ItemStatus?>(context, resource, objects!!) {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val _context: Context = context
    private val weekName: Array<String>
    private var mDateFormat = 0
    private var mPref: SharedPreferences? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val eventDateColon = _context.resources.getString(R.string.event_date_colon)
        val amountColon = _context.resources.getString(R.string.amount_colon)
        val memoColon = _context.resources.getString(R.string.memo_colon)
        val categoryColon = _context.resources.getString(R.string.category_colon)
        val savedOnColon = _context.resources.getString(R.string.updated_on_colon)
        val item = getItem(position)
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.row_dialog_category_detail_item, null)
            val txvEventDate = convertView.findViewById<TextView>(R.id.btn_event_date)
            val txvCategory = convertView.findViewById<TextView>(R.id.txv_category)
            val txvAmount = convertView.findViewById<TextView>(R.id.txv_amount)
            val txvMemo = convertView.findViewById<TextView>(R.id.txv_memo)
            val txvUpdateDate = convertView.findViewById<TextView>(R.id.txv_update_date)
            val viewHolder: ViewHolder = ViewHolder(txvEventDate, txvCategory, txvAmount, txvMemo, txvUpdateDate)
            convertView.tag = viewHolder
        }
        val viewHolder = convertView!!.tag as ViewHolder

//        String eventDateText = eventDateColon + UtilDate.getDateWithDayFromDBDate(item.getEventDate(), weekName, mDateFormat);
//        viewHolder.txvEventDate.setText(eventDateText);
//
//        String categoryText = categoryColon + UtilCategory.getCategoryStr(_context, item.getCategoryCode());
//        viewHolder.txvCategory.setText(categoryText);
//
//        String amountText = amountColon + item.getAmount();
//        viewHolder.txvAmount.setText(amountText);
//
//        SpannableString span1 = new SpannableString("");
//        SpannableString span2 = new SpannableString("");
//
//        if (UtilCategory.getCategoryColor(_context, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_INCOME) {
////todo 0 is not the only income        if (0 == (item.getCategoryCode())) {
//            span1 = new SpannableString(amountColon);
//            span2 = new SpannableString("+" + item.getAmount());
//            span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorBlue)), 0, 1, 0);
//        } else if (UtilCategory.getCategoryColor(_context, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_EXPENSE) {
//            span1 = new SpannableString(amountColon);
//            span2 = new SpannableString("-" + item.getAmount());
//            span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorRed)), 0, 1, 0);
//        }
//        viewHolder.txvAmount.setText(TextUtils.concat(span1, span2));
//
//        String memoText = memoColon + item.getMemo();
//        viewHolder.txvMemo.setText(memoText);
//
//        String updateDateText = savedOnColon + UtilDate.getDateWithDayFromDBDate(item.getUpdateDate(), weekName, mDateFormat);
//        viewHolder.txvUpdateDate.setText(updateDateText);
        return convertView
    }

    private inner class ViewHolder internal constructor(private val txvEventDate: TextView, private val txvCategory: TextView, private val txvAmount: TextView, private val txvMemo: TextView, private val txvUpdateDate: TextView)

    private fun loadSharedPreference() {
        mDateFormat = SubApp.getDateFormat(R.string.pref_key_date_format)
    }

    init {
        weekName = _context.resources.getStringArray(R.array.week_name)
        loadSharedPreference()
    }
}