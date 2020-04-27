package com.kakeibo;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.core.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilDrawing;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * Created by T on 2015/09/16.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _lstDateHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Item>> _hmpChildData;
//todo should be disposable    private TypedArray _trrMipmaps;

    ExpandableListAdapter(Context context, List<String> dateHeaderList, HashMap<String, List<Item>> childDataHashMap) {
        this._context = context;
        this._lstDateHeader = dateHeaderList;
        this._hmpChildData = childDataHashMap;

//todo should be disposable        _trrMipmaps = _context.getResources().obtainTypedArray(R.array.category_drawables);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._hmpChildData.get(this._lstDateHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Object baseItem = getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_explist_child, parent, false);
            final ImageView imvCategory = convertView.findViewById(R.id.img_category);
            final TextView txvCategory = convertView.findViewById(R.id.txv_category);
            final TextView txvMemo = convertView.findViewById(R.id.txv_memo);
            final TextView txvAmount = convertView.findViewById(R.id.txv_amount);
            final ChildViewHolder childViewHolder = new ChildViewHolder(imvCategory, txvCategory, txvMemo, txvAmount);
            convertView.setTag(childViewHolder);
        }

        final ChildViewHolder childViewHolder = (ChildViewHolder) convertView.getTag();

        Item item = (Item)baseItem;

        /*** imv category ***/
//todo should be disposable        childViewHolder.imvCategory.setImageResource(_trrMipmaps.getResourceId(item.getCategoryCode(), 0));
        if (UtilCategory.getCategoryDrawable(_context, item.getCategoryCode()) == -1) { // ==-1: category is created by user -> use byte array
            childViewHolder.imvCategory.setImageBitmap(
                    UtilDrawing.bytesToBitmap(UtilCategory.getCategoryImage(_context, item.getCategoryCode()))
            );
        } else { // default category -> use drawable
            childViewHolder.imvCategory.setImageDrawable(
                    _context.getDrawable(UtilCategory.getCategoryDrawable(_context, item.getCategoryCode()))
            );
        }

        /*** memo ***/
        if (item.getMemo().length() >= 15) {
            String str = item.getMemo().substring(0, 14) + "...";
            childViewHolder.txvMemo.setText(str);
        }
        else {
            childViewHolder.txvMemo.setText(item.getMemo());
        }

        /*** txv category ***/
        childViewHolder.txvCategory.setText(UtilCategory.getCategoryStr(_context, item.getCategoryCode()));

        /*** amount ***/
        SpannableString spannableString = new SpannableString("");
        if (UtilCategory.getCategoryColor(_context, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_INCOME) {
//todo should be disposable        if (item.getCategoryCode() <= 0) {
            String string = "+" + item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorBlue)), 0, 1, 0);
        } else if (UtilCategory.getCategoryColor(_context, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_EXPENSE) {
            String string = "-" + item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorRed)), 0, 1, 0);
        }
        childViewHolder.txvAmount.setText(spannableString);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._hmpChildData.get(this._lstDateHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._lstDateHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._lstDateHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        String[] arrHeaderInfo = headerTitle.split("[,]");
        String headerYear = arrHeaderInfo[0];
        String headerMonth = arrHeaderInfo[1];
        String headerDay = arrHeaderInfo[2];
        String headerBalance = arrHeaderInfo[3];

        String headerDate;
        switch (MainActivity.sDateFormat) {
            case 1: // MDY
                headerDate = headerMonth+"/"+headerDay+"/"+headerYear;
                break;
            case 2: // DMY
                headerDate = headerDay+"/"+headerMonth+"/"+headerYear;
                break;
            default: // YMD
                headerDate = headerYear+"/"+headerMonth+"/"+headerDay;
        }

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_explist_header, parent, false);
            final TextView txvHeaderDate = convertView.findViewById(R.id.txv_header_date);
            final TextView txvHeaderBalance = convertView.findViewById(R.id.txv_header_balance);
            final ParentViewHolder parentViewHolder = new ParentViewHolder(txvHeaderDate, txvHeaderBalance);
            convertView.setTag(parentViewHolder);
        }

        final ParentViewHolder parentViewHolder = (ParentViewHolder) convertView.getTag();

        /*** header date ***/
        Calendar cal = Calendar.getInstance();
        cal.set(parseInt(headerYear), parseInt(headerMonth)-1, parseInt(headerDay));
        String[] weekName = _context.getResources().getStringArray(R.array.week_name);

        String str = headerDate + " [" + weekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
        parentViewHolder.txvHeaderDate.setText(str);

        /*** header balance ***/
        SpannableString spannableString;
        String tmp;
        BigDecimal zero = new BigDecimal(0);
        if (new BigDecimal(headerBalance).compareTo(zero) > 0) {
            tmp = "+" + headerBalance;
            spannableString = new SpannableString(tmp);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorBlue)), 0, 1, 0);
        } else if (new BigDecimal(headerBalance).compareTo(zero) < 0){
            tmp = headerBalance;
            spannableString = new SpannableString(tmp);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorRed)), 0, 1, 0);
        } else {
            tmp = headerBalance;
            spannableString = new SpannableString(tmp);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.colorBlack)), 0, 1, 0);
        }
        parentViewHolder.txvHeaderBalance.setText(spannableString);

        return convertView;
    }

    private class ParentViewHolder {
        TextView txvHeaderDate;
        TextView txvHeaderBalance;

        ParentViewHolder (TextView txvHeaderDate, TextView txvHeaderBalance) {
            this.txvHeaderDate = txvHeaderDate;
            this.txvHeaderBalance = txvHeaderBalance;
        }
    }

    private class ChildViewHolder {
        ImageView imvCategory;
        TextView txvCategory;
        TextView txvMemo;
        TextView txvAmount;

        ChildViewHolder (ImageView imvCategory, TextView txvCategory, TextView txvMemo, TextView txvAmount) {
            this.imvCategory = imvCategory;
            this.txvCategory = txvCategory;
            this.txvMemo = txvMemo;
            this.txvAmount = txvAmount;
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
