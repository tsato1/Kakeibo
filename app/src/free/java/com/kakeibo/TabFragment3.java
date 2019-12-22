package com.kakeibo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kakeibo.db.ItemsDBAdapter;
import com.kakeibo.util.UtilKeyboard;
import com.kakeibo.util.UtilCurrency;
import com.kakeibo.util.UtilDate;
import com.kakeibo.util.UtilQuery;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class TabFragment3 extends Fragment implements RecyclerItemTouchHelperListener {
    private final static String TAG = TabFragment3.class.getSimpleName();

    private static String[] _searchCriteria;

    private Activity _activity;
    private Context _context;
    private View _view;
    private CoordinatorLayout _viewRoot;
    private TextView _txvSearchInstruction;
    private RecyclerView _rcvSearchCriteria;
    private SearchRecyclerViewAdapter _adpRecyclerView;
    private ArrayList<Card> _lstCards;     // for cards displayed
    private ArrayList<String> _lstChoices; // for choices shown in dialog upon tapping fab

    private static String _fromDate;
    private static String _toDate;

    public static TabFragment3 newInstance() {
        TabFragment3 tabFragment3 = new TabFragment3();
        Bundle args = new Bundle();
        tabFragment3.setArguments(args);
        return tabFragment3;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        _activity = getActivity();
        _context = getContext();

        _searchCriteria = getResources().getStringArray(R.array.search_criteria);

        findViews();
        setListeners();

        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        UtilKeyboard.hideSoftKeyboard(_activity);

        if (_lstCards.size() == 0) {
            _txvSearchInstruction.setVisibility(View.VISIBLE);
        } else {
            _txvSearchInstruction.setVisibility(View.INVISIBLE);
        }

        int indexAmountRangeCard = _lstCards.indexOf(new Card(Card.TYPE_AMOUNT_RANGE, 0));
        if (indexAmountRangeCard > -1) {
            RecyclerView.ViewHolder viewHolder = _rcvSearchCriteria.findViewHolderForAdapterPosition(indexAmountRangeCard);
            if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderAmountRange) {
                SearchRecyclerViewAdapter.ViewHolderAmountRange viewHolderAmountRange = (SearchRecyclerViewAdapter.ViewHolderAmountRange) viewHolder;
                viewHolderAmountRange.edtMin.setText("");
                viewHolderAmountRange.edtMax.setText("");
            }
        }

        _adpRecyclerView.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        UtilKeyboard.hideSoftKeyboard(_activity);
    }

    private void findViews() {
        _viewRoot = _view.findViewById(R.id.col_root_fragment3);
        _txvSearchInstruction = _view.findViewById(R.id.txv_inst_search);
        _rcvSearchCriteria = _view.findViewById(R.id.rcv_search_criteria);
    }

    private void setListeners() {
        _lstChoices = new ArrayList<>(Arrays.asList(_searchCriteria));
        _lstCards = new ArrayList<>();
        _adpRecyclerView = new SearchRecyclerViewAdapter(_context, _lstCards);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(_context);
        _rcvSearchCriteria.setLayoutManager(layoutManager);
        _rcvSearchCriteria.setItemAnimator(new DefaultItemAnimator());
        _rcvSearchCriteria.setAdapter(_adpRecyclerView);

        ItemTouchHelper.SimpleCallback ithCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(ithCallback).attachToRecyclerView(_rcvSearchCriteria);
    }

    /*** removes a selected choice from fab and add card(criterion) for display ***/
    private void addCriterion(int which) {
        String str = _lstChoices.remove(which);

        int selected = 0;
        for (int i = 0; i< _searchCriteria.length; i++) {
            if(_searchCriteria[i].equals(str)) selected = i;
        }

        Card card = new Card(selected, 0);
        _lstCards.add(card);
        _adpRecyclerView.notifyDataSetChanged();
        _txvSearchInstruction.setVisibility(View.INVISIBLE);
    }

    private boolean checkBeforeSearch() {
        if (_lstCards.size() == 0) {
            Toast.makeText(_activity, getResources().getString(R.string.err_no_search_criteria_found), Toast.LENGTH_SHORT).show();
            return false;
        }

        int indexDateRangeCard = _lstCards.indexOf(new Card(Card.TYPE_DATE_RANGE, 0));
        if (indexDateRangeCard > -1) {
            RecyclerView.ViewHolder viewHolder = _rcvSearchCriteria.findViewHolderForAdapterPosition(indexDateRangeCard);
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
                if (UtilDate.compareDate(fromDate, toDate, MainActivity.sDateFormat) == -1) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_from_date_older), Toast.LENGTH_SHORT).show();
                    return false;
                }

                _fromDate = UtilDate.convertDateFormat(fromDate, MainActivity.sDateFormat, 3);
                _toDate = UtilDate.convertDateFormat(toDate, MainActivity.sDateFormat, 3);
                UtilQuery.setDate(_fromDate, _toDate);
            }
        }

        int indexAmountRangeCard = _lstCards.indexOf(new Card(Card.TYPE_AMOUNT_RANGE, 0));
        if (indexAmountRangeCard > -1) {
            RecyclerView.ViewHolder viewHolder = _rcvSearchCriteria.findViewHolderForAdapterPosition(indexAmountRangeCard);
            if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderAmountRange) {
                SearchRecyclerViewAdapter.ViewHolderAmountRange viewHolderAmountRange = (SearchRecyclerViewAdapter.ViewHolderAmountRange) viewHolder;
                String min = viewHolderAmountRange.edtMin.getText().toString();
                String max = viewHolderAmountRange.edtMax.getText().toString();

                if ("".equals(min)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_please_enter_min_amount), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if ("".equals(max)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_please_enter_max_amount), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (/***"0".equals(min) ||***/ "0".equals(max) ||
                        /***"0.0".equals(min) ||***/ "0.0".equals(max) ||
                        /***"0.00".equals(min) ||***/ "0.00".equals(max) ||
                        /***"0.000".equals(min) ||***/ "0.000".equals(max)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_max_amount_cannot_be_0), Toast.LENGTH_SHORT).show();
                    return false;
                }

                BigDecimal bigMin = new BigDecimal(min);
                BigDecimal bigMax = new BigDecimal(max);

                if (bigMin.compareTo(bigMax) > 0) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_min_amount_greater), Toast.LENGTH_SHORT).show();
                    return false;
                }

                /*** using 3 (multiply by 1000) to compare with what's stored in db ***/
                UtilQuery.setAmount(UtilCurrency.getLongAmountFromBigDecimal(bigMin, 3),
                        UtilCurrency.getLongAmountFromBigDecimal(bigMax, 3));
            }
        }

        int indexCategoryCard = _lstCards.indexOf(new Card(Card.TYPE_CATEGORY, 0));
        if (indexCategoryCard > -1) {
            RecyclerView.ViewHolder viewHolder = _rcvSearchCriteria.findViewHolderForAdapterPosition(indexCategoryCard);
            if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderCategory) {
                SearchRecyclerViewAdapter.ViewHolderCategory viewHolderCategory = (SearchRecyclerViewAdapter.ViewHolderCategory) viewHolder;
                String category = viewHolderCategory.btnCategory.getText().toString();
                int categoryCode = viewHolderCategory.getSelectedCategoryCode();

                if ("".equals(category)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_please_select_category), Toast.LENGTH_SHORT).show();
                    return false;
                }

                UtilQuery.setCategoryCode(String.valueOf(categoryCode));
            }
        }

        int indexMemoCard = _lstCards.indexOf(new Card(Card.TYPE_MEMO, 0));
        if (indexMemoCard > -1) {
            RecyclerView.ViewHolder viewHolder = _rcvSearchCriteria.findViewHolderForAdapterPosition(indexMemoCard);
            if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderMemo) {
                SearchRecyclerViewAdapter.ViewHolderMemo viewHolderMemo = (SearchRecyclerViewAdapter.ViewHolderMemo) viewHolder;
                String memo = viewHolderMemo.edtMemo.getText().toString();

                if ("".equals(memo)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.err_memo_empty), Toast.LENGTH_SHORT).show();
                    return false;
                }

                UtilQuery.setMemo(memo);
            }
        }

        return true;
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        String name = "";

        if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderDateRange) {
            name = getResources().getString(R.string.date_range);
            _lstChoices.add(0, _searchCriteria[0]);
        } else if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderAmountRange) {
            name = getResources().getString(R.string.amount_range);
            if (_lstChoices.size() == 0) {
                _lstChoices.add(_searchCriteria[1]);
            } else {
                _lstChoices.add(1, _searchCriteria[1]);
            }
        } else if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderCategory) {
            name = getResources().getString(R.string.category);
            if (_lstChoices.size() <= 1) {
                _lstChoices.add(_searchCriteria[2]);
            } else {
                _lstChoices.add(2, _searchCriteria[2]);
            }
        } else if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderMemo) {
            name = getResources().getString(R.string.memo);
            if (_lstChoices.size() <= 2) {
                _lstChoices.add(_searchCriteria[3]);
            } else {
                _lstChoices.add(3, _searchCriteria[3]);
            }
        }

        Card cardItem = _lstCards.get(viewHolder.getAdapterPosition());
        int deleteIndex = viewHolder.getAdapterPosition();

        _adpRecyclerView.removeItem(deleteIndex);
        if (_lstCards.size() == 0) {
            _txvSearchInstruction.setVisibility(View.VISIBLE);
        }

        Snackbar snackbar = Snackbar.make(_viewRoot, name + getResources().getString(R.string.card_is_removed), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _adpRecyclerView.restoreItem(cardItem, deleteIndex);
                _txvSearchInstruction.setVisibility(View.INVISIBLE);
                for (int i = 0; i < _lstChoices.size(); i++) {
                    if (_lstChoices.get(i).equals(_searchCriteria[cardItem.type])) _lstChoices.remove(i);
                }
            }
        }).setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    void addCriteria() {
        String[] arrToDisplay = _lstChoices.toArray(new String[_lstChoices.size()]);

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
    }

    void doSearch() {
        Query query = new Query(Query.QUERY_TYPE_SEARCH);
        UtilQuery.init();

        if (checkBeforeSearch()) {
            UtilQuery.setCGroupBy(ItemsDBAdapter.COL_CATEGORY_CODE);
            UtilQuery.setCOrderBy(UtilQuery.SUM_AMOUNT, UtilQuery.DESC);
            UtilQuery.setCsWhere(ItemsDBAdapter.COL_CATEGORY_CODE);
            UtilQuery.setDOrderBy(ItemsDBAdapter.COL_EVENT_DATE, UtilQuery.ASC);
            query.setQueryC(UtilQuery.buildQueryC());
            query.setQueryCs(UtilQuery.buildQueryCs());
            query.setQueryD(UtilQuery.buildQueryD());

            ItemsDBAdapter itemsDbAdapter = new ItemsDBAdapter();
            itemsDbAdapter.open();
            Cursor c = itemsDbAdapter.getCountItemsByRawQuery(query.getQueryD());

            if (c.moveToNext()) {
                /*** if the query returns empty set, toast message ***/
                if (c.getInt(0)<=0) {
                    Toast.makeText(_activity, getString(R.string.msg_no_result_found), Toast.LENGTH_SHORT).show();
                }
                /*** if the query returns non-empty set, proceed ***/
                else {
                    ((MainActivity) _activity).getViewPager().setCurrentItem(1); // 1 = Fragment2
                    ((MainActivity) _activity).onSearch(query, _fromDate, _toDate);
                }
            } else {
                /*** if the query returns empty set, toast message ***/
                Toast.makeText(_activity, getString(R.string.msg_no_result_found), Toast.LENGTH_SHORT).show();
            }

            itemsDbAdapter.close();
        }
    }
}
