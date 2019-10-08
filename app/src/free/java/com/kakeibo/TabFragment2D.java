package com.kakeibo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakeibo.db.ItemsDBAdapter;
import com.kakeibo.export.CreateFileInFolderActivity;
import com.kakeibo.util.UtilFiles;
import com.kakeibo.util.UtilDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabFragment2D extends Fragment {
    public final static String TAG = TabFragment2D.class.getSimpleName();

    private static final int MENU_ITEM_ID_DELETE = 0;
    private static final int MENU_ITEM_ID_EDIT = 1;

    private static Query _query;
    private static ItemLoadListener _itemLoadListener;

    private Activity _activity;
    private View _view;
    private ItemsDBAdapter _itemsDbAdapter;
    private StringBuilder _stringBuilder;

    private List<String> lstDateHeader;
    private HashMap<String, List<Item>> hmpChildData;
    private ExpandableListAdapter elaData;
    private ExpandableListView explvData;

    public static TabFragment2D newInstance(ItemLoadListener itemLoadListener, Query query) {
        TabFragment2D tabFragment2D = new TabFragment2D();
        Bundle args = new Bundle();
        args.putParcelable("query", query);
        _query = query;
        _itemLoadListener = itemLoadListener;
        tabFragment2D.setArguments(args);
        return tabFragment2D;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _activity = getActivity();
        _view = inflater.inflate(R.layout.tab_fragment_2d, container, false);

        findViews();

        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        loadItemsOrderByDate(); /*** <- to handle come back from settings ***/
    }

    void findViews(){
        explvData = _view.findViewById(R.id.lsv_expandable);

        explvData.setOnChildClickListener(new ChildClickListener());
        explvData.setOnCreateContextMenuListener(new ChildClickContextMenuListener());

        _stringBuilder = new StringBuilder();
        _itemsDbAdapter = new ItemsDBAdapter();
        lstDateHeader = new ArrayList<>();
        hmpChildData = new HashMap<>();
        elaData = new ExpandableListAdapter(_activity, lstDateHeader, hmpChildData);
        explvData.setAdapter(elaData);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ChildClickListener implements ExpandableListView.OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
            Object child = (elaData.getChild(groupPosition, childPosition));
            Item item = (Item)child;

            LayoutInflater inflater = (LayoutInflater)_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.dialog_item_detail, view.findViewById(R.id.layout_root));

            TextView txvCategory = layout.findViewById(R.id.txv_detail_category);
            TextView txvAmount = layout.findViewById(R.id.txv_detail_amount);
            TextView txvMemo = layout.findViewById(R.id.txv_detail_memo);
            TextView txvRegistrationDate = layout.findViewById(R.id.txv_detail_registration);

            String categoryText = getString(R.string.category_colon) + MainActivity.sCategories[item.getCategoryCode()];
            txvCategory.setText(categoryText);
            SpannableString span1, span2;
            if (item.getCategoryCode() <= 0) {
                span1 = new SpannableString(getString(R.string.amount_colon));
                span2 = new SpannableString("+" + item.getAmount());
                span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_activity, R.color.colorBlue)), 0, 1, 0);
            } else {
                span1 = new SpannableString(getString(R.string.amount_colon));
                span2 = new SpannableString("-" + item.getAmount());
                span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_activity, R.color.colorRed)), 0, 1, 0);
            }
            txvAmount.setText(TextUtils.concat(span1, span2));
            String memoText = getString(R.string.memo_colon) + item.getMemo();
            txvMemo.setText(memoText);
            String savedOnText = getString(R.string.updated_on_colon) +
                    UtilDate.getDateWithDayFromDBDate(item.getUpdateDate(), MainActivity.sWeekName, MainActivity.sDateFormat);
            txvRegistrationDate.setText(savedOnText);

            new AlertDialog.Builder(_activity)
                    .setIcon(R.mipmap.ic_mikan)
                    .setTitle(getResources().getString(R.string.item_detail))
                    .setView(layout)
                    .show();

            return false;
        }
    }

    class ChildClickContextMenuListener implements ExpandableListView.OnCreateContextMenuListener {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
            int type = ExpandableListView.getPackedPositionType(info.packedPosition);
            if(type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                menu.setHeaderIcon(R.mipmap.ic_mikan);
                menu.add(0, MENU_ITEM_ID_EDIT, 0, R.string.edit);
                menu.add(0, MENU_ITEM_ID_DELETE, 1, R.string.delete);
            }
        }
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuItem.getMenuInfo();
        final int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        final int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);
        final Item item = (Item) elaData.getChild(groupPosition, childPosition);

        switch(menuItem.getItemId()) {
            case MENU_ITEM_ID_DELETE:
                new AlertDialog.Builder(_activity)
                        .setIcon(R.mipmap.ic_mikan)
                        .setTitle(getString(R.string.quest_do_you_want_to_delete_item))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.d("groupPosition", String.valueOf(groupPosition));
                                //Log.d("childPosition", String.valueOf(childPosition));
                                _itemsDbAdapter.open();
                                final int itemId = Integer.parseInt(item.getId());

                                if(_itemsDbAdapter.deleteItem(itemId)) {
                                    Toast.makeText(_activity, getString(R.string.msg_item_successfully_deleted), Toast.LENGTH_SHORT).show();
                                }

                                loadItemsOrderByDate();
                                _itemsDbAdapter.close();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            case MENU_ITEM_ID_EDIT:
                LayoutInflater layoutInflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = layoutInflater.inflate(R.layout.dialog_item_edit, _view.findViewById(R.id.layout_root));
                TextView txvEventDate = layout.findViewById(R.id.txv_event_date);
                txvEventDate.setText(item.getEventDate());
                TextView txvCategory = layout.findViewById(R.id.txv_category);
                String categoryText = getString(R.string.category_colon) + MainActivity.sCategories[item.getCategoryCode()];
                txvCategory.setText(categoryText);
                EditText edtAmount = layout.findViewById(R.id.edt_amount);
                edtAmount.addTextChangedListener(new AmountTextWatcher(edtAmount));
                edtAmount.setText(String.valueOf(item.getAmount()));
                EditText edtMemo = layout.findViewById(R.id.edt_memo);
                edtMemo.setText(item.getMemo());

                new AlertDialog.Builder(_activity)
                        .setIcon(R.mipmap.ic_mikan)
                        .setTitle(getString(R.string.title_edit_item))
                        .setView(layout)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                _itemsDbAdapter.open();
                                final int itemId = Integer.parseInt(item.getId());

                                if (checkBeforeSave(edtAmount)) {
                                    if(_itemsDbAdapter.deleteItem(itemId)) {
                                        String amount = edtAmount.getText().toString();

                                        Item tmp = new Item(
                                                "",
                                                new BigDecimal(amount),
                                                MainActivity.sFractionDigits,
                                                item.getCategoryCode(),
                                                edtMemo.getText().toString(),
                                                item.getEventDate(),
                                                UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS)
                                        );

                                        _itemsDbAdapter.saveItem(tmp);

                                        Toast.makeText(_activity, getString(R.string.msg_change_successfully_saved), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                loadItemsOrderByDate();
                                _itemsDbAdapter.close();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
        }
        return super.onContextItemSelected(menuItem);
    }

    private boolean checkBeforeSave(EditText edtAmount) {
        if ("".equals(edtAmount.getText().toString())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.err_please_enter_amount), Toast.LENGTH_SHORT).show();
            return false;
        }
        if ("0".equals(edtAmount.getText().toString()) ||
                "0.0".equals(edtAmount.getText().toString()) ||
                "0.00".equals(edtAmount.getText().toString()) ||
                "0.000".equals(edtAmount.getText().toString())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.err_amount_cannot_be_0), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void setQuery(Query query) {
        _query = query;
    }

    public void loadItemsOrderByDate() {
        Log.d(TAG, "loadItemsOrderByDate() " + _query.getQueryD());

        lstDateHeader.clear();
        hmpChildData.clear();
        Balance balance = Balance.newInstance(MainActivity.sFractionDigits);
        int sameDateCounter = 0;
        _stringBuilder.setLength(0);
        _stringBuilder.append(getResources().getString(R.string.event_date));
        _stringBuilder.append(",");
        _stringBuilder.append(getResources().getString(R.string.amount));
        _stringBuilder.append(",");
        _stringBuilder.append(getResources().getString(R.string.category));
        _stringBuilder.append(",");
        _stringBuilder.append(getResources().getString(R.string.memo));
        _stringBuilder.append(",");
        _stringBuilder.append(getResources().getString(R.string.updated_date));
        _stringBuilder.append("\n");

        _itemsDbAdapter.open();

        Cursor c = _itemsDbAdapter.getItemsByRawQuery(_query.getQueryD());

        if (c!=null && c.moveToFirst()) {
            String eventDate = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE));
            BigDecimal balanceDay = new BigDecimal(0)
                    .setScale(MainActivity.sFractionDigits, RoundingMode.UNNECESSARY);;
            List<Item> tmpItemList = new ArrayList<>();

            do {
                //Log.d("item(memo)", c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)));

                if (!c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)).equals(eventDate)){ // if the event day of an item increases
                    lstDateHeader.add(eventDate.replace('-', ',') + "," + String.valueOf(balanceDay)); // comma is deliminator
                    hmpChildData.put(lstDateHeader.get(sameDateCounter), tmpItemList); // set the header of the old day
                    balanceDay = BigDecimal.valueOf(0);
                    /*** change of the date ***/
                    eventDate = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)); // set a new date
                    tmpItemList = new ArrayList<>(); // empty the array list of items
                    sameDateCounter++;
                }

                Item item = new Item(
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_ID)),
                        c.getLong(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
                        "",
                        MainActivity.sFractionDigits,
                        c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)),
                        c.getString(c.getColumnIndex(ItemsDBAdapter.COL_UPDATE_DATE))
                );

                if(c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)) == 0) {
                    balance.addIncome(item.getAmount());
                    balanceDay = balanceDay.add(item.getAmount());
                } else {
                    balance.addExpense(item.getAmount());
                    balanceDay = balanceDay.subtract(item.getAmount());
                }

                _stringBuilder.append(item.getEventDate());
                _stringBuilder.append(",");
                _stringBuilder.append(item.getAmount());
                _stringBuilder.append(",");
                _stringBuilder.append(MainActivity.sCategories[item.getCategoryCode()]);
                _stringBuilder.append(",");
                _stringBuilder.append(item.getMemo());
                _stringBuilder.append(",");
                _stringBuilder.append(item.getUpdateDate());
                _stringBuilder.append("\n");

                tmpItemList.add(item);
            } while (c.moveToNext());

            lstDateHeader.add(eventDate.replace('-', ',')
                    + "," + String.valueOf(balanceDay)); // set what to show on the header
            hmpChildData.put(lstDateHeader.get(sameDateCounter), tmpItemList);
        }

        _itemsDbAdapter.close();
        elaData.notifyDataSetChanged();

        UtilFiles.writeToFile(CreateFileInFolderActivity.FILE_ORDER_DATE, _stringBuilder.toString(),
                _activity, Context.MODE_PRIVATE);
        _stringBuilder.setLength(0);

        _itemLoadListener.onItemsLoaded(balance);
    }

    public void focusOnSavedItem(String eventDate) {
        Log.d(TAG, "focusOnSavedItem() " + eventDate);
        int m = Integer.parseInt(eventDate.split("-")[1]);
        int d = Integer.parseInt(eventDate.split("-")[2]);

        for (int i = 0; i < lstDateHeader.size(); i++) {
            String[] header = lstDateHeader.get(i).split("[,]"); // ex. "2018,04,30,-700"

            if (Integer.parseInt(header[1]) == m && Integer.parseInt(header[2]) == d) {
                explvData.expandGroup(i);
                explvData.smoothScrollToPositionFromTop(i, 0);
            } else {
                explvData.collapseGroup(i);
            }
        }
    }

    public void export() {
        Log.d(TAG, "export() called");

        if (lstDateHeader.size()==0 || hmpChildData.size()==0) {
            Toast.makeText(_activity, R.string.nothing_to_export, Toast.LENGTH_SHORT).show();
        }

        AlertDialog.Builder dialogExport = new AlertDialog.Builder(_activity);
        dialogExport.setIcon(R.mipmap.ic_mikan);
        dialogExport.setTitle(getString(R.string.export_date));
        dialogExport.setMessage(getString(R.string.quest_export_this_report_D));
        dialogExport.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(_activity, CreateFileInFolderActivity.class);
                intent.putExtra("REPORT_VIEW_TYPE", TabFragment2.REPORT_BY_DATE);
                startActivityForResult(intent, 10);
            }
        });
        dialogExport.show();
    }
}
