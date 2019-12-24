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
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kakeibo.CategoryGridAdapter;
import com.kakeibo.KkbCategory;
import com.kakeibo.R;
import com.kakeibo.util.UtilCategory;

import java.util.ArrayList;
import java.util.List;

public class CategoryPlacementRemovalFragment extends Fragment {
    public static final String TAG = CategoryPlacementRemovalFragment.class.getSimpleName();

    private static List<KkbCategory> _kkbCategoryList;
    private static List<Integer> _selectedCategoryCodeForRemoval;

    private Activity _activity;
    private GridView _grvCategory;
    private Button _btnBack, _btnNext;
    private RelativeLayout _rllBackground;

    private SettingsCategoryEventListener _eventListener;

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
        View view = inflater.inflate(R.layout.fragment_settings_category_placement, container, false);
        _activity = getActivity();
        _kkbCategoryList = UtilCategory.getDspKkbCategoryList(_activity);

        findViews(view);

        return view;
    }

    private void findViews(View view) {
        _grvCategory = view.findViewById(R.id.grv_category);
        _btnBack = view.findViewById(R.id.btn_back);
        _btnNext = view.findViewById(R.id.btn_next);
        _rllBackground = view.findViewById(R.id.rll_settings_category_placement);
        _rllBackground.setBackgroundColor(getResources().getColor(R.color.colorBackground_category_removal));

        _selectedCategoryCodeForRemoval = new ArrayList<>();

        final CategoryGridAdapter categoryGridAdapter = new CategoryGridAdapter(_activity, _kkbCategoryList);
        _grvCategory.setAdapter(categoryGridAdapter);
        _grvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _selectedCategoryCodeForRemoval.add(_kkbCategoryList.get(position).getCode());
//                _imvCategoryRemoval.setVisibility(View.VISIBLE);
            }
        });

        _btnBack.setOnClickListener(new ItemClickListener());
        _btnNext.setOnClickListener(new ItemClickListener());
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
