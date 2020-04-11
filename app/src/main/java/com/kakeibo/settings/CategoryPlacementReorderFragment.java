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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kakeibo.CategoryDynamicGridAdapter;
import com.kakeibo.KkbApplication;
import com.kakeibo.KkbCategory;
import com.kakeibo.R;

import com.kakeibo.util.UtilCategory;
import com.takahidesato.android.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.List;

public class CategoryPlacementReorderFragment extends Fragment {
    public final static String TAG = CategoryPlacementReorderFragment.class.getSimpleName();
    public final static int TAG_INT = 2;

    private static SettingsCategoryEventListener _sEventListener;

    private static List<KkbCategory> _newKkbCategoryList;
    private static List<KkbCategory> _addedKkbCategoryList;
    private static int _sNumColumns;

    DynamicGridView _dgvCategory;
    private CategoryDynamicGridAdapter _adpGridCategory;

    private Activity _activity;
    private Button _btnBack, _btnNext;
    private TextView _txvTitle, _txvDescription;
    private RelativeLayout _rllBackground;

    public static CategoryPlacementReorderFragment newInstance() {
        CategoryPlacementReorderFragment fragment = new CategoryPlacementReorderFragment();
        Bundle args = new Bundle();
        args.putString("key", TAG);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        _sEventListener = (SettingsCategoryEventListener) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_category_placement_reorder, container, false);
        _activity = getActivity();

        /*** SharedPreference: num category icons per row ***/
        _sNumColumns = KkbApplication.getNumColumns(R.string.pref_key_num_columns);

        findViews(view);

        return view;
    }

    private void findViews(View view) {
        _btnBack = view.findViewById(R.id.btn_back);
        _btnNext = view.findViewById(R.id.btn_next);
        _dgvCategory = view.findViewById(R.id.dynamic_grid);
        _btnNext.setText(getString(R.string.done));
        _btnBack.setOnClickListener(new CategoryPlacementReorderFragment.ItemClickListener());
        _btnNext.setOnClickListener(new CategoryPlacementReorderFragment.ItemClickListener());
        _txvTitle = view.findViewById(R.id.txv_title);
        _txvTitle.setText(R.string.reorder_categories_for_display);
        _txvDescription = view.findViewById(R.id.txv_description);
        _txvDescription.setText(R.string.inst_long_tap_to_move_icons);
        _rllBackground = view.findViewById(R.id.rll_settings_category_placement);
        _rllBackground.setBackgroundColor(getResources().getColor(R.color.colorBackground));

        _addedKkbCategoryList = new ArrayList<>();
        _newKkbCategoryList = new ArrayList<>();
    }

    void setItemsOnGrid(List<Integer> newList, List<Integer> addedList) {
        _addedKkbCategoryList = new ArrayList<>();
        _newKkbCategoryList = new ArrayList<>();

        for (Integer categoryCode: newList) {
            KkbCategory kkbCategory = new KkbCategory(
                    categoryCode,
                    UtilCategory.getCategoryStr(_activity, categoryCode),
                    0,
                    0,
                    UtilCategory.getCategoryDrawable(_activity, categoryCode),
                    UtilCategory.getCategoryImage(_activity, categoryCode),
                    0, 0, "",""
                    );
            if (addedList.contains(categoryCode)) _addedKkbCategoryList.add(kkbCategory);
            _newKkbCategoryList.add(kkbCategory);
        }

        _adpGridCategory = new CategoryDynamicGridAdapter(_activity, _newKkbCategoryList, _sNumColumns);
        _dgvCategory.setAdapter(_adpGridCategory);
        _dgvCategory.setNumColumns(_sNumColumns);
        _dgvCategory.setOnDropListener(new DynamicGridView.OnDropListener() {
            @Override
            public void onActionDrop()
            {
                _dgvCategory.stopEditMode();
            }
        });
        _dgvCategory.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
                Log.d(TAG, "drag started at position " + position);
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                Log.d(TAG, String.format("drag item position changed from %d to %d", oldPosition, newPosition));
            }
        });
        _dgvCategory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                _dgvCategory.startEditMode(position);
                return true;
            }
        });
        _dgvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, parent.getAdapter().getItem(position).toString());
                Toast.makeText(_activity, R.string.inst_keep_pressing_longer, Toast.LENGTH_LONG).show();
            }
        });
    }

    class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back:
                    _newKkbCategoryList.removeAll(_addedKkbCategoryList);
                    _adpGridCategory.notifyDataSetChanged();
                    Log.d("asdf","new list size="+_newKkbCategoryList.size());
                    _sEventListener.onBackPressed(TAG_INT);
                    break;
                case R.id.btn_next:
                    List<Object> list = _adpGridCategory.getItems();
                    List<Integer> out = new ArrayList<>();
                    for (Object item: list) {
                        KkbCategory category = (KkbCategory) item;
                        out.add(category.getCode());
                    }
                    _sEventListener.onNextPressed(TAG_INT, out);
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
