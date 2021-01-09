package com.kakeibo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakeibo.data.ItemStatus;
import com.kakeibo.R;
import com.kakeibo.SubApp;
import com.kakeibo.db.ItemDBAdapter;
import com.kakeibo.export.ExportActivity;
import com.kakeibo.ui.items.ExpandableListAdapter;
import com.kakeibo.util.UtilCurrency;
import com.kakeibo.util.UtilDate;
import com.kakeibo.util.UtilKeyboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TabFragment2D extends Fragment {
    public final static String TAG = TabFragment2D.class.getSimpleName();

    private static final int MENU_ITEM_ID_DELETE = 0;
    private static final int MENU_ITEM_ID_EDIT = 1;

    private static Query _query;
    private static ItemLoadListener _itemLoadListener;

    private static String[] _weekNames;
    private static int _dateFormat;
    private static int _fractionDigits;
    private static int _numColumns;

    private Activity _activity;
    private View _view;
    private ItemDBAdapter _itemDbAdapter;
    private StringBuilder _stringBuilder;

    private List<String> lstDateHeader;
    private HashMap<String, List<ItemStatus>> hmpChildData;
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
        _weekNames = getResources().getStringArray(R.array.week_name);
        _dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format);
        _fractionDigits = SubApp.getFractionDigits(R.string.pref_key_fraction_digits);
        _numColumns = SubApp.getNumColumns(R.string.pref_key_num_columns);

        _view = inflater.inflate(R.layout.fragment_tab_2d, container, false);

        findViews();

        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            _query = bundle.getParcelable("query");
        }

        loadItemsOrderByDate(); /*** <- to handle come back from settings ***/
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        UtilKeyboard.hideKeyboard(_activity);
//    }

    void findViews(){
        explvData = _view.findViewById(R.id.lsv_expandable);

        explvData.setOnChildClickListener(new ChildClickListener());
        explvData.setOnCreateContextMenuListener(new ChildClickContextMenuListener());

        _stringBuilder = new StringBuilder();
        _itemDbAdapter = new ItemDBAdapter();
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
            ItemStatus item = (ItemStatus)child;

            LayoutInflater inflater = (LayoutInflater)_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.dialog_item_detail, view.findViewById(R.id.layout_root));

            TextView txvCategory = layout.findViewById(R.id.txv_detail_category);
            TextView txvAmount = layout.findViewById(R.id.txv_detail_amount);
            TextView txvMemo = layout.findViewById(R.id.txv_detail_memo);
            TextView txvRegistrationDate = layout.findViewById(R.id.txv_detail_registration);

//            String categoryText = getString(R.string.category_colon) + UtilCategory.getCategoryStr(getContext(), item.getCategoryCode());
            String categoryText = "";

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
                    UtilDate.getDateWithDayFromDBDate(item.getUpdateDate(), _weekNames, _dateFormat);
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
        final ItemStatus item = (ItemStatus) elaData.getChild(groupPosition, childPosition);

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
//                                _itemDbAdapter.open();
//                                final int itemId = item.getId();
//
//                                if(_itemDbAdapter.deleteItem(itemId)) {
//                                    Toast.makeText(_activity, getString(R.string.msg_item_successfully_deleted), Toast.LENGTH_SHORT).show();
//                                }
//
//                                loadItemsOrderByDate();
//                                _itemDbAdapter.close();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            case MENU_ITEM_ID_EDIT:
                LayoutInflater layoutInflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = layoutInflater.inflate(R.layout.dialog_item_edit, _view.findViewById(R.id.layout_root));

                /*** event date ***/
                Button btnEventDate = layout.findViewById(R.id.btn_event_date);
                String[] eventDates = item.getEventDate().split("-");
                GregorianCalendar cal = new GregorianCalendar(
                        Integer.parseInt(eventDates[0]),
                        Integer.parseInt(eventDates[1])-1,
                        Integer.parseInt(eventDates[2]));
                Date date = cal.getTime();
                String eventDate = new SimpleDateFormat(UtilDate.DATE_FORMATS[_dateFormat],
                        Locale.getDefault()).format(date)
                        + " [" + _weekNames[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
                btnEventDate.setText(eventDate);
                btnEventDate.setOnClickListener((View v1) -> {
                    String[] ymd = UtilDate.convertDateFormat(
                            eventDate.split("\\s+")[0], _dateFormat, 3).split("-");
                    int year = Integer.parseInt(ymd[0]), month=Integer.parseInt(ymd[1]), day=Integer.parseInt(ymd[2]);
                    DatePickerDialog dialog = new DatePickerDialog(_activity, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker picker, int year, int month, int day){
                            GregorianCalendar cal = new GregorianCalendar(year, month, day);
                            Date date = cal.getTime();
                            String str = new SimpleDateFormat(UtilDate.DATE_FORMATS[_dateFormat],
                                    Locale.getDefault()).format(date)
                                    + " [" + _weekNames[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
                            btnEventDate.setText(str);
                        }
                    }, year, month-1, day);
                    dialog.show();
                });
                /*** category ***/
//                Button btnCategory = layout.findViewById(R.id.btn_category);
//                String categoryText = UtilCategory.getCategoryStr(getContext(), item.getCategoryCode());
//                btnCategory.setText(categoryText);
//                btnCategory.setHint(""+item.getCategoryCode());
//                btnCategory.setOnClickListener((View v2) -> {
//                    /*** ordered by location ***/
//                    List<KkbCategory> kkbCategoriesList = UtilCategory.getDspKkbCategoryList(_activity);
//                    CategoryListAdapter adapter =
//                            new CategoryListAdapter(_activity, 0, kkbCategoriesList);
//                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(_activity);
//                    LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    View convertView = inflater.inflate(R.layout.dialog_bas_search_category, null);
//                    builder.setView(convertView);
//                    builder.setCancelable(true);
//                    builder.setIcon(R.mipmap.ic_mikan);
//                    builder.setTitle(R.string.category);
//                    ListView lv = convertView.findViewById(R.id.lsv_base_search_category);
//                    lv.setAdapter(adapter);
//                    Dialog dialog = builder.show();
//                    lv.setOnItemClickListener((AdapterView<?> parent, View v, int pos, long id) -> {
//                        int selectedCategoryCode = kkbCategoriesList.get(pos).getCode();
//                        btnCategory.setText(UtilCategory.getCategoryStr(_activity, selectedCategoryCode));
//                        btnCategory.setHint(""+selectedCategoryCode);
//                        dialog.dismiss();
//                    });
//                });
//                /*** amount***/
//                EditText edtAmount = layout.findViewById(R.id.edt_amount);
//                edtAmount.addTextChangedListener(new AmountTextWatcher(edtAmount));
//                edtAmount.setText(String.valueOf(item.getAmount()));
//                /*** memo ***/
//                EditText edtMemo = layout.findViewById(R.id.edt_memo);
//                edtMemo.setText(item.getMemo());
//
//                new AlertDialog.Builder(_activity)
//                        .setIcon(R.mipmap.ic_mikan)
//                        .setTitle(getString(R.string.edit_item))
//                        .setView(layout)
//                        .setPositiveButton(R.string.save, (DialogInterface d, int which)-> {
//                            _itemDbAdapter.open();
//                            final int itemId = item.getId();
//
//                            if (checkBeforeSave(edtAmount)) {
//                                if(_itemDbAdapter.deleteItem(itemId)) {
//                                    String amount = edtAmount.getText().toString();
//
//                                    Item tmp = new Item(
//                                            0,
//                                            new BigDecimal(amount),
//                                            _fractionDigits,
//                                            Integer.parseInt(btnCategory.getHint().toString()),
//                                            edtMemo.getText().toString(),
//                                            UtilDate.convertDateFormat(
//                                                    btnEventDate.getText().toString().split("\\s+")[0],
//                                                    _dateFormat, 3),
//                                            UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS)
//                                    );
//
//                                    _itemDbAdapter.saveItem(tmp);
//
//                                    Toast.makeText(_activity, getString(R.string.msg_change_successfully_saved), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            loadItemsOrderByDate();
//                            _itemDbAdapter.close();
//                        })
//                        .setNegativeButton(R.string.cancel, null)
//                        .setOnDismissListener((DialogInterface dialog)-> {
//                            loadItemsOrderByDate();
//                        })
//                        .show();
//                return true;
        }
        return super.onContextItemSelected(menuItem);
    }

    /*** same functionality is in TabFragment1 too ***/
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
        if (!UtilCurrency.checkAmount(edtAmount.getText().toString())) {
            Toast.makeText(getActivity(), R.string.err_amount_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void setQuery(Query query) {
        _query = query;
    }

    public void loadItemsOrderByDate() {
//        Log.d(TAG, "loadItemsOrderByDate() " + _query.getQueryD());
//
//        lstDateHeader.clear();
//        hmpChildData.clear();
//        Balance balance = Balance.newInstance(_fractionDigits);
//        int sameDateCounter = 0;
//        _stringBuilder.setLength(0);
//        _stringBuilder.append(getResources().getString(R.string.event_date));
//        _stringBuilder.append(",");
//        _stringBuilder.append(getResources().getString(R.string.amount));
//        _stringBuilder.append(",");
//        _stringBuilder.append(getResources().getString(R.string.category));
//        _stringBuilder.append(",");
//        _stringBuilder.append(getResources().getString(R.string.memo));
//        _stringBuilder.append(",");
//        _stringBuilder.append(getResources().getString(R.string.updated_date));
//        _stringBuilder.append("\n");

//        _itemDbAdapter.open();
//
//        Cursor c = _itemDbAdapter.getItemsByRawQuery(_query.getQueryD());
//
//        if (c!=null && c.moveToFirst()) {
//            String eventDate = c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE));
//            BigDecimal balanceDay = new BigDecimal(0)
//                    .setScale(_fractionDigits, RoundingMode.UNNECESSARY);;
//            List<Item> tmpItemList = new ArrayList<>();
//
//            do {
//                if (!c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE)).equals(eventDate)){ // if the event day of an item increases
//                    lstDateHeader.add(eventDate.replace('-', ',') + "," + balanceDay); // comma is deliminator
//                    hmpChildData.put(lstDateHeader.get(sameDateCounter), tmpItemList); // set the header of the old day
//                    balanceDay = BigDecimal.valueOf(0);
//                    /*** change of the date ***/
//                    eventDate = c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE)); // set a new date
//                    tmpItemList = new ArrayList<>(); // empty the array list of items
//                    sameDateCounter++;
//                }
//
//                Item item = new Item(
//                        c.getInt(c.getColumnIndex(ItemDBAdapter.COL_ID)),
//                        c.getLong(c.getColumnIndex(ItemDBAdapter.COL_AMOUNT)),
//                        "",
//                        _fractionDigits,
//                        c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)),
//                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_MEMO)),
//                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE)),
//                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_UPDATE_DATE))
//                );
//
//                if (UtilCategory.getCategoryColor(_activity, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_INCOME) {
////todo should be disposable //                if(c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)) == 0) {
//                    balance.addIncome(item.getAmount());
//                    balanceDay = balanceDay.add(item.getAmount());
//                } else if (UtilCategory.getCategoryColor(_activity, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_EXPENSE) {
//                    balance.addExpense(item.getAmount());
//                    balanceDay = balanceDay.subtract(item.getAmount());
//                }
//
//                _stringBuilder.append(item.getEventDate());
//                _stringBuilder.append(",");
//                _stringBuilder.append(item.getAmount());
//                _stringBuilder.append(",");
//                _stringBuilder.append(UtilCategory.getCategoryStr(getContext(), item.getCategoryCode()));
//                _stringBuilder.append(",");
//                _stringBuilder.append(item.getMemo());
//                _stringBuilder.append(",");
//                _stringBuilder.append(item.getUpdateDate());
//                _stringBuilder.append("\n");
//
//                tmpItemList.add(item);
//            } while (c.moveToNext());
//
//            lstDateHeader.add(eventDate.replace('-', ',') + "," + balanceDay); // set what to show on the header
//            hmpChildData.put(lstDateHeader.get(sameDateCounter), tmpItemList);
//        }
//
//        _itemDbAdapter.close();
//        elaData.notifyDataSetChanged();
//
//        UtilFiles.writeToFile(ExportActivity.FILE_ORDER_DATE, _stringBuilder.toString(),
//                _activity, Context.MODE_PRIVATE);
//        _stringBuilder.setLength(0);
//
//        _itemLoadListener.onItemsLoaded(balance);
    }

    void focusOnSavedItem(String eventDate) {
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
            return;
        }

        AlertDialog.Builder dialogExport = new AlertDialog.Builder(_activity);
        dialogExport.setIcon(R.mipmap.ic_mikan);
        dialogExport.setTitle(getString(R.string.export_date));
        dialogExport.setMessage(getString(R.string.quest_export_this_report_D));
        dialogExport.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(_activity, ExportActivity.class);
                intent.putExtra("REPORT_VIEW_TYPE", TabFragment2.REPORT_BY_DATE);
                startActivityForResult(intent, 10);
            }
        });
        dialogExport.show();
    }
}
