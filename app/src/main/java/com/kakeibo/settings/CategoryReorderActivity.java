package com.kakeibo.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.kakeibo.BuildConfig;
import com.kakeibo.R;

import java.util.List;

public class CategoryReorderActivity extends AppCompatActivity
        implements SettingsCategoryEventListener {

    private static final String TAG = CategoryReorderActivity.class.getSimpleName();

    private CategoryPlacementReorderFragment _fragmentReorder;

    private FrameLayout _adContainerView;
    private AdView _adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_category_reorder);

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
//        if (UtilAds.isBannerAdsDisplayAgreed()) {
//            initAd();
//            loadBanner();
//        }

        _fragmentReorder = CategoryPlacementReorderFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frl_settings_category_reorder_container, _fragmentReorder)
                .commit();//frl_settings_category_reorder_container
    }

    @Override
    protected void onStart() {
        super.onStart();
//        List<Integer> list = new ArrayList<>(UtilCategory.getDspCategoryCodeList(getApplicationContext())); // ordered by location
//        _fragmentReorder.setItemsOnGrid(list, new ArrayList<>());
    }

    @Override
    public void onNextPressed(int tag, List<Integer> list) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_mikan);
        dialog.setTitle(R.string.reorder_categories);
        dialog.setMessage(R.string.quest_determine_category_order);
        dialog.setPositiveButton(R.string.yes, (DialogInterface d, int which) -> {
            Toast.makeText(this, R.string.msg_change_successfully_saved, Toast.LENGTH_LONG).show();
//            UtilCategory.updateDspTable(getApplicationContext(), list);
            finish();
        });
        dialog.setNegativeButton(R.string.no, (DialogInterface d, int which) -> {});
        dialog.show();
    }

    @Override
    public void onBackPressed(int tag) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (_fragmentReorder!=null && _fragmentReorder._dgvCategory.isEditMode()) {
            _fragmentReorder._dgvCategory.stopEditMode();
        } else {
            super.onBackPressed();
        }
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
            _adView.setAdUnitId(getString(R.string.category_reorder_banner_ad));
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
