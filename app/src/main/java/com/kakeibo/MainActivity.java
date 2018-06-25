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

import com.kakeibo.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

//todo pie graph holograph library

//todo add ads

//todo bring search fragment to different viewpager
//todo add and subtract custom categories (discreet categories for income too)

//todo locale

// versioncode: 9 (mom)
public class MainActivity extends AppCompatActivity {

    public static final String[] categoryColor = {
            "#2b381d", "#40552b", "#557238", "#6a8d47", "#80aa55", "#95b872",
            "#aac78d", "#bfd5aa", "#d5e2c7", "#eaf1e2", "#fafcf8"};

    private ViewPager viewPager;
    private TabFragment1 tabFragment1;
    private TabFragment2 tabFragment2;
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
        adapter.addFragment(tabFragment1, getString(R.string.input));
        adapter.addFragment(tabFragment2, getString(R.string.report));
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

    public void onItemSaved(String date) {
        String[] ymd = date.split(" ")[0].split("/");
        String y, m, d;

        switch (sDateFormat) {
            case 1: // MDY
                y = ymd[2];
                m = ymd[0];
                d = ymd[1];
                break;
            case 2: // DMY
                y = ymd[2];
                m = ymd[1];
                d = ymd[0];
                break;
            default:  // YMD
                y = ymd[0];
                m = ymd[1];
                d = ymd[2];
        }

        try {
            tabFragment2.focusOnSavedItem(y, m, d);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSharedPreference() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String f = mPref.getString(SettingsActivity.PREF_KEY_DATE_FORMAT, Utilities.DATE_FORMAT_YMD);
        sDateFormat = Integer.parseInt(f);
    }

    // speech to text //
//    public void speechText(String string) {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, string);
//
//        try
//        {
//            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
//        }
//        catch (ActivityNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode)
//        {
//            case REQ_CODE_SPEECH_INPUT:
//            {
//                if (resultCode == RESULT_OK && null != data)
//                {
//                    final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    Log.d("MainActivity.java", "You said: " + result.get(0));
//                    new Thread(new Runnable() {
//                        public void run() {
//                            //adapter.getFragment1().edt_amount.setText(result.get(0));
//                        }
//                    }).start();
//
//                    //next step
//                }
//                break;
//            }
//        }
//    }


    public ViewPager getViewPager() {
        if (null == viewPager) {
            viewPager = (ViewPager) findViewById(R.id.viewpager);
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
