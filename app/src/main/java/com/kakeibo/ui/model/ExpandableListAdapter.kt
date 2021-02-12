//package com.kakeibo.ui.model
//
//import android.content.Context
//import android.text.SpannableString
//import android.text.style.ForegroundColorSpan
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.BaseExpandableListAdapter
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.core.content.ContextCompat
//import com.kakeibo.R
//import com.kakeibo.SubApp
//import com.kakeibo.data.ItemStatus
//import com.kakeibo.util.UtilCategory
//import java.math.BigDecimal
//import java.util.*
//
///**
// * Created by T on 2015/09/16.
// */
//class ExpandableListAdapter(private val context: Context) : BaseExpandableListAdapter() {
//
//    companion object {
//        private val _dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format)
//    }
//
//    private lateinit var parents: List<String>
//    private lateinit var children: HashMap<String, List<ItemStatus>>
//
//    fun setParents(parents: List<String>) {
//        this.parents = parents
//        notifyDataSetChanged()
//    }
//
//    fun setChildren(children: HashMap<String, List<ItemStatus>>) {
//        this.children = children
//        notifyDataSetChanged()
//    }
//
//    override fun getChild(groupPosition: Int, childPosititon: Int): Any {
//        return children[parents[groupPosition]]!![childPosititon]
//    }
//
//    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
//        return childPosition.toLong()
//    }
//
//    override fun getChildView(
//            groupPosition: Int,
//            childPosition: Int,
//            isLastChild: Boolean,
//            convertView: View?,
//            parent: ViewGroup)
//    : View {
//        val item = getChild(groupPosition, childPosition) as ItemStatus
//
//        val view: View = if (convertView == null) {
//            val infalInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            infalInflater.inflate(R.layout.row_explist_child, parent, false)
//        } else {
//            convertView
//        }
//
//        val imvCategory = view.findViewById<ImageView>(R.id.img_category)
//        val txvCategory = view.findViewById<TextView>(R.id.txv_category)
//        val txvMemo = view.findViewById<TextView>(R.id.txv_memo)
//        val txvAmount = view.findViewById<TextView>(R.id.txv_amount)
//
//        /*** imv category  */
////todo should be disposable        childViewHolder.imvCategory.setImageResource(_trrMipmaps.getResourceId(item.getCategoryCode(), 0));
////        if (UtilCategory.getCategoryDrawable(_context, item.getCategoryCode()) == -1) { // ==-1: category is created by user -> use byte array
////            childViewHolder.imvCategory.setImageBitmap(
////                    UtilDrawing.bytesToBitmap(UtilCategory.getCategoryImage(_context, item.getCategoryCode()))
////            );
////        } else { // default category -> use drawable
////            childViewHolder.imvCategory.setImageDrawable(
////                    _context.getDrawable(UtilCategory.getCategoryDrawable(_context, item.getCategoryCode()))
////            );
////        }
////
//        /*** memo ***/
//        if (item.memo.length >= 15) {
//            txvMemo.text = item.memo.substring(0, 14) + "..."
//        }
//        else {
//            txvMemo.text = item.memo
//        }
//
//        /*** txv category ***/
////        childViewHolder.txvCategory.setText(UtilCategory.getCategoryStr(_context, item.getCategoryCode()));
//
//        /*** amount ***/
//        val spannableString: SpannableString = SpannableString("")
////        if (UtilCategory.getCategoryColor(context, item.categoryCode)== UtilCategory.CATEGORY_COLOR_INCOME) {
//////todo should be disposable        if (item.getCategoryCode() <= 0) {
////            String string = "+" + item.getAmount();
////            spannableString = new SpannableString(string);
////            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorBlue)), 0, 1, 0);
////        } else if (UtilCategory.getCategoryColor(_context, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_EXPENSE) {
////            String string = "-" + item.getAmount();
////            spannableString = new SpannableString(string);
////            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorRed)), 0, 1, 0);
////        }
////        childViewHolder.txvAmount.setText(spannableString);
//        return view
//    }
//
//    override fun getChildrenCount(groupPosition: Int): Int {
//        return children[parents[groupPosition]]!!.size
//    }
//
//    override fun getGroup(groupPosition: Int): Any {
//        return parents[groupPosition]
//    }
//
//    override fun getGroupCount(): Int {
//        return parents.size
//    }
//
//    override fun getGroupId(groupPosition: Int): Long {
//        return groupPosition.toLong()
//    }
//
//    override fun getGroupView(
//            groupPosition: Int,
//            isExpanded: Boolean,
//            convertView: View,
//            parent: ViewGroup
//    ): View {
//
//        var convertView = convertView
//        val headerTitle = getGroup(groupPosition) as String
//        val arrHeaderInfo = headerTitle.split("[,]").toTypedArray()
//        val headerYear = arrHeaderInfo[0]
//        val headerMonth = arrHeaderInfo[1]
//        val headerDay = arrHeaderInfo[2]
//        val headerBalance = arrHeaderInfo[3]
//        val headerDate: String
//        headerDate = when (_dateFormat) {
//            1 -> "$headerMonth/$headerDay/$headerYear"
//            2 -> "$headerDay/$headerMonth/$headerYear"
//            else -> "$headerYear/$headerMonth/$headerDay"
//        }
//        if (convertView == null) {
//            val infalInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            convertView = infalInflater.inflate(R.layout.row_explist_parent, parent, false)
//            val txvHeaderDate = convertView.findViewById<TextView>(R.id.txv_header_date)
//            val txvHeaderBalance = convertView.findViewById<TextView>(R.id.txv_header_balance)
////            val parentViewHolder: ParentViewHolder = ParentViewHolder(txvHeaderDate, txvHeaderBalance)
////            convertView.tag = parentViewHolder
//        }
////        val parentViewHolder = convertView.tag as ParentViewHolder
//
//        /*** header date  */
//        val cal = Calendar.getInstance()
//        cal[headerYear.toInt(), headerMonth.toInt() - 1] = headerDay.toInt()
//        val weekName = context.resources.getStringArray(R.array.week_name)
//        val str = headerDate + " [" + weekName[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
////        parentViewHolder.txvHeaderDate.text = str
//        /*** header balance  */
//        val spannableString: SpannableString
//        val tmp: String
//        val zero = BigDecimal(0)
//        if (BigDecimal(headerBalance).compareTo(zero) > 0) {
//            tmp = "+$headerBalance"
//            spannableString = SpannableString(tmp)
//            spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorBlue)), 0, 1, 0)
//        } else if (BigDecimal(headerBalance).compareTo(zero) < 0) {
//            tmp = headerBalance
//            spannableString = SpannableString(tmp)
//            spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorRed)), 0, 1, 0)
//        } else {
//            tmp = headerBalance
//            spannableString = SpannableString(tmp)
//            spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorBlack)), 0, 1, 0)
//        }
////        parentViewHolder.txvHeaderBalance.text = spannableString
//        return convertView
//    }
//
//    override fun hasStableIds(): Boolean {
//        return false
//    }
//
//    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
//        return true
//    }
//}