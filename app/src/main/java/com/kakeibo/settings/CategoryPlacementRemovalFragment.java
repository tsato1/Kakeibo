package com.kakeibo.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kakeibo.CategoryGridAdapter;
import com.kakeibo.KkbApplication;
import com.kakeibo.KkbCategory;
import com.kakeibo.R;
import com.kakeibo.util.UtilCategory;

import java.util.ArrayList;
import java.util.List;

public class CategoryPlacementRemovalFragment extends Fragment {
    public static final String TAG = CategoryPlacementRemovalFragment.class.getSimpleName();
    public static final int TAG_INT = 0;

    private static ArrayList<Integer> _selectedCategoryCodeList;
    private static int _sNumColumns;

    private Activity _activity;
    private GridView _grvCategory;
    private Button _btnBack, _btnNext;
    private TextView _txvTitle;
    private RelativeLayout _rllBackground;

    public static CategoryPlacementRemovalFragment newInstance() {
        CategoryPlacementRemovalFragment fragment = new CategoryPlacementRemovalFragment();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_category_placement, container, false);
        _activity = getActivity();

        /*** SharedPreference: num category icons per row ***/
        _sNumColumns = KkbApplication.getNumColumns(R.string.pref_key_num_columns);

        _selectedCategoryCodeList = new ArrayList<>();

        /*** find views ***/
        _btnBack = view.findViewById(R.id.btn_back);
        _btnNext = view.findViewById(R.id.btn_next);
        _btnBack.setOnClickListener(new ItemClickListener());
        _btnNext.setOnClickListener(new ItemClickListener());

        _txvTitle = view.findViewById(R.id.txv_title);
        _txvTitle.setText(R.string.hide_categories);
        _rllBackground = view.findViewById(R.id.rll_settings_category_placement);
        _rllBackground.setBackgroundColor(getResources().getColor(R.color.colorBackground));

        List<KkbCategory> kkbDspCategoryList = UtilCategory.getDspKkbCategoryList(_activity);
        final CategoryGridAdapter categoryGridAdapter = new CategoryGridAdapter(_activity, kkbDspCategoryList);
        _grvCategory = view.findViewById(R.id.grv_category);
        _grvCategory.setNumColumns(_sNumColumns);
        _grvCategory.setAdapter(categoryGridAdapter);
        _grvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick() called: position="+position);
                ImageView imvCategoryOverlay = view.findViewById(R.id.imv_category_removal);
                toggle(kkbDspCategoryList.get(position).getCode(), imvCategoryOverlay);
            }
        });

        return view;
    }

    private void toggle(Integer categoryCode, ImageView imv) {
        if (_selectedCategoryCodeList.contains(categoryCode)) {
            Log.d(TAG, "removed! at position "+categoryCode);
            _selectedCategoryCodeList.remove(categoryCode);
            imv.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "add! at position "+categoryCode);
            _selectedCategoryCodeList.add(categoryCode);
            imv.setVisibility(View.VISIBLE);
        }
    }

    class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back:
                    ((CategoryPlacementActivity) _activity).onBackPressed(TAG_INT);
                    break;
                case R.id.btn_next:
                    ArrayList<Integer> list = new ArrayList<>(_selectedCategoryCodeList);
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
