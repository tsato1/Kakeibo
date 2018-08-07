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
import android.view.Menu;
import android.view.MenuItem;

import com.kakeibo.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;


//todo add ads

//todo add and subtract custom categories (discreet categories for income too)

//todo get only one dot in edit_amount and handle text starting with dot

//todo functionality of log out

//todo google drive api for release version

//todo save search

// versioncode: 9 (mom)
public class MainActivity extends AppCompatActivity {

    public static final String[] categoryColor = {
            "#2b381d", "#40552b", "#557238", "#6a8d47", "#80aa55", "#95b872",
            "#aac78d", "#bfd5aa", "#d5e2c7", "#eaf1e2", "#fafcf8"};

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

    @Override
    public void onResume() {
        super.onResume();
        loadSharedPreference();
    }

    public void onItemSaved(Query query) {
//        String[] ymd = date.split(" ")[0].split("/");
//        String y, m, d;
//
//        switch (sDateFormat) {
//            case 1: // MDY
//                y = ymd[2];
//                m = ymd[0];
//                d = ymd[1];
//                break;
//            case 2: // DMY
//                y = ymd[2];
//                m = ymd[1];
//                d = ymd[0];
//                break;
//            default:  // YMD
//                y = ymd[0];
//                m = ymd[1];
//                d = ymd[2];
//        }

        try {
            tabFragment2.focusOnSavedItem(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSearch(Query query) {
        try {
            tabFragment2.onSearch(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSharedPreference() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String f = mPref.getString(SettingsActivity.PREF_KEY_DATE_FORMAT, Util.DATE_FORMAT_YMD);
        sDateFormat = Integer.parseInt(f);
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
