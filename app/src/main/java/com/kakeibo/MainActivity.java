package com.kakeibo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.kakeibo.pref.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TextToSpeach";
    private final int REQ_CODE_SPEECH_INPUT = 100;

    //public static final String[] weekName = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    //public static final String[] defaultCategory = {"Income", "Comm", "Meal", "Until", "Health", "Edu", "Cloth", "Trans", "Ent", "Ins", "Tax", "Other"};
    public static final String[] categoryColor = {"#2b381d", "#40552b", "#557238", "#6a8d47", "#80aa55", "#95b872", "#aac78d", "#bfd5aa", "#d5e2c7", "#eaf1e2", "#fafcf8"};

//    ViewPager viewPager;
//    PagerAdapter adapter;
//    private TextToSpeech tts;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TabFragment1 tab1;
    private TabFragment2 tab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        tab1 = new TabFragment1();
        tab2 = new TabFragment2();
        adapter.addFragment(tab1, getString(R.string.input));
        adapter.addFragment(tab2, getString(R.string.report));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        //getSupportActionBar().setDisplayShowTitleEnabled(false);
//        //getSupportActionBar().setDisplayShowHomeEnabled(false);
//
//        final TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.input));
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.report));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//
//        viewPager = (ViewPager)findViewById(R.id.pager);
//        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//                                               @Override
//                                               public void onTabSelected(TabLayout.Tab tab) {
//                                                   viewPager.setCurrentItem(tab.getPosition());
//
//                                                   if (adapter == null
//                                                           || viewPager == null
//                                                           || adapter.getFragment1() == null
//                                                           || adapter.getFragment2() == null) {
//                                                       adapter.getItem(0);
//                                                       adapter.getItem(1);
//                                                       Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                                                       startActivity(intent);
//                                                   }
//
//                                                   if (adapter != null) {
//                                                       adapter.getFragment1().onResume();
//                                                       adapter.getFragment2().onResume();
//                                                   }
//                                               }
//
//                                               @Override
//                                               public void onTabUnselected(TabLayout.Tab tab) {
//
//                                               }
//
//                                               @Override
//                                               public void onTabReselected(TabLayout.Tab tab) {
//
//                                               }
//                                           }
//        );
//    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onItemSaved(String date) {
        //Log.d("testtest", date);
        String y = date.substring(0, 4);
        String m = date.substring(5, 7);
        String d = date.substring(8, date.indexOf(" "));
        //Log.d("testtest", "y=" + y + " m=" + m + " d=" + d);
        if (d.length() == 1) {
            d = "0" + d;
        }

        try {
            tab2.focusOnSavedItem(y, m, d);
        } catch (Exception e) {
        }
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
