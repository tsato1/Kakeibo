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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kakeibo.CategoryGridAdapter;
import com.kakeibo.KkbCategory;
import com.kakeibo.R;
import com.kakeibo.util.UtilCategory;

import java.util.HashSet;
import java.util.List;

public class CategoryPlacementRemovalFragment extends Fragment {
    public static final String TAG = CategoryPlacementRemovalFragment.class.getSimpleName();

    private static List<KkbCategory> _kkbCategoryList;
    private static HashSet<Integer> _selectedCategorySet;

    private Activity _activity;
    private GridView _grvCategory;
    private Button _btnBack, _btnNext;
    private RelativeLayout _rllBackground;

    public static CategoryPlacementRemovalFragment newInstance() {
        CategoryPlacementRemovalFragment fragment = new CategoryPlacementRemovalFragment();
        Bundle args = new Bundle();
        args.putString("key", TAG);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_category_placement, container, false);
        _activity = getActivity();
        _kkbCategoryList = UtilCategory.getDspKkbCategoryList(_activity);

        findViews(view);

        return view;
    }

    private void findViews(View view) {
        _btnBack = view.findViewById(R.id.btn_back);
        _btnNext = view.findViewById(R.id.btn_next);
        _rllBackground = view.findViewById(R.id.rll_settings_category_placement);
        _rllBackground.setBackgroundColor(getResources().getColor(R.color.colorBackground_category_removal));

        _selectedCategorySet = new HashSet<>();

        final CategoryGridAdapter categoryGridAdapter = new CategoryGridAdapter(_activity, _kkbCategoryList);
        _grvCategory = view.findViewById(R.id.grv_category);
        _grvCategory.setNumColumns(CategoryPlacementActivity.sNumColumns);
        _grvCategory.setAdapter(categoryGridAdapter);
        _grvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView imvCategoryOverlay = view.findViewById(R.id.imv_category_removal);
                toggle(_kkbCategoryList.get(position).getCode(), imvCategoryOverlay);
            }
        });

        _btnBack.setOnClickListener(new ItemClickListener());
        _btnNext.setOnClickListener(new ItemClickListener());
    }

    private void toggle(int categoryCode, ImageView imv) {
        if (_selectedCategorySet.contains(categoryCode)) {
            _selectedCategorySet.remove(categoryCode);
            imv.setVisibility(View.GONE);
        } else {
            _selectedCategorySet.add(categoryCode);
            imv.setVisibility(View.VISIBLE);
        }
    }

    class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back:
                    ((CategoryPlacementActivity) _activity).onBackPressed(-1);
                    break;
                case R.id.btn_next:
                    ((CategoryPlacementActivity) _activity).onNextPressed(0);
                    break;
            }
        }
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
