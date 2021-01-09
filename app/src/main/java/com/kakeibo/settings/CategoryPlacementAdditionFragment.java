package com.kakeibo.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kakeibo.data.CategoryStatus;
import com.kakeibo.R;
import com.kakeibo.SubApp;
import com.kakeibo.util.UtilCategory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CategoryPlacementAdditionFragment extends Fragment {
    public static final String TAG = CategoryPlacementAdditionFragment.class.getSimpleName();
    public static final int TAG_INT = 1;

    private static List<CategoryStatus> _nonDspCategoryList;
    private static HashSet<Integer> _selectedCategoryCodeSet;
    private static int _sNumColumns;

    private int _remainingCount; // remaining count for addition

    private Activity _activity;
    private GridView _grvCategory;
    private Button _btnBack, _btnNext;
    private TextView _txvTitle, _txvDescription;
    private RelativeLayout _rllBackground;

    public static CategoryPlacementAdditionFragment newInstance() {
        CategoryPlacementAdditionFragment fragment = new CategoryPlacementAdditionFragment();
        Bundle args = new Bundle();
        args.putString("key", TAG);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_category_placement, container, false);
        _activity = getActivity();

        /*** SharedPreference: num category icons per row ***/
        _sNumColumns = SubApp.getNumColumns(R.string.pref_key_num_columns);

        findViews(view);

        return view;
    }

    private void findViews(View view) {
        _nonDspCategoryList = null;//((SubApp) getContext()).getRepository().getNonDspCategories();
        _selectedCategoryCodeSet = new HashSet<>();

        _btnBack = view.findViewById(R.id.btn_back);
        _btnNext = view.findViewById(R.id.btn_next);
        _btnBack.setOnClickListener(new ItemClickListener());
        _btnNext.setOnClickListener(new ItemClickListener());
        _txvTitle = view.findViewById(R.id.txv_title);
        _txvTitle.setText(R.string.display_categories);
        _txvDescription = view.findViewById(R.id.txv_description);
        _txvDescription.setText(R.string.inst_tap_plus_to_add_criteria);
        _rllBackground = view.findViewById(R.id.rll_settings_category_placement);
        _rllBackground.setBackgroundColor(getResources().getColor(R.color.colorBackground));

//        final CategoryGridAdapter categoryGridAdapter = new CategoryGridAdapter(_activity, null); ///////////////////////
//        _grvCategory = view.findViewById(R.id.grv_category);
//        _grvCategory.setNumColumns(_sNumColumns);
//        _grvCategory.setAdapter(categoryGridAdapter);
//        _grvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ImageView imvCategoryOverlay = view.findViewById(R.id.imv_category_addition);
//                toggle(_nonDspKkbCategoryList.get(position).getCode(), imvCategoryOverlay);
//            }
//        });
    }

    private void toggle(Integer categoryCode, ImageView imv) {
        if (_selectedCategoryCodeSet.contains(categoryCode)) {
            _selectedCategoryCodeSet.remove(categoryCode);
            imv.setVisibility(View.GONE);
            _remainingCount++;
        } else {
            if (_remainingCount <= 0) {
                Toast.makeText(_activity,
                        getString(R.string.err_cannot_add_category) +
                        "(="+UtilCategory.NUM_MAX_DSP_CATEGORIES+")", Toast.LENGTH_LONG).show();
            } else {
                _selectedCategoryCodeSet.add(categoryCode);
                imv.setVisibility(View.VISIBLE);
                _remainingCount--;
            }
        }

        String tmp = getString(R.string.remaining_spots_colon) + _remainingCount;
        _txvDescription.setText(tmp);
    }

    /*** called from CategoryPlacementActivity ***/
    void setRemainingCount(int remainingCount) {
        Log.d("asdf","remaining  count = " + remainingCount);
        this._remainingCount = remainingCount - _selectedCategoryCodeSet.size();
        String tmp = getString(R.string.remaining_spots_colon) + _remainingCount;
        _txvDescription.setText(tmp);
    }

    class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back:
                    ((CategoryPlacementActivity) _activity).onBackPressed(TAG_INT);
                    break;
                case R.id.btn_next:
                    List<Integer> list = new ArrayList<>(_selectedCategoryCodeSet);
//                    for (Integer code: _selectedCategoryCodeSet) {
//                        Log.d("asdf", "ooo "+code);
//                    }
                    ((CategoryPlacementActivity) _activity).onNextPressed(TAG_INT, list);
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _activity.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            _activity.onBackPressed();
            Log.d(TAG, "Home button pressed");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
