package com.kakeibo.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.kakeibo.BuildConfig;
import com.kakeibo.KkbCategory;
import com.kakeibo.MyExceptionHandler;
import com.kakeibo.R;
import com.kakeibo.db.CategoryLanDBAdapter;
import com.kakeibo.db.TmpCategory;
import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryCreationActivity extends AppCompatActivity {
    private static final String TAG = CategoryCreationActivity.class.getSimpleName();

    private static int _rowInsertIndex = 2;

    private Context _context;

    private TmpCategory _tmpCategory;

    private LinearLayout _lnlForm;
    private FrameLayout _adContainerView;
    private AdView _adView;
    private Button _btnColor, _btnLanguage, _btnDefaultIcons, _btnBack, _btnDone;
    private ImageButton _imbRemove, _imbAdd;
    private EditText _edtName;
    private List<String> _langList = new ArrayList<>();
    private List<KkbCategory> _kkbCategoryList = new ArrayList<>();
    private Map<String, EditText> _langMap = new HashMap<>();
    private ArrayAdapter<String> _langAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_category_creation);
        _context = this;

        /*** this part is to handle unexpected crashes ***/
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        if (getIntent().getBooleanExtra("crash", false)) {
            Log.e(TAG, "crashed");
        }

        /*** hide home button on actionbar ***/
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        /*** ads ***/
        initAd();
        loadBanner();

        /*** findViews ***/
        _lnlForm = findViewById(R.id.lnl_form);
        _btnColor = findViewById(R.id.btn_color);
        _btnLanguage = findViewById(R.id.btn_language);
        _imbRemove = findViewById(R.id.imb_remove);
        _imbAdd = findViewById(R.id.imb_add);
        _btnDefaultIcons = findViewById(R.id.btn_default_icon);
        _btnBack = findViewById(R.id.btn_back);
        _btnDone = findViewById(R.id.btn_done);
        _edtName = findViewById(R.id.edt_name);
        _btnColor.setOnClickListener(new ButtonClickListener());
        _btnLanguage.setOnClickListener(new ButtonClickListener());
        _btnDefaultIcons.setOnClickListener(new ButtonClickListener());
        _btnBack.setOnClickListener(new ButtonClickListener());
        _btnDone.setOnClickListener(new ButtonClickListener());
        _imbAdd.setOnClickListener(new ButtonClickListener());
        _imbRemove.setOnClickListener(new ButtonClickListener());

        _btnLanguage.setText(UtilSystem.getCurrentLangCode(this));

        /*** other prep ***/
        _langList.add(CategoryLanDBAdapter.COL_ENG);
        _langList.add(CategoryLanDBAdapter.COL_SPA);
        _langList.add(CategoryLanDBAdapter.COL_FRA);
        _langList.add(CategoryLanDBAdapter.COL_HIN);
        _langList.add(CategoryLanDBAdapter.COL_IND);
        _langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _langList);

        _kkbCategoryList = UtilCategory.getNonDspKkbCategoryList(this);
    }

    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_color:
                    if (_btnColor.getText().equals(getString(R.string.income))) {
                        _btnColor.setBackground(getDrawable(R.drawable.gradient_expense));
                        _btnColor.setText(getString(R.string.expense));
                    } else {
                        _btnColor.setBackground(getDrawable(R.drawable.gradient_income));
                        _btnColor.setText(getString(R.string.income));
                    }
                    break;
                case R.id.btn_language:
                    break;
                case R.id.btn_default_icon:
                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(_context);
                    LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View convertView = inflater.inflate(R.layout.form_new_category_drawable, null);
                    dialog2.setIcon(R.mipmap.ic_mikan);
                    dialog2.setTitle(R.string.default_icons);
                    dialog2.setView(convertView);
                    dialog2.setNegativeButton(R.string.cancel, (DialogInterface d, int which) -> {
                    });
                    LinearLayout lnl = convertView.findViewById(R.id.lnl_default_category);
                    for (KkbCategory kkbCategory: _kkbCategoryList) {
                        ImageView thumbnail = new ImageView(_context);
                        thumbnail.setImageResource(kkbCategory.getDrawable());

                        int paddingDp = (int) getResources().getDimension(R.dimen.list_row_padding_vertical);
                        float density = _context.getResources().getDisplayMetrics().density;
                        int paddingPixel = (int)(paddingDp * density);

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, paddingPixel, paddingPixel, 0);
                        thumbnail.setLayoutParams(lp);
                        lnl.addView(thumbnail);
                    }
                    final Dialog d2 = dialog2.show();
                    lnl.setOnClickListener((View v) -> {
                        d2.dismiss();
                    });
                    break;
                case R.id.btn_back:
                    onBackPressed();
                    break;
                case R.id.btn_done:
                    int color = 0;
                    if (_btnColor.getText().equals(getString(R.string.income))) {
                        color = 0;
                    } else if (_btnColor.getText().equals(getString(R.string.expense))) {
                        color = 1;
                    }
                    _tmpCategory = new TmpCategory(0, color, 111, 0,
                            "","","","","","","","",
                            "","","","","","","");

                    if (checkBeforeSave()) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(_context);
                        dialog.setIcon(R.mipmap.ic_mikan);
                        dialog.setTitle(R.string.create_new_category);
                        dialog.setMessage(R.string.quest_new_category_do_you_want_to_proceed);
                        dialog.setPositiveButton(R.string.yes, (DialogInterface d, int which) -> {
                            Toast.makeText(_context, R.string.msg_new_category_created, Toast.LENGTH_SHORT).show();
                            UtilCategory.addNewCategory(_context, _tmpCategory);
                            finish();
                        });
                        dialog.setNegativeButton(R.string.no, (DialogInterface d, int which) -> {
                        });
                        dialog.show();
                    }
                    break;
                case R.id.imb_add:
                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(_context);
                    dialog1.setIcon(R.mipmap.ic_mikan);
                    dialog1.setTitle(R.string.language);
                    dialog1.setAdapter(_langAdapter, (DialogInterface dialog, int position)-> {
                        LinearLayout row = (LinearLayout) LayoutInflater.from(_context).inflate(R.layout.form_new_category_language, null);
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
            }
        }
    }

    private boolean checkBeforeSave() {
        if (_edtName.getText().toString().trim().length() == 0) {
            Toast.makeText(this, R.string.err_please_enter_name, Toast.LENGTH_SHORT).show();
            return false;
        }

        for (EditText edt: _langMap.values()) {
            if (edt.getText().toString().trim().equals("")) {
                Toast.makeText(this, R.string.err_please_enter_name, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    /*** ads ***/
    private void initAd() {
        //Call the function to initialize AdMob SDK
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //get the reference to your FrameLayout
        _adContainerView = findViewById(R.id.ad_container);

        //Create an AdView and put it into your FrameLayout
        _adView = new AdView(this);
        if (BuildConfig.DEBUG) {
            _adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");/*** in debug mode ***/
        } else {
            _adView.setAdUnitId(getString(R.string.category_creation_banner_ad));
        }
        _adContainerView.addView(_adView);
    }

    private AdSize getAdSize() {
        //Determine the screen width to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        //you can also pass your selected width here in dp
        int adWidth = (int) (widthPixels / density);

        //return the optimal size depends on your orientation (landscape or portrait)
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        AdSize adSize = getAdSize();
        // Set the adaptive ad size to the ad view.
        _adView.setAdSize(adSize);

        // Start loading the ad in the background.
        _adView.loadAd(adRequest);
    }
}
