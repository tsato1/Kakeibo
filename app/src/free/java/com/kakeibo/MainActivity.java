package com.kakeibo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.MobileAds;

import com.kakeibo.settings.SettingsCompatActivity;
import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilDate;
import com.kakeibo.util.UtilKeyboard;
import com.kakeibo.util.UtilSystem;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

//todo add and subtract custom categories (discreet categories for income too)
//todo expense showing percentage as the fraction of income -> tabfragment2 2c and 2d

//todo bug for arabic check query some arabic languages in the date part

//todo expected/unexpected, favorite,


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String[] categoryColor = {
            "#2b381d", "#324220", "#374C22", "#40552b", "#465E2E",
            "#4E6A32", "#557238", "#6a8d47", "#7ca058", "#80aa55",
            "#8CB365", "#95b872", "#aac78d", "#bfd5aa", "#d5e2c7",
            "#eaf1e2"}; //"#fafcf8"

    public static int sFragmentPosition;
    public static int sDateFormat;
    public static int sFractionDigits;
    public static int sWidgetFontSize;
    public static String[] sWeekName;

//    private InterstitialAd mInterstitialAd;
    private FragmentManager fm;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TabFragment1 tabFragment1;
    private TabFragment2 tabFragment2;
    private TabFragment3 tabFragment3;
    private static FloatingActionButton fabStart;
    private static FloatingActionButton fabEnd;

    private Activity _activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*** loading categories from db ***/
        if (UtilSystem.isLangChanged(this)) {
            UtilCategory.reloadCategoryLists(this);
        }

        /*** this part is to handle unexpected crashes ***/
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        if (getIntent().getBooleanExtra("crash", false)) {
            Log.e(TAG, "crashed");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        fm = getSupportFragmentManager();

        fabStart = findViewById(R.id.fab_start);
        fabEnd = findViewById(R.id.fab_end);
        fabStart.setOnClickListener(new ButtonClickListener());
        fabEnd.setOnClickListener(new ButtonClickListener());
        sWeekName = getResources().getStringArray(R.array.week_name);
//        loadAds();
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager, int flag) {
            super(manager, flag);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void loadSharedPreference() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        /*** dateFormat ***/
        String dateFormatIndex = pref.getString(getString(R.string.pref_key_date_format), UtilDate.DATE_FORMAT_YMD);
        sDateFormat = Integer.parseInt(dateFormatIndex);

        /*** fraction digits ***/
        Locale locale = Locale.getDefault();
        int defValue = 0;
        try {
            Currency currency = Currency.getInstance(locale);
            defValue = currency.getDefaultFractionDigits();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        String digitsIndex = pref.getString(getString(R.string.pref_key_fraction_digits), String.valueOf(defValue));
        String[] fractionDigits = getResources().getStringArray(R.array.pref_list_fraction_digits);
        sFractionDigits = Integer.parseInt(fractionDigits[Integer.parseInt(digitsIndex)]);

        /*** widget and font size ***/
        String widgetFontSizeIndex = pref.getString(getString(R.string.pref_key_widget_and_font_size), "1");
        int sWidgetFontSize = Integer.parseInt(widgetFontSizeIndex);

        Log.d(TAG, "sDateFormat:"+sDateFormat+
                " sFractionDigits:"+sFractionDigits+
                " widgetAndFontSize:"+widgetFontSizeIndex);
    }

    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fab_start:
                    if (viewPager.getCurrentItem()==1) {
                        /**** invisible ****/
//                        TabFragment2 tabFragment2 = (TabFragment2) fm.findFragmentByTag(
//                                "android:switcher:" + viewPager.getId() + ":" + viewPager.getCurrentItem());
//                        tabFragment2.toggleViews();
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

//    private void loadAds() {
//        Log.d(TAG, "loading ads");
//
//        MobileAds.initialize(this, "ca-app-pub-3282892636336089~3692682630");
//        mInterstitialAd = new InterstitialAd(this);
//        AdRequest.Builder request = new AdRequest.Builder();
//
//        if (BuildConfig.DEBUG) {
//            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); /*** in debug mode ***/
//        } else {
//            mInterstitialAd.setAdUnitId(getString(R.string.google_ads_api_key));
//        }
//        mInterstitialAd.loadAd(request.build());
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                // Load the next interstitial.
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//            }
//        });
//    }

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
}
