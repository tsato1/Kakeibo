package com.kakeibo.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kakeibo.R;
import com.kakeibo.db.CategoryLanDBAdapter;
import com.kakeibo.db.TmpCategory;
import com.kakeibo.util.UtilKeyboard;
import com.kakeibo.util.UtilSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryCreationLanguageFragment extends Fragment {
    public final static String TAG = CategoryCreationLanguageFragment.class.getSimpleName();
    public static final int TAG_INT = 1;

    private static int _rowInsertIndex = 1;

    private Activity _activity;
    private ImageButton _imbRemove, _imbAdd;
    private Button _btnLanguage, _btnBack, _btnNext;
    private LinearLayout _lnlForm;
    private EditText _edtName;

    private List<String> _langList = new ArrayList<>();
    private Map<String, EditText> _langMap = new HashMap<>();
    private ArrayAdapter<String> _langAdapter;

    /***
     * this category is to be passed and eventually saved ***/
    private TmpCategory _tmpCategory;

    /***
     * assigned in newInstance()
     * -1 : when called from CategoryCreationActivity
     * otherwise : when called from CategoryEditionActivity ***/
    private static int _categoryCode;

    public static CategoryCreationLanguageFragment newInstance(int categoryCode) {
        CategoryCreationLanguageFragment fragment = new CategoryCreationLanguageFragment();
        Bundle args = new Bundle();
        args.putString("key", TAG);
        args.putInt("categoryCode", categoryCode);
        _categoryCode = categoryCode;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_category_creation_language, container, false);
        _activity = getActivity();

        findViews(view);

        /*** set data if the screen is navigated from CategoryEditionActivity ***/
//20        if (_categoryCode!=-1) _edtName.setText(UtilCategory.getCategoryStr(_activity, _categoryCode));

        /*** language setup ***/
        _btnLanguage.setText(UtilSystem.getCurrentLangCode(_activity));
        _langList = UtilSystem.getAllSupportedLanguages();
        _langAdapter = new ArrayAdapter<>(_activity, android.R.layout.simple_list_item_1, _langList);
        _langMap.put(_btnLanguage.getText().toString(), _edtName);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        _rowInsertIndex = 1;
    }

    private void findViews(View view) {
        _lnlForm = view.findViewById(R.id.lnl_form);
        _imbRemove = view.findViewById(R.id.imb_remove);
        _imbAdd = view.findViewById(R.id.imb_add);
        _btnLanguage = view.findViewById(R.id.btn_language);
        _btnBack = view.findViewById(R.id.btn_back);
        _btnNext = view.findViewById(R.id.btn_next);
        _edtName = view.findViewById(R.id.edt_name);
        _btnBack.setOnClickListener(new ButtonClickListener());
        _btnNext.setOnClickListener(new ButtonClickListener());
        _imbAdd.setOnClickListener(new ButtonClickListener());
        _imbRemove.setOnClickListener(new ButtonClickListener());
    }

    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imb_add:
                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(_activity);
                    dialog1.setIcon(R.mipmap.ic_mikan);
                    dialog1.setTitle(R.string.language);
                    dialog1.setAdapter(_langAdapter, (DialogInterface dialog, int position)-> {
                        LinearLayout row = (LinearLayout) LayoutInflater.from(_activity)
                                .inflate(R.layout.row_dialog_new_category_language, null);
                        Button btn = row.findViewById(R.id.btn_language);
                        ImageButton imb = row.findViewById(R.id.imb_remove);
                        EditText edt = row.findViewById(R.id.edt_name);

                        String lang = _langList.remove(position);
                        imb.setOnClickListener((View v) -> {
                            _langList.add(lang);
                            _lnlForm.removeView(row);
                            _langMap.remove(lang);
                            _rowInsertIndex--;
                            _langAdapter.notifyDataSetChanged();
                        });

                        btn.setText(lang);
                        _lnlForm.addView(row, _rowInsertIndex);
                        _langMap.put(lang, edt);
                        _rowInsertIndex++;
                        _langAdapter.notifyDataSetChanged();
                    });
                    dialog1.setNegativeButton(R.string.cancel, (DialogInterface d, int which) -> {
                    });
                    dialog1.show();
                    break;
                case R.id.btn_back:
                    UtilKeyboard.hideKeyboard(_activity);
                    ((CategoryCreationActivity) _activity).onBackPressed(TAG_INT);
                    break;
                case R.id.btn_next:
                    if (checkBeforeProceed()) {
                        UtilKeyboard.hideKeyboard(_activity);
                        ((CategoryCreationActivity) _activity).onNextPressed(TAG_INT, _tmpCategory);
                    }
                    break;
            }
        }
    }

    void setTmpCategory(TmpCategory tmpCategory) {
        _tmpCategory = tmpCategory;
    }

    private boolean checkBeforeProceed() {
        if (_edtName.getText().toString().trim().length() == 0) {
            Toast.makeText(_activity, R.string.err_please_enter_name, Toast.LENGTH_LONG).show();
            return false;
        }

        for (String key: _langMap.keySet()) {
            EditText edt = _langMap.get(key);

            if (edt == null) continue;

            if (edt.getText().toString().trim().equals("")) {
                Toast.makeText(_activity, R.string.err_please_enter_name + ": " +key, Toast.LENGTH_LONG).show();
                return false;
            }

            if (key.equals(CategoryLanDBAdapter.COL_ARA)) {
                _tmpCategory.ara = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_ENG)) {
                _tmpCategory.eng = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_SPA)) {
                _tmpCategory.spa = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_FRA)) {
                _tmpCategory.fra = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_HIN)) {
                _tmpCategory.hin = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_IND)) {
                _tmpCategory.ind = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_ITA)) {
                _tmpCategory.ita = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_JPN)) {
                _tmpCategory.jpn = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_KOR)) {
                _tmpCategory.kor = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_POL)) {
                _tmpCategory.pol = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_POR)) {
                _tmpCategory.por = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_RUS)) {
                _tmpCategory.rus = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_TUR)) {
                _tmpCategory.tur = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_VIE)) {
                _tmpCategory.vie = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_Hans)) {
                _tmpCategory.hans = edt.getText().toString();
            } else if (key.equals(CategoryLanDBAdapter.COL_Hant)) {
                _tmpCategory.hant = edt.getText().toString();
            }
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _activity.finish();
    }
}
