package com.kakeibo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kakeibo.settings.SettingsActivity;
import com.kakeibo.settings.UtilKeyboard;

import java.util.ArrayList;
import java.util.Arrays;

public class TabFragment3 extends Fragment implements RecyclerItemTouchHelperListener {
    private final static String TAG = TabFragment3.class.getSimpleName();

    private static String[] weekName;
    private static String[] searchCriteria;

    private SharedPreferences mPref;
    private Activity _activity;
    private Context _context;
    private FrameLayout frlRoot;
    private RecyclerView rcvSearchCriteria;
    private SearchRecyclerViewAdapter adpRecyclerView;
    private ArrayList<Card> lstCards;     // for cards displayed
    private ArrayList<String> lstChoices; // for choices shown in dialog upon tapping fab
    private FloatingActionButton fabSearch, fabAdd;
    private View _view;
    private int mDateFormat;
    private Query _query;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        _activity = getActivity();
        _context = getContext();

        weekName = getResources().getStringArray(R.array.week_name);
        searchCriteria = getResources().getStringArray(R.array.search_criteria);

        findViews();
        setListeners();
        loadSharedPreferences();

        return _view;
    }

    private void findViews() {
        frlRoot = _view.findViewById(R.id.frl_root_fragment3);
        rcvSearchCriteria = _view.findViewById(R.id.rcv_search_criteria);
        fabAdd = _view.findViewById(R.id.fab_add_criterion);
        fabSearch = _view.findViewById(R.id.fab_search);
    }

    private void setListeners() {
        fabAdd.setOnClickListener(new ButtonClickListener());
        fabSearch.setOnClickListener(new ButtonClickListener());

        lstChoices = new ArrayList<>(Arrays.asList(searchCriteria));
        lstCards = new ArrayList<>();
        adpRecyclerView = new SearchRecyclerViewAdapter(_context, lstCards);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(_context);
        rcvSearchCriteria.setLayoutManager(layoutManager);
        rcvSearchCriteria.setItemAnimator(new DefaultItemAnimator());
        rcvSearchCriteria.setAdapter(adpRecyclerView);

        ItemTouchHelper.SimpleCallback ithCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(ithCallback).attachToRecyclerView(rcvSearchCriteria);
    }

    private void loadSharedPreferences() {
        PreferenceManager.setDefaultValues(_activity, R.xml.pref_general, false);
        mPref = PreferenceManager.getDefaultSharedPreferences(_activity);
        String f = mPref.getString(SettingsActivity.PREF_KEY_DATE_FORMAT, Util.DATE_FORMAT_YMD);
        mDateFormat = Integer.parseInt(f);

//        String json1 = mPref.getString(SettingsActivity.PREF_KEY_LIST_CARDS, "");
//        Type typeCards = new TypeToken<ArrayList<Card>>() {}.getType();
//        lstCards = new Gson().fromJson(json1, typeCards);
//
//        String json2 = mPref.getString(SettingsActivity.PREF_KEY_LIST_CHOICES, "");
//        Type typeChoices = new TypeToken<ArrayList<String>>() {}.getType();
//        lstChoices = new Gson().fromJson(json2, typeChoices);
//
//        if (lstCards == null) lstCards = new ArrayList<>();
//        if (lstChoices == null) lstChoices = new ArrayList<>(Arrays.asList(searchCriteria));

        //todo amount expense - (minus) remove

        //todo category picker for card -> db
    }

    class ButtonClickListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fab_add_criterion:
                    String[] arrToDisplay = lstChoices.toArray(new String[lstChoices.size()]);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getResources().getString(R.string.add_search_criterion));
                    builder.setIcon(R.mipmap.ic_mikan);
                    builder.setItems(arrToDisplay, new DialogInterface.OnClickListener() {
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
                    _query = new Query(Query.QUERY_TYPE_SEARCH);

                    if (checkBeforeSearch()) {
                        _query.buildQuery();
                        ((MainActivity)_activity).getViewPager().setCurrentItem(1); // 1 = Fragment2
                        ((MainActivity)_activity).onSearch(_query);
                    }
                    break;
            }
        }
    }

    /*** removes a selected choice from fab and add card(criterion) for display ***/
    private void addCriterion(int which) {
        String str = lstChoices.remove(which);

        int selected = 0;
        for (int i=0; i< searchCriteria.length;i++) {
            if(searchCriteria[i].equals(str)) selected = i;
        }

        Card card = new Card(selected, 0);
        lstCards.add(card);
        adpRecyclerView.notifyDataSetChanged();

        saveSharedPreferences();
    }

    private boolean checkBeforeSearch() {
        if (lstCards.size() == 0) {
            Toast.makeText(_activity, getResources().getString(R.string.err_no_search_criteria_found), Toast.LENGTH_SHORT).show();
            return false;
        }

        int indexDateRangeCard = lstCards.indexOf(new Card(Card.TYPE_DATE_RANGE, 0));
        if (indexDateRangeCard > -1) {
            RecyclerView.ViewHolder viewHolder = rcvSearchCriteria.findViewHolderForAdapterPosition(indexDateRangeCard);
            if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderDateRange) {
                SearchRecyclerViewAdapter.ViewHolderDateRange viewHolderDateRange = (SearchRecyclerViewAdapter.ViewHolderDateRange) viewHolder;
                String fromDate = viewHolderDateRange.btnFrom.getText().toString();
                String toDate = viewHolderDateRange.btnTo.getText().toString();

                if ("".equals(fromDate)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_please_choose_from_date), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if ("".equals(toDate)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_please_choose_to_date), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (Util.compareDate(fromDate, toDate, mDateFormat) == -1) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_from_date_older), Toast.LENGTH_SHORT).show();
                    return false;
                }

                _query.setValDate(fromDate, toDate, mDateFormat);
            }
        }

        int indexAmountRangeCard = lstCards.indexOf(new Card(Card.TYPE_AMOUNT_RANGE, 0));
        if (indexAmountRangeCard > -1) {
            RecyclerView.ViewHolder viewHolder = rcvSearchCriteria.findViewHolderForAdapterPosition(indexAmountRangeCard);
            if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderAmountRange) {
                SearchRecyclerViewAdapter.ViewHolderAmountRange viewHolderDateRange = (SearchRecyclerViewAdapter.ViewHolderAmountRange) viewHolder;
                String min = viewHolderDateRange.edtMin.getText().toString();
                String max = viewHolderDateRange.edtMax.getText().toString();

                if ("".equals(min)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_please_enter_min_amount), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if ("".equals(max)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_please_enter_max_amount), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (0 == Integer.parseInt(min) || 0 == Integer.parseInt(max)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_amount_cannot_be_0), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (Integer.parseInt(min) - Integer.parseInt(max) > 0) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_min_amount_greater), Toast.LENGTH_SHORT).show();
                    return false;
                }

                _query.setValAmount(min, max);
            }
        }

        int indexCategoryCard = lstCards.indexOf(new Card(Card.TYPE_CATEGORY, 0));
        if (indexCategoryCard > -1) {
            RecyclerView.ViewHolder viewHolder = rcvSearchCriteria.findViewHolderForAdapterPosition(indexCategoryCard);
            if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderCategory) {
                SearchRecyclerViewAdapter.ViewHolderCategory viewHolderCategory = (SearchRecyclerViewAdapter.ViewHolderCategory) viewHolder;
                String category = viewHolderCategory.btnCategory.getText().toString();

                if ("".equals(category)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_please_select_category), Toast.LENGTH_SHORT).show();
                    return false;
                }

                _query.setCategory(category);
            }
        }

        int indexMemoCard = lstCards.indexOf(new Card(Card.TYPE_MEMO, 0));
        if (indexMemoCard > -1) {
            RecyclerView.ViewHolder viewHolder = rcvSearchCriteria.findViewHolderForAdapterPosition(indexMemoCard);
            if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderMemo) {
                SearchRecyclerViewAdapter.ViewHolderMemo viewHolderMemo = (SearchRecyclerViewAdapter.ViewHolderMemo) viewHolder;
                String memo = viewHolderMemo.edtMemo.getText().toString();

                if ("".equals(memo)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_memo_empty), Toast.LENGTH_SHORT).show();
                    return false;
                }

                _query.setMemo(memo);
            }
        }

        _query.setListCards(lstCards);
        return true;
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        String name = "";

        if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderDateRange) {
            name = getResources().getString(R.string.date_range);
            lstChoices.add(0, searchCriteria[0]);
        } else if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderAmountRange) {
            name = getResources().getString(R.string.amount_range);
            if (lstChoices.size() == 0) {
                lstChoices.add(searchCriteria[1]);
            } else {
                lstChoices.add(1, searchCriteria[1]);
            }
        } else if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderCategory) {
            name = getResources().getString(R.string.category);
            if (lstChoices.size() <= 1) {
                lstChoices.add(searchCriteria[2]);
            } else {
                lstChoices.add(2, searchCriteria[2]);
            }
        } else if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderMemo) {
            name = getResources().getString(R.string.memo);
            if (lstChoices.size() <= 2) {
                lstChoices.add(searchCriteria[3]);
            } else {
                lstChoices.add(3, searchCriteria[3]);
            }
        }

        Card cardItem = lstCards.get(viewHolder.getAdapterPosition());
        int deleteIndex = viewHolder.getAdapterPosition();

        adpRecyclerView.removeItem(deleteIndex);

        Snackbar snackbar = Snackbar.make(frlRoot, name + getResources().getString(R.string.card_is_removed), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adpRecyclerView.restoreItem(cardItem, deleteIndex);
                for (int i = 0; i < lstChoices.size(); i++) {
                    if (lstChoices.get(i).equals(searchCriteria[cardItem.type])) lstChoices.remove(i);
                }
            }
        }).setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();

        saveSharedPreferences();
    }

    private void saveSharedPreferences() {
        SharedPreferences.Editor editor = mPref.edit();
        String json1 = new Gson().toJson(lstCards);
        String json2 = new Gson().toJson(lstChoices);
        editor.putString(SettingsActivity.PREF_KEY_LIST_CARDS, json1);
        editor.putString(SettingsActivity.PREF_KEY_LIST_CHOICES, json2);
        editor.apply();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (lstCards!=null && lstChoices!=null)

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                Log.d(TAG, "Not visible anymore.");
                UtilKeyboard.hideSoftKeyboard(_activity);
            }
        }
    }
}
