package com.kakeibo.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.kakeibo.BuildConfig;
import com.kakeibo.R;
import com.kakeibo.ViewPagerAdapter;
import com.kakeibo.db.TmpCategory;
import com.kakeibo.util.UtilAds;
import com.kakeibo.util.UtilCategory;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class CategoryCreationActivity extends AppCompatActivity {
    private final static String TAG = CategoryCreationActivity.class.getSimpleName();

    private final static int VIEWPAGER_OFF_SCREEN_PAGE_LIMIT = 2;
    private final static int NUM_PAGES = 3;

    private FrameLayout _adContainerView;
    private AdView _adView;

    private Context _context;
    private NonSwipeableViewPager _viewPager;
    private ViewPagerAdapter _adapter;
    private CategoryCreationColorFragment _fragmentColor;
    private CategoryCreationLanguageFragment _fragmentLanguage;
    private CategoryCreationIconFragment _fragmentIcon;

    private List<ImageView> _lstDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_category_creation);
        _context = this;

        /*** if this activity was called from CategoryEditionActivity ***/
        Intent intent = getIntent();
        int categoryCode = intent.getIntExtra(CategoryEditionActivity.EXTRA_KEY, -1);

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

        /*** find views ***/
        _viewPager = findViewById(R.id.view_pager);
        _viewPager.setOffscreenPageLimit(VIEWPAGER_OFF_SCREEN_PAGE_LIMIT);

        /*** restoring fragment's instances ***/
        if (savedInstanceState != null) {
            _fragmentColor = (CategoryCreationColorFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState, CategoryCreationColorFragment.TAG);
            _fragmentLanguage = (CategoryCreationLanguageFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState, CategoryCreationLanguageFragment.TAG);
            _fragmentIcon = (CategoryCreationIconFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState, CategoryCreationIconFragment.TAG);
        } else {
            _fragmentColor = CategoryCreationColorFragment.newInstance(categoryCode);
            _fragmentLanguage = CategoryCreationLanguageFragment.newInstance(categoryCode);
            _fragmentIcon = CategoryCreationIconFragment.newInstance(categoryCode);
        }

        /*** setting adapter ***/
        _adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        _adapter.addFragment(_fragmentColor, CategoryCreationColorFragment.TAG);
        _adapter.addFragment(_fragmentLanguage, CategoryCreationLanguageFragment.TAG);
        _adapter.addFragment(_fragmentIcon, CategoryCreationIconFragment.TAG);
        _viewPager.setAdapter(_adapter);

        /*** for dots indicator for pages ***/
        addDots();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /*** saving fragment's instance ***/
        getSupportFragmentManager().putFragment(outState, CategoryCreationColorFragment.TAG, _fragmentColor);
        getSupportFragmentManager().putFragment(outState, CategoryCreationLanguageFragment.TAG, _fragmentLanguage);
        getSupportFragmentManager().putFragment(outState, CategoryCreationIconFragment.TAG, _fragmentIcon);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void addDots() {
        _lstDots = new ArrayList<>();
        LinearLayout dotsLayout = findViewById(R.id.dots);

        for(int i = 0; i < NUM_PAGES; i++) {
            ImageView dot = new ImageView(this);

            if (i==0) {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_color_primary));
            } else {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_color_accent));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            dotsLayout.addView(dot, params);

            _lstDots.add(dot);
        }

        _viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i < NUM_PAGES; i++) {
                    int drawableId = (i==position)? (R.drawable.dot_color_primary):(R.drawable.dot_color_accent);
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), drawableId);
                    _lstDots.get(i).setImageDrawable(drawable);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void onNextPressed(int tag, TmpCategory tmpCategory) {
        switch (tag) {
            case CategoryCreationColorFragment.TAG_INT:
                _fragmentLanguage.setTmpCategory(tmpCategory);
                _viewPager.setCurrentItem(CategoryCreationLanguageFragment.TAG_INT);
                break;
            case CategoryCreationLanguageFragment.TAG_INT:
                _fragmentIcon.setTmpCategory(tmpCategory);
                _viewPager.setCurrentItem(CategoryCreationIconFragment.TAG_INT);
                break;
            case CategoryCreationIconFragment.TAG_INT:
                AlertDialog.Builder dialog = new AlertDialog.Builder(_context);
                dialog.setIcon(R.mipmap.ic_mikan);
                dialog.setTitle(R.string.create_category);
                dialog.setMessage(R.string.quest_category_creation_do_you_want_to_proceed);
                dialog.setPositiveButton(R.string.yes, (DialogInterface d, int which) -> {
                    if (UtilCategory.addNewCategory(_context, tmpCategory)==-1) {
                        Toast.makeText(_context, getString(R.string.err_category_not_created), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(_context, R.string.msg_category_created, Toast.LENGTH_LONG).show();
                    }
                    finish();
                });
                dialog.setNegativeButton(R.string.no, (DialogInterface d, int which) -> { });
                dialog.show();
                break;
        }
    }

    public void onBackPressed(int tag) {
        switch (tag) {
            case CategoryCreationColorFragment.TAG_INT:
                super.onBackPressed();
                break;
            case CategoryCreationLanguageFragment.TAG_INT:
                _viewPager.setCurrentItem(CategoryCreationColorFragment.TAG_INT);
                break;
            case CategoryCreationIconFragment.TAG_INT:
                _viewPager.setCurrentItem(CategoryCreationLanguageFragment.TAG_INT);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
