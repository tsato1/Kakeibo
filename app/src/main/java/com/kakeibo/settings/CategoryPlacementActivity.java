package com.kakeibo.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.kakeibo.util.UtilAds;
import com.kakeibo.util.UtilCategory;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class CategoryPlacementActivity extends AppCompatActivity
        implements SettingsCategoryEventListener{
    private final static String TAG = CategoryPlacementActivity.class.getSimpleName();
    private final static int VIEWPAGER_OFF_SCREEN_PAGE_LIMIT = 2;
    private final static int NUM_PAGES = 3;

    private NonSwipeableViewPager _viewPager;
    private ViewPagerAdapter _adapter;
    private CategoryPlacementRemovalFragment _fragmentRemoval;
    private CategoryPlacementAdditionFragment _fragmentAddition;
    private CategoryPlacementReorderFragment _fragmentReorder;
    private List<ImageView> _lstDots;
    private List<Integer> _modCategoryCodeList;
    private List<Integer> _tmpRemovedCategoryCodes;
    private List<Integer> _tmpAddedCategoryCodes;

    private FrameLayout _adContainerView;
    private AdView _adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_category_placement);

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
            _fragmentRemoval = (CategoryPlacementRemovalFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState, CategoryPlacementRemovalFragment.TAG);
            _fragmentAddition = (CategoryPlacementAdditionFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState, CategoryPlacementAdditionFragment.TAG);
            _fragmentReorder = (CategoryPlacementReorderFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState, CategoryPlacementReorderFragment.TAG);
        } else {
            _fragmentRemoval = CategoryPlacementRemovalFragment.newInstance();
            _fragmentAddition = CategoryPlacementAdditionFragment.newInstance();
            _fragmentReorder = CategoryPlacementReorderFragment.newInstance();
        }

        /*** setting adapter ***/
        _adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        _adapter.addFragment(_fragmentRemoval, CategoryPlacementRemovalFragment.TAG);
        _adapter.addFragment(_fragmentAddition, CategoryPlacementAdditionFragment.TAG);
        _adapter.addFragment(_fragmentReorder, CategoryPlacementReorderFragment.TAG);
        _viewPager.setAdapter(_adapter);

        /*** for dots indicator for pages ***/
        addDots();

        /*** initializing the set with dspCategories ***/
//        _modCategoryCodeList = new ArrayList<>(UtilCategory.getDspCategoryCodeList(getApplicationContext())); // ordered by location
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /*** saving fragment's instance ***/
        getSupportFragmentManager().putFragment(outState, CategoryPlacementRemovalFragment.TAG, _fragmentRemoval);
        getSupportFragmentManager().putFragment(outState, CategoryPlacementAdditionFragment.TAG, _fragmentAddition);
        getSupportFragmentManager().putFragment(outState, CategoryPlacementReorderFragment.TAG, _fragmentReorder);
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

    @Override
    public void onNextPressed(int tag, List<Integer> list) {
        switch (tag) {
            case CategoryPlacementRemovalFragment.TAG_INT:
                /*** check before proceed: list = category codes to remove ***/
                if (_modCategoryCodeList.size()-list.size()>=UtilCategory.NUM_MAX_DSP_CATEGORIES) {
                    Toast.makeText(this, "Please remove at least one category", Toast.LENGTH_LONG).show();
                    return ;
                }

                _modCategoryCodeList.removeAll(list);
                _fragmentAddition.setRemainingCount(UtilCategory.NUM_MAX_DSP_CATEGORIES - _modCategoryCodeList.size());
                Log.d("asdf1","modlist size = "+_modCategoryCodeList.size() +" listsize="+list.size());
                _viewPager.setCurrentItem(CategoryPlacementAdditionFragment.TAG_INT);
                _tmpRemovedCategoryCodes = new ArrayList<>(list);
                break;
            case CategoryPlacementAdditionFragment.TAG_INT:
                /*** check before proceed: list = category codes to add ***/
                if (_modCategoryCodeList.size()+list.size()>UtilCategory.NUM_MAX_DSP_CATEGORIES) {
                    Toast.makeText(this, "You cannot exceed the MAX count: "+UtilCategory.NUM_MAX_DSP_CATEGORIES, Toast.LENGTH_LONG).show();
                    return ;
                }

                _modCategoryCodeList.addAll(list);
                Log.d("asdf2","modlist size = "+_modCategoryCodeList.size() +" listsize="+list.size());
                _fragmentReorder.setItemsOnGrid(_modCategoryCodeList, list);
                _viewPager.setCurrentItem(CategoryPlacementReorderFragment.TAG_INT);
                _tmpAddedCategoryCodes = new ArrayList<>(list);
                break;
            case CategoryPlacementReorderFragment.TAG_INT:
                /*** list: contains necessary categories ordered by location the user wants ***/
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setIcon(R.mipmap.ic_mikan);
                dialog.setTitle(R.string.reorder_categories);
                dialog.setMessage(R.string.quest_determine_category_order);
                dialog.setPositiveButton(R.string.yes, (DialogInterface d, int which) -> {
                    Toast.makeText(this, R.string.msg_change_successfully_saved, Toast.LENGTH_LONG).show();
//                    UtilCategory.updateDspTable(getApplicationContext(), list);
                    finish();
                });
                dialog.setNegativeButton(R.string.no, (DialogInterface d, int which) -> {});
                dialog.show();
                break;
        }
    }

    @Override
    public void onBackPressed(int tag) {
        switch (tag) {
            case CategoryPlacementRemovalFragment.TAG_INT:
                super.onBackPressed();
                break;
            case CategoryPlacementAdditionFragment.TAG_INT:
                _modCategoryCodeList.addAll(_tmpRemovedCategoryCodes);
                _viewPager.setCurrentItem(CategoryPlacementRemovalFragment.TAG_INT);
                break;
            case CategoryPlacementReorderFragment.TAG_INT:
                _viewPager.setCurrentItem(CategoryPlacementAdditionFragment.TAG_INT);
                _modCategoryCodeList.removeAll(_tmpAddedCategoryCodes);
                break;
        }
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
            _adView.setAdUnitId(getString(R.string.category_placement_banner_ad));
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
