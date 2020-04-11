package com.kakeibo.settings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kakeibo.R;
import com.kakeibo.db.CategoryDBAdapter;
import com.kakeibo.db.TmpCategory;
import com.kakeibo.util.UtilCategory;

public class CategoryCreationColorFragment extends Fragment {
    public final static String TAG = CategoryCreationColorFragment.class.getSimpleName();
    public static final int TAG_INT = 0;

    private Activity _activity;
    private Button _btnIncome, _btnExpense, _btnBack, _btnNext;
    private ImageView _imvIncomeOverlay, _imvExpenseOverlay;
    private TextView _txvDescription;

    private static int _selectedColor = -1;

    /***
     * assigned in newInstance()
     * -1 : when called from CategoryCreationActivity
     * otherwise : when called from CategoryEditionActivity ***/
    private static int _categoryCode = -1;

    public static CategoryCreationColorFragment newInstance(int categoryCode) {
        CategoryCreationColorFragment fragment = new CategoryCreationColorFragment();
        Bundle args = new Bundle();
        args.putString("key", TAG);
        args.putInt("categoryCode", categoryCode);
        _categoryCode = categoryCode;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_category_creation_color, container, false);
        _activity = getActivity();

        findViews(view);

        /*** set data if the screen is navigated from CategoryEditionActivity ***/
        _selectedColor = _categoryCode==-1? -1: UtilCategory.getCategoryColor(_activity, _categoryCode);

        selectColor();

        return view;
    }

    private void findViews(View view) {
        _btnIncome = view.findViewById(R.id.btn_income);
        _btnExpense = view.findViewById(R.id.btn_expense);
        _btnBack = view.findViewById(R.id.btn_back);
        _btnNext = view.findViewById(R.id.btn_next);
        _imvIncomeOverlay = view.findViewById(R.id.imv_category_add_in);
        _imvExpenseOverlay = view.findViewById(R.id.imv_category_add_ex);
        _txvDescription = view.findViewById(R.id.txv_description);
        _btnIncome.setOnClickListener(new ButtonClickListener());
        _btnExpense.setOnClickListener(new ButtonClickListener());
        _btnBack.setOnClickListener(new ButtonClickListener());
        _btnNext.setOnClickListener(new ButtonClickListener());
        String str = getString(R.string.max_number_of_categories_colon) + UtilCategory.NUM_MAX_CUSTOM_CATEGORIES;
        _txvDescription.setText(str);
    }

    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_income:
                    _selectedColor = 0;
                    selectColor();
                    break;
                case R.id.btn_expense:
                    _selectedColor = 1;
                    selectColor();
                    break;
                case R.id.btn_back:
                    ((CategoryCreationActivity) _activity).onBackPressed(TAG_INT);
                    break;
                case R.id.btn_next:
                    if (_selectedColor == -1) {
                        Toast.makeText(_activity, getString(R.string.err_nothing_selected_select_one), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    /***
                     * this category is to be passed and eventually saved ***/
                    TmpCategory tmpCategory = new TmpCategory(-1, //code will be determined later
                            _selectedColor, -1, -1, null,
                            "","","","","","","","",
                            "","","","","","","","");
                    ((CategoryCreationActivity) _activity).onNextPressed(TAG_INT, tmpCategory);
                    break;
            }
        }
    }

    private void selectColor() {
        switch (_selectedColor) {
            case 0: // definition made in CategoryDBAdapter
                _imvIncomeOverlay.setVisibility(View.VISIBLE);
                _imvExpenseOverlay.setVisibility(View.INVISIBLE);
            break;
            case 1: // definition made in CategoryDBAdapter
                _imvIncomeOverlay.setVisibility(View.INVISIBLE);
                _imvExpenseOverlay.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _activity.finish();
    }
}
