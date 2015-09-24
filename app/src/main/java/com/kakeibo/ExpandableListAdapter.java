package com.kakeibo;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
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

        ImageView categoryImageView = (ImageView)convertView.findViewById(R.id.img_category);
        TextView categoryTextView = (TextView)convertView.findViewById(R.id.txv_category);
        TextView amountTextView = (TextView)convertView.findViewById(R.id.txv_amount);

        Item item = (Item)baseItem;
        //todo image set SupportingItemListAdapter.setCategoryImage(this._context, item, categoryImageView);
        categoryTextView.setText(String.valueOf(item.getCategory()));
        amountTextView.setText(String.valueOf(item.getAmount()));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._childDataHashMap.get(this._dateHeaderList.get(groupPosition))
                .size();
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
