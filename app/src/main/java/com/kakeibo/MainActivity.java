package com.kakeibo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.kakeibo.settings.SettingsActivity;

import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;


//todo add ads

//todo add and subtract custom categories (discreet categories for income too)

//todo google drive api for release version


// versioncode: 9 (mom)
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String[] categoryColor = {
            "#2b381d", "#40552b", "#557238", "#6a8d47", "#80aa55", "#95b872",
            "#aac78d", "#bfd5aa", "#d5e2c7", "#eaf1e2", "#fafcf8"};

    private InterstitialAd mInterstitialAd;

    private ViewPager viewPager;
    private TabFragment1 tabFragment1;
    private TabFragment2 tabFragment2;
    private TabFragment3 tabFragment3;
    private SharedPreferences mPref;
    public static int sDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        loadSharedPreference();
        loadAds();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        tabFragment1 = new TabFragment1();
        tabFragment2 = new TabFragment2();
        tabFragment3 = new TabFragment3();
        adapter.addFragment(tabFragment1, getString(R.string.input));
        adapter.addFragment(tabFragment2, getString(R.string.report));
        adapter.addFragment(tabFragment3, getString(R.string.search));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
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
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String f = mPref.getString(SettingsActivity.PREF_KEY_DATE_FORMAT, Util.DATE_FORMAT_YMD);
        sDateFormat = Integer.parseInt(f);
    }

    private void loadAds() {
        MobileAds.initialize(this, "ca-app-pub-3282892636336089~3692682630");
        mInterstitialAd = new InterstitialAd(this);
        AdRequest.Builder request = new AdRequest.Builder();

        if (BuildConfig.DEBUG) {
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); /*** in debug mode ***/
        } else {
            mInterstitialAd.setAdUnitId(getString(R.string.google_ads_api_key));
        }
        mInterstitialAd.loadAd(request.build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    public void onItemSaved(Query query) {
        try {
            tabFragment2.focusOnSavedItem(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSearch(Query query) {
        try {
            tabFragment2.onSearch(query);

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d(TAG, "The interstitial wasn't loaded yet.");
            }
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
