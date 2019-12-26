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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kakeibo.CategoryDynamicGridAdapter;
import com.kakeibo.KkbCategory;
import com.kakeibo.R;
import com.kakeibo.util.UtilCategory;

import com.takahidesato.android.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryPlacementReorderFragment extends Fragment {
    public final static String TAG = CategoryPlacementReorderFragment.class.getSimpleName();

    private static List<KkbCategory> _kkbCategoryList;
    private static int sNumColumns;

    DynamicGridView _dgvCategory;

    private Activity _activity;
    private Button _btnBack, _btnNext;
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
        sNumColumns = CategoryPlacementActivity.sNumColumns;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_category_placement_reorder, container, false);
        _activity = getActivity();
        _kkbCategoryList = UtilCategory.getDspKkbCategoryList(_activity);

        findViews(view);

        _dgvCategory = view.findViewById(R.id.dynamic_grid);
        _dgvCategory.setAdapter(new CategoryDynamicGridAdapter(_activity,
                new ArrayList<>(Arrays.asList(Cheeses.sCheeseStrings)), sNumColumns));
        _dgvCategory.setNumColumns(sNumColumns);
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
                Toast.makeText(_activity, parent.getAdapter().getItem(position).toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void findViews(View view) {
        _btnBack = view.findViewById(R.id.btn_back);
        _btnNext = view.findViewById(R.id.btn_next);
        _btnNext.setText(getString(R.string.done));
        _btnBack.setOnClickListener(new CategoryPlacementReorderFragment.ItemClickListener());
        _btnNext.setOnClickListener(new CategoryPlacementReorderFragment.ItemClickListener());

        _rllBackground = view.findViewById(R.id.rll_settings_category_placement);
        _rllBackground.setBackgroundColor(getResources().getColor(R.color.colorBackground_category_reorder));
    }

    class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back:
                    ((CategoryPlacementActivity) _activity).onBackPressed(1);
                    break;
                case R.id.btn_next:
                    ((CategoryPlacementActivity) _activity).onNextPressed(2);
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
