package com.kakeibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.kakeibo.settings.SettingsCompatActivity;
import com.kakeibo.util.UtilAds;
import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilKeyboard;
import com.kakeibo.util.UtilSystem;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

//roadmap
//todo tab for different topic
//todo sync from google drive
//todo bar graph
//todo data sync - firebase
//todo discard holograph library
//future fix////
//todo bug for arabic check query some arabic languages in the date part
//todo edit doesn't save item tabfragment2D -> kotlin version
//todo expected/unexpected, favorite
//todo textsize for all the textviews
//current------------
//todo tell user max number of custom categories they can make
//done-------
//todo new categories max 10: TEST!
//todo custom category too big in reorder fragment -> dimen 50dp TEST!
//todo ask if user wants category management or ads -> hide ads depending on answer
//todo sharedpreferences in KkbApplication
//todo custom categories trim in circle -> can see the square edge
//todo CategoryCreationActivity language options
//todo delete paid
//todo string delete unused strings
//todo string translateion
//put aside----
//todo ads code -> Utility

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int VIEWPAGER_OFF_SCREEN_PAGE_LIMIT = 2;

//    public static final String[] categoryColor = {
//            "#2b381d", "#324220", "#374C22", "#40552b", "#465E2E",
//            "#4E6A32", "#557238", "#6a8d47", "#7ca058", "#80aa55",
//            "#8CB365", "#95b872", "#aac78d", "#bfd5aa", "#d5e2c7",
//            "#eaf1e2"}; //"#fafcf8"

    public static final String[] categoryColor = {
            "#2b381d", "#40552b", "#557238", "#80aa55", "#aac78d",
            "#eaf1e2", "#fafcf8", "#fafcf8", "#fafcf8", "#fafcf8",
            "#fafcf8", "#fafcf8", "#fafcf8", "#fafcf8", "#fafcf8",
            "#fafcf8"
    };

    public static int sFragmentPosition;
    public static int sDateFormat;
    public static int sFractionDigits;
    public static int sNumColumns;
    public static String[] sWeekName;

    private PublisherAdView _publisherAdView;
    private FragmentManager fm;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TabFragment1 tabFragment1;
    private TabFragment2 tabFragment2;
    private TabFragment3 tabFragment3;
    private static FloatingActionButton fabStart;
    private static FloatingActionButton fabEnd;

    private Activity _activity;

    private FrameLayout _adContainerView;
    private AdView _adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*** loading categories from db ***/
        if (UtilSystem.isLangChanged(this)) {
            UtilCategory.reloadCategoryLists(this);
        }

        /*** this part is to handle unexpected crashes ***/
//        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
//        if (getIntent().getBooleanExtra("crash", false)) {
//            Log.e(TAG, "crashed");
//        }

        /*** toolbar ***/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*** ads ***/
        if (UtilAds.isBannerAdsDisplayAgreed()) {
            initAd();
            loadBanner();
        }

        /*** viewPager ***/
        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(VIEWPAGER_OFF_SCREEN_PAGE_LIMIT);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        fm = getSupportFragmentManager();

        fabStart = findViewById(R.id.fab_start);
        fabEnd = findViewById(R.id.fab_end);
        fabStart.setOnClickListener(new ButtonClickListener());
        fabEnd.setOnClickListener(new ButtonClickListener());
        sWeekName = getResources().getStringArray(R.array.week_name);

        _activity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        loadSharedPreference();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UtilKeyboard.hideSoftKeyboard(_activity);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        tabFragment1 = TabFragment1.newInstance();
        tabFragment2 = TabFragment2.newInstance();
        tabFragment3 = TabFragment3.newInstance();
        adapter.addFragment(tabFragment1, getString(R.string.input));
        adapter.addFragment(tabFragment2, getString(R.string.report));
        adapter.addFragment(tabFragment3, getString(R.string.search));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                sFragmentPosition = position;

                if (position==0) {
                    fabStart.setVisibility(View.INVISIBLE);
                    fabEnd.setVisibility(View.INVISIBLE);
                } else if (position==1) {
                    fabStart.setImageResource(R.drawable.ic_cloud_upload_white);
                    fabStart.setVisibility(View.INVISIBLE);
                    fabEnd.setImageResource(R.drawable.ic_cloud_upload_white);
                    fabEnd.setVisibility(View.VISIBLE);
                } else if (position==2) {
                    fabStart.setImageResource(R.drawable.ic_add_white);
                    fabStart.setVisibility(View.VISIBLE);
                    fabEnd.setImageResource(R.drawable.ic_search_white);
                    fabEnd.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void loadSharedPreference() {
//        SharedPreferences pref = KkbApplication.getSharedPreferences();

//        /*** dateFormat ***/
//        String dateFormatIndex = pref.getString(getString(R.string.pref_key_date_format), UtilDate.DATE_FORMAT_YMD);
//        sDateFormat = Integer.parseInt(dateFormatIndex);
        sDateFormat = KkbApplication.getDateFormat(R.string.pref_key_date_format);

        /*** fraction digits ***/
//        Locale locale = Locale.getDefault();
//        int defValue = 0;
//        try {
//            Currency currency = Currency.getInstance(locale);
//            defValue = currency.getDefaultFractionDigits();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        }
//        String digitsIndex = pref.getString(getString(R.string.pref_key_fraction_digits), String.valueOf(defValue));
//        String[] fractionDigits = getResources().getStringArray(R.array.pref_list_fraction_digits);
//        sFractionDigits = Integer.parseInt(fractionDigits[Integer.parseInt(digitsIndex)]);
        sFractionDigits = KkbApplication.getFractionDigits(R.string.pref_key_fraction_digits);

        /*** num category icons per row ***/
//        String numColumnsIndex = pref.getString(getString(R.string.pref_key_num_columns), getString(R.string.def_num_columns));
//        String[] numColumns = getResources().getStringArray(R.array.pref_list_num_columns);
//        sNumColumns = Integer.parseInt(numColumns[Integer.parseInt(numColumnsIndex)]);
        sNumColumns = KkbApplication.getNumColumns(R.string.pref_key_num_columns);

        Log.d(TAG, "sDateFormat:"+sDateFormat+
                " sFractionDigits:"+sFractionDigits+
                " sNumColumns:"+sNumColumns);
    }

    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fab_start:
                    if (viewPager.getCurrentItem()==1) {
                        /**** invisible ****/
                    } else if (viewPager.getCurrentItem()==2) {
                        TabFragment3 tabFragment3 = (TabFragment3) fm.findFragmentByTag(
                                "android:switcher:" + viewPager.getId() + ":" + viewPager.getCurrentItem());
                        tabFragment3.addCriteria();
                    }
                    break;
                case R.id.fab_end:
                    if (viewPager.getCurrentItem()==1) {
                        TabFragment2 tabFragment2 = (TabFragment2) fm.findFragmentByTag(
                                "android:switcher:" + viewPager.getId() + ":" + viewPager.getCurrentItem());
                        tabFragment2.export();
                    } else if (viewPager.getCurrentItem()==2) {
                        TabFragment3 tabFragment3 = (TabFragment3) fm.findFragmentByTag(
                                "android:switcher:" + viewPager.getId() + ":" + viewPager.getCurrentItem());
                        tabFragment3.doSearch();
                    }
                    break;
            }
        }
    }

    public void onItemSaved(Query query, String eventDate) {
        Log.d(TAG, "onItemSaved() queryD="+query.getQueryD());
        try {
            tabFragment2.focusOnSavedItem(query, eventDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSearch(Query query, String fromDate, String toDate) {
        Log.d(TAG, "onSearch() queryD="+query.getQueryD());
        try {
            tabFragment2.onSearch(query, fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ViewPager getViewPager() {
        if (null == viewPager) {
            viewPager = findViewById(R.id.viewpager);
        }
        return viewPager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsCompatActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            _adView.setAdUnitId(getString(R.string.main_banner_ad));
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
