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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;

public class CategoryPlacementAdditionFragment extends Fragment {
    public static final String TAG = CategoryPlacementAdditionFragment.class.getSimpleName();

    private static List<KkbCategory> _kkbCategoryList;
    private static HashSet<KkbCategory> _selectedCategorySet;

    private Activity _activity;
    private GridView _grvCategory;
    private Button _btnBack, _btnNext;
    private RelativeLayout _rllBackground;

    public static CategoryPlacementAdditionFragment newInstance() {
        CategoryPlacementAdditionFragment fragment = new CategoryPlacementAdditionFragment();
        Bundle args = new Bundle();
        args.putString("key", TAG);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@Nonnull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_category_placement, container, false);
        _activity = getActivity();
        _kkbCategoryList = UtilCategory.getNonDspKkbCategoryList(_activity);

        findViews(view);

        return view;
    }

    private void findViews(View view) {
        _btnBack = view.findViewById(R.id.btn_back);
        _btnNext = view.findViewById(R.id.btn_next);
        _rllBackground = view.findViewById(R.id.rll_settings_category_placement);
        _rllBackground.setBackgroundColor(getResources().getColor(R.color.colorBackground_category_addition));

        _selectedCategorySet = new HashSet<>();

        final CategoryGridAdapter categoryGridAdapter = new CategoryGridAdapter(_activity, _kkbCategoryList);
        _grvCategory = view.findViewById(R.id.grv_category);
        _grvCategory.setNumColumns(CategoryPlacementActivity.sNumColumns);
        _grvCategory.setAdapter(categoryGridAdapter);
        _grvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView imvCategoryOverlay = view.findViewById(R.id.imv_category_addition);
                toggle(_kkbCategoryList.get(position), imvCategoryOverlay);
            }
        });

        _btnBack.setOnClickListener(new CategoryPlacementAdditionFragment.ItemClickListener());
        _btnNext.setOnClickListener(new CategoryPlacementAdditionFragment.ItemClickListener());
    }

    private void toggle(KkbCategory category, ImageView imv) {
        if (_selectedCategorySet.contains(category)) {
            _selectedCategorySet.remove(category);
            imv.setVisibility(View.GONE);
        } else {
            _selectedCategorySet.add(category);
            imv.setVisibility(View.VISIBLE);
        }
    }

    class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back:
                    ((CategoryPlacementActivity) _activity).onBackPressed(0);
                    break;
                case R.id.btn_next:
                    List<KkbCategory> list = new ArrayList<>(_selectedCategorySet);
                    ((CategoryPlacementActivity) _activity).onNextPressed(1, list);
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
