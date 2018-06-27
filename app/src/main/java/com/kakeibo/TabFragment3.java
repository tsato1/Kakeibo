package com.kakeibo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.kakeibo.db.ItemsDBAdapter;
import com.kakeibo.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class TabFragment3 extends Fragment {
    private ItemsDBAdapter itemsDbAdapter;
    private LinearLayout searchLayout;
    private ImageButton btnSearch;
    private EditText edtSearch;
    private View _view;
    private int mDateFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.tab_fragment_3, container, false);

//        loadSharedPreference();
//        findViews();

        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        loadSharedPreference();
//        findViews();
    }

    private void findViews() {
        searchLayout = _view.findViewById(R.id.lnl_search);
        //btnVoice = _view.findViewById(R.id.btn_voice_search);
        btnSearch = _view.findViewById(R.id.btn_search);
        edtSearch = _view.findViewById(R.id.edt_search);

        btnSearch.setOnClickListener(new TabFragment3.ButtonClickListener());

        itemsDbAdapter = new ItemsDBAdapter(getActivity());
    }

    public void loadSharedPreference() {
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_general, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String f = pref.getString(SettingsActivity.PREF_KEY_DATE_FORMAT, Utilities.DATE_FORMAT_YMD);
        mDateFormat = Integer.parseInt(f);
    }

    void searchItem() {
//        String searchItem = edtSearch.getText().toString();
//
//        if ("".equals(searchItem.trim())) {
//            Toast.makeText(getActivity(), getString(R.string.err_search_word_empty), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        List<Item> searchResultList = new ArrayList<>();
//
//        itemsDbAdapter.open();
//
//        String[] ym = btnDate.getText().toString().split("[/]");
//        String y, m;
//        switch (mDateFormat) {
//            case 1: // MDY
//            case 2: // DMY
//                y = ym[1];
//                m = ym[0];
//                break;
//            default:  // YMD
//                y = ym[0];
//                m = ym[1];
//        }
//
//        Cursor c = itemsDbAdapter.getAllItemsInMonth(y, m);
//
//        if (c.moveToFirst()) {
//            do {
//                if (c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)).contains(searchItem)) {
//                    Item item = new Item(
//                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_ID)),
//                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT)),
//                            c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY_CODE)),
//                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_MEMO)),
//                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_DATE)),
//                            c.getString(c.getColumnIndex(ItemsDBAdapter.COL_UPDATE_DATE))
//                    );
//
//                    searchResultList.add(item);
//                }
//            } while (c.moveToNext());
//        }
//
//        itemsDbAdapter.close();
//
//        SearchListAdapter searchListAdapter = new SearchListAdapter(getActivity(), 0, searchResultList);
//        ListView listView = new ListView(getActivity());
//        listView.setAdapter(searchListAdapter);
//        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//        dialog.setIcon(R.mipmap.ic_mikan);
//        dialog.setTitle(getString(R.string.title_search_result));
//        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
//        dialog.setView(listView).create();
//        dialog.show();
    }


    class ButtonClickListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_search:
                    searchItem();
                    break;
            }
        }
    }
}
