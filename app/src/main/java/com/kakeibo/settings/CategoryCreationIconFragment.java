package com.kakeibo.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.divyanshu.draw.widget.DrawView;
import com.kakeibo.R;
import com.kakeibo.db.TmpCategory;
import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilDrawing;

public class CategoryCreationIconFragment extends Fragment {
    public final static String TAG = CategoryCreationIconFragment.class.getSimpleName();
    public static final int TAG_INT = 2;

    private static int _selectedThickness = 1;

    private Activity _activity;
    private DrawView _drawView;
    private Button _btnBack, _btnDone;
    private ImageButton _imbClearCanvas, _imbUndo, _imbRedo, _imbThickness;

    private String[] _thicknessList;

    /***
     * this category is to be passed and eventually saved ***/
    private TmpCategory _tmpCategory;

    /***
     * assigned in newInstance()
     * -1 : when called from CategoryCreationActivity
     * otherwise : when called from CategoryEditionActivity ***/
    private static int _categoryCode = -1;

    public static CategoryCreationIconFragment newInstance(int categoryCode) {
        CategoryCreationIconFragment fragment = new CategoryCreationIconFragment();
        Bundle args = new Bundle();
        args.putString("key", TAG);
        args.putInt("categoryCode", categoryCode);
        _categoryCode = categoryCode;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_category_creation_icon, container, false);
        _activity = getActivity();

        findViews(view);

        /*** draw view setup ***/
        _drawView.setColor(getResources().getColor(R.color.colorBackground));
        _drawView.setStrokeWidth(getResources().getDimension(R.dimen.draw_view_thickness_medium));
        _thicknessList = getResources().getStringArray(R.array.thickness);

        return view;
    }

    private void findViews(View view) {
        _btnBack = view.findViewById(R.id.btn_back);
        _btnDone = view.findViewById(R.id.btn_done);
        _imbClearCanvas = view.findViewById(R.id.imb_clear_canvas);
        _imbUndo = view.findViewById(R.id.imb_undo);
        _imbRedo = view.findViewById(R.id.imb_redo);
        _imbThickness = view.findViewById(R.id.imb_thickness);
        _drawView = view.findViewById(R.id.draw_view);

        _btnBack.setOnClickListener(new ButtonClickListener());
        _btnDone.setOnClickListener(new ButtonClickListener());
        _imbClearCanvas.setOnClickListener(new ButtonClickListener());
        _imbUndo.setOnClickListener(new ButtonClickListener());
        _imbRedo.setOnClickListener(new ButtonClickListener());
        _imbThickness.setOnClickListener(new ButtonClickListener());
    }

    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imb_clear_canvas:
                    _drawView.clearCanvas();
//                    if (_tmpCategory.color == UtilCategory.CATEGORY_COLOR_INCOME) { // income
//                        _drawView.setBackground(ContextCompat.getDrawable(_activity,
//                                R.drawable.new_category_background_income));
//                    } else if (_tmpCategory.color == UtilCategory.CATEGORY_COLOR_EXPENSE) { // expense
//                        _drawView.setBackground(ContextCompat.getDrawable(_activity,
//                                R.drawable.new_category_background_expense));
//                    }
                    break;
                case R.id.imb_undo:
                    _drawView.undo();
                    break;
                case R.id.imb_redo:
                    _drawView.redo();
                    break;
                case R.id.imb_thickness:
                    AlertDialog.Builder dialog3 = new AlertDialog.Builder(_activity);
                    dialog3.setIcon(R.mipmap.ic_mikan);
                    dialog3.setTitle(R.string.stroke_thickness);
                    dialog3.setSingleChoiceItems(_thicknessList, _selectedThickness, (DialogInterface d3, int position)-> {
                        switch (position) {
                            case 0:
                                _drawView.setStrokeWidth(getResources().getDimension(R.dimen.draw_view_thickness_small));
                                break;
                            case 1:
                                _drawView.setStrokeWidth(getResources().getDimension(R.dimen.draw_view_thickness_medium));
                                break;
                            case 2:
                                _drawView.setStrokeWidth(getResources().getDimension(R.dimen.draw_view_thickness_large));
                                break;
                        }
                        _selectedThickness = position;
                        d3.dismiss();
                    });
                    dialog3.setNegativeButton(R.string.cancel, (DialogInterface d, int which) -> { });
                    dialog3.show();
                    break;
                case R.id.btn_back:
                    ((CategoryCreationActivity) _activity).onBackPressed(TAG_INT);
                    break;
                case R.id.btn_done:
//                    Bitmap bitmap = _drawView.getBitmap();
//                    bitmap = UtilDrawing.getResizedBitmap(
//                            bitmap,
//                            (int) getResources().getDimension(R.dimen.new_category_drawable_size),
//                            (int) getResources().getDimension(R.dimen.new_category_drawable_size));
//                    bitmap = UtilDrawing.getBitmapClippedCircle(bitmap);
//                    _tmpCategory.image = UtilDrawing.bitmapToBytes(bitmap);
//                    ((CategoryCreationActivity) _activity).onNextPressed(TAG_INT, _tmpCategory);
                    break;
            }
        }
    }

    void setTmpCategory(TmpCategory tmpCategory) {
        _tmpCategory = tmpCategory;

        /*** When called from CategoryCreationActivity ***/
//        if (_tmpCategory.color == UtilCategory.CATEGORY_COLOR_INCOME) { // income
//            _drawView.setBackground(ContextCompat.getDrawable(_activity,
//                    R.drawable.new_category_background_income));
//
//        } else if (_tmpCategory.color == UtilCategory.CATEGORY_COLOR_EXPENSE) { // expense
//            _drawView.setBackground(ContextCompat.getDrawable(_activity,
//                    R.drawable.new_category_background_expense));
//        }

        /*** _categoryCode gets passed from CategoryEditionActivity ***/
        if (_categoryCode != -1) {
//            byte[] bytes = UtilCategory.getCategoryImage(_activity, _categoryCode);
//            Bitmap bitmap = UtilDrawing.bytesToBitmap(bytes);
//            if (tmpCategory.color == UtilCategory.CATEGORY_COLOR_INCOME) {
//                int fromColor = ContextCompat.getColor(_activity, R.color.colorAccent);
//                int toColor = ContextCompat.getColor(_activity, R.color.colorPrimary);
//                bitmap = UtilDrawing.replaceColor(bitmap, fromColor, toColor);
//                Drawable drawable = UtilDrawing.bitmapToDrawalbe(_activity, bitmap);
//                _drawView.setBackground(drawable);
//            } else if (tmpCategory.color == UtilCategory.CATEGORY_COLOR_EXPENSE) {
//                int fromColor = ContextCompat.getColor(_activity, R.color.colorPrimary);
//                int toColor = ContextCompat.getColor(_activity, R.color.colorAccent);
//                bitmap = UtilDrawing.replaceColor(bitmap, fromColor, toColor);
//                Drawable drawable = UtilDrawing.bitmapToDrawalbe(_activity, bitmap);
//                _drawView.setBackground(drawable);
//            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _activity.finish();
    }
}
