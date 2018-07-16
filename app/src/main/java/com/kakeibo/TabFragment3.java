package com.kakeibo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.kakeibo.db.ItemsDBAdapter;
import com.kakeibo.settings.SettingsActivity;
import com.kakeibo.settings.UtilKeyboard;

import java.util.ArrayList;

public class TabFragment3 extends Fragment {
    private final static String TAG = TabFragment3.class.getSimpleName();

    private Activity _activity;
    private Context _context;
    private String[] weekName;
    private String[] searchCriteria;
    private RecyclerView rcvSearchCriteria;
    private SearchRecycleViewAdapter adpRecyclerView;
    private ArrayList<Card> lstCard;
    private FloatingActionButton fabSearch, fabAdd;
    private View _view;
    private int mDateFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        _activity = getActivity();
        _context = getContext();

        weekName = getResources().getStringArray(R.array.week_name);
        searchCriteria = getResources().getStringArray(R.array.search_criteria);

//        itemsDbAdapter = new ItemsDBAdapter(getActivity());
//
//        loadSharedPreference();
        findViews();
        setListeners();
//        reset();

        lstCard = new ArrayList<>();
        Card card = new Card(Card.TYPE_MEMO, R.drawable.ic_action_search);
        lstCard.add(card);
        rcvSearchCriteria.setLayoutManager(new LinearLayoutManager(_context));
        adpRecyclerView = new SearchRecycleViewAdapter(_context, lstCard);
        rcvSearchCriteria.setAdapter(adpRecyclerView);

        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        loadSharedPreference();
//        findViews();
//        setListeners();
//        reset();
    }

    private void loadSharedPreference() {
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_general, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String f = pref.getString(SettingsActivity.PREF_KEY_DATE_FORMAT, Util.DATE_FORMAT_YMD);
        mDateFormat = Integer.parseInt(f);
    }

    private void findViews() {
        rcvSearchCriteria = _view.findViewById(R.id.rcv_search_criteria);
        fabAdd = _view.findViewById(R.id.fab_add_criterion);
        fabSearch = _view.findViewById(R.id.fab_search);
//        btnSearch = _view.findViewById(R.id.fab_search);
//        btnFromDate = _view.findViewById(R.id.btn_from_date);
//        btnToDate = _view.findViewById(R.id.btn_to_date);
//        edtSearch = _view.findViewById(R.id.edt_memo_search);
    }

    private void setListeners() {
        fabAdd.setOnClickListener(new ButtonClickListener());
        fabSearch.setOnClickListener(new ButtonClickListener());
    }

    class ButtonClickListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fab_add_criterion:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getResources().getString(R.string.add_search_criterion));
                    builder.setIcon(R.mipmap.ic_mikan);
                    builder.setItems(searchCriteria, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            addCriterion(which);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                    break;
                case R.id.fab_search:
                    //searchItem();
                    break;
            }
        }
    }

    private void addCriterion(int which) {
        Card card;

        switch (which) {
            case Card.TYPE_DATE_RANGE:
                card = new Card(Card.TYPE_DATE_RANGE, R.drawable.ic_add_white);
                break;
            case Card.TYPE_AMOUNT_RANGE:
                card = new Card(Card.TYPE_AMOUNT_RANGE, R.drawable.ic_add_white);
                break;
            case Card.TYPE_CATEGORY:
                card = new Card(Card.TYPE_CATEGORY, R.drawable.ic_add_white);
                break;
            case Card.TYPE_MEMO:
                card = new Card(Card.TYPE_MEMO, R.drawable.ic_add_white);
                break;
            default:
                card = new Card(Card.TYPE_DATE_RANGE, R.drawable.ic_add_white);
                break;
        }

        lstCard.add(card);
        adpRecyclerView.notifyDataSetChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                //Log.d(TAG, "Not visible anymore.");
                UtilKeyboard.hideSoftKeyboard(_activity);
            }
        }
    }
}
