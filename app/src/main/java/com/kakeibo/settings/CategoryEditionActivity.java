package com.kakeibo.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.kakeibo.BuildConfig;
import com.kakeibo.CategoryListAdapter;
import com.kakeibo.KkbCategory;
import com.kakeibo.R;
import com.kakeibo.util.UtilAds;
import com.kakeibo.util.UtilCategory;

import java.util.List;

public class CategoryEditionActivity extends AppCompatActivity {
    private static final int MENU_ITEM_ID_DELETE = 0;
    private static final int MENU_ITEM_ID_EDIT = 1;

    private FrameLayout _adContainerView;
    private AdView _adView;

    private Context _context;
    private Button _btnBack;
    private TextView _txvNoCustomCategory;
    private ListView _lsvCustomCategories;
    private CategoryListAdapter _categoryListAdapter;
    private List<KkbCategory> _customKkbCategoriesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_category_edition);
        _context = this;

        /*** this part is to handle unexpected crashes ***/
//        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
//        if (getIntent().getBooleanExtra("crash", false)) {
//            Log.e(TAG, "crashed");
//        }

        /*** hide home button on actionbar ***/
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        /*** ads ***/
        if (UtilAds.isBannerAdsDisplayAgreed()) {
            initAd();
            loadBanner();
        }

        /*** findViews ***/
        _btnBack = findViewById(R.id.btn_back);
        _txvNoCustomCategory = findViewById(R.id.txv_no_custom_category);
        _lsvCustomCategories = findViewById(R.id.lsv_custom_categories);
        _btnBack.setOnClickListener((View view) -> { onBackPressed(); });

        /*** setup listview ***/
        _lsvCustomCategories.setOnItemClickListener(new ItemClickListener());
        _lsvCustomCategories.setOnCreateContextMenuListener(new ItemContextClickListener());
        _customKkbCategoriesList = UtilCategory.getCustomKkbCategoryList(_context);
        _categoryListAdapter = new CategoryListAdapter(_context, 0, _customKkbCategoriesList);
        _lsvCustomCategories.setAdapter(_categoryListAdapter);

        if (_customKkbCategoriesList.isEmpty()) _txvNoCustomCategory.setVisibility(View.VISIBLE);
        else _txvNoCustomCategory.setVisibility(View.GONE);
    }

    class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            StringBuilder message = new StringBuilder();
            List<String> allLangStrings = UtilCategory.getCategoryLangStrs(_customKkbCategoriesList.get(position).getCode());
            for (String str: allLangStrings) {
                String[] strs = str.split(","); /*** ex: "ENG, income" ***/
                if (strs.length<=1 || strs[1].trim().isEmpty()) continue;

                message.append(strs[0]).append(": ").append(strs[1]).append("\n");
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(_context);
            dialog.setIcon(R.mipmap.ic_mikan);
            dialog.setTitle(_customKkbCategoriesList.get(position).getName());
            dialog.setMessage(message);
            dialog.setPositiveButton(R.string.ok, (DialogInterface d, int which) -> { });
            dialog.create();
            dialog.show();
        }
    }

    static class ItemContextClickListener implements AdapterView.OnCreateContextMenuListener {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderIcon(R.mipmap.ic_mikan);
            menu.add(0, MENU_ITEM_ID_EDIT, 0, R.string.edit);
            menu.add(0, MENU_ITEM_ID_DELETE, 1, R.string.delete);
        }
    }

    public final static String EXTRA_KEY = "CATEGORY_CODE";

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        ListView.AdapterContextMenuInfo info = (ListView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        final KkbCategory kkbCategory = _categoryListAdapter.getItem(info.position);
        if (kkbCategory == null) return false;

        switch (menuItem.getItemId()) {
            case MENU_ITEM_ID_EDIT:
                Intent intent = new Intent(_context, CategoryCreationActivity.class);
                intent.putExtra(EXTRA_KEY, kkbCategory.getCode());
                startActivityForResult(intent, 10);
                break;
            case MENU_ITEM_ID_DELETE:
                AlertDialog.Builder dialog = new AlertDialog.Builder(_context);
                dialog.setIcon(R.mipmap.ic_mikan);
                dialog.setTitle(getString(R.string.quest_do_you_want_to_delete_item));
                dialog.setPositiveButton(R.string.yes, (DialogInterface d, int which) -> {
                    if (UtilCategory.deleteCustomKkbCategory(_context, kkbCategory.getCode())) {
                        Toast.makeText(_context, getString(R.string.msg_item_successfully_deleted), Toast.LENGTH_LONG).show();
                        _categoryListAdapter.notifyDataSetChanged();
                        if (_customKkbCategoriesList.isEmpty()) _txvNoCustomCategory.setVisibility(View.VISIBLE);
                        else _txvNoCustomCategory.setVisibility(View.GONE);
                    }
                });
                dialog.setNegativeButton(R.string.no, null);
                dialog.create();
                dialog.show();
                break;
        }
        return super.onContextItemSelected(menuItem);
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
