package com.kakeibo;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

/**
 * Created by T on 2015/09/16.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _dateHeaderList; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Item>> _childDataHashMap;

    public ExpandableListAdapter(Context context, List<String> dateHeaderList, HashMap<String, List<Item>> childDataHashMap) {
        this._context = context;
        this._dateHeaderList = dateHeaderList;
        this._childDataHashMap = childDataHashMap;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._childDataHashMap.get(this._dateHeaderList.get(groupPosition))
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
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_list_item, parent, false);
        }

        ImageView imvCategoryImage = (ImageView)convertView.findViewById(R.id.img_category);
        TextView txvCategory = (TextView)convertView.findViewById(R.id.txv_category);
        TextView txvMemo = (TextView)convertView.findViewById(R.id.txv_memo);
        TextView txvAmount = (TextView)convertView.findViewById(R.id.txv_amount);

        Item item = (Item)baseItem;

        /*** image ***/
        String[] category = MainActivity.defaultCategory;
        if(item.getCategory().equals(category[0])) {
            imvCategoryImage.setImageResource(R.mipmap.ic_category_income);
        } else if(item.getCategory().equals(category[1])) {
            imvCategoryImage.setImageResource(R.mipmap.ic_category_meal);
        } else if(item.getCategory().equals(category[2])) {
            imvCategoryImage.setImageResource(R.mipmap.ic_category_util);
        } else if(item.getCategory().equals(category[3])) {
            imvCategoryImage.setImageResource(R.mipmap.ic_category_health);
        } else if(item.getCategory().equals(category[4])) {
            imvCategoryImage.setImageResource(R.mipmap.ic_category_edu);
        } else if(item.getCategory().equals(category[5])) {
            imvCategoryImage.setImageResource(R.mipmap.ic_category_cloth);
        } else if(item.getCategory().equals(category[6])) {
            imvCategoryImage.setImageResource(R.mipmap.ic_category_trans);
        } else if(item.getCategory().equals(category[7])) {
            imvCategoryImage.setImageResource(R.mipmap.ic_category_other);
        }

        /*** memo ***/
        if (item.getMemo().length() >= 15) {
            txvMemo.setText(item.getMemo().substring(0, 15) + "...");
        }
        else {
            txvMemo.setText(item.getMemo());
        }

        /*** category ***/
        txvCategory.setText(item.getCategory());

        /*** amount ***/
        SpannableString spannableString;
        if ("Income".equals(item.getCategory())) {
            String string = "+" + item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.ColorBlue)), 0, 1, 0);
        } else {
            String string = "-" + item.getAmount();
            spannableString = new SpannableString(string);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_context, R.color.ColorRed)), 0, 1, 0);
        }
        txvAmount.setText(spannableString);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._childDataHashMap.get(this._dateHeaderList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._dateHeaderList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._dateHeaderList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        String headerDate = headerTitle.substring(0, headerTitle.indexOf(","));
        String headerBalance = headerTitle.substring(headerTitle.indexOf(",")+1);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_list_date, null);
        }

        TextView txvHeaderDate = (TextView) convertView.findViewById(R.id.txv_header_date);
        txvHeaderDate.setText(headerDate);

        TextView txvHeaderBalance = (TextView) convertView.findViewById(R.id.txv_header_balance);
        txvHeaderBalance.setText(headerBalance);
        if (Integer.parseInt(headerBalance) < 0) {
            txvHeaderBalance.setTextColor(ContextCompat.getColor(_context, R.color.ColorRed));
        }
        else if (Integer.parseInt(headerBalance) > 0) {
            txvHeaderBalance.setTextColor(ContextCompat.getColor(_context, R.color.ColorBlue));
            txvHeaderBalance.setText("+" + headerBalance);
        }
        else {
            txvHeaderBalance.setTextColor(ContextCompat.getColor(_context, R.color.ColorBlack));
        }

        return convertView;
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
