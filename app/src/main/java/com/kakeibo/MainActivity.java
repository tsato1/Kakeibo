package com.kakeibo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
//import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "TextToSpeach";
    private final int REQ_CODE_SPEECH_INPUT = 100;

    public static final String[] weekName = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    public static final String[] defaultCategory = {"Income", "Meal", "Until", "Health", "Edu", "Cloth", "Trans", "Other"};

    ViewPager viewPager;
    PagerAdapter adapter;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(com.kakeibo.R.string.input));
        tabLayout.addTab(tabLayout.newTab().setText(com.kakeibo.R.string.list));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager)findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                               @Override
                                               public void onTabSelected(TabLayout.Tab tab) {
                                                   viewPager.setCurrentItem(tab.getPosition());

                                                   if (tab.getText().toString().equals("LIST")){
                                                       adapter.getFragment2().calMonth = Integer.parseInt(adapter.getFragment1().btnDate.getText().toString().substring(5, 7));
                                                       Calendar cal = Calendar.getInstance();
                                                       adapter.getFragment2().calYear = cal.get(Calendar.YEAR);
                                                       Log.d("testtest", String.valueOf(adapter.getFragment2().calMonth));
                                                       adapter.getFragment2().reset();
                                                       adapter.getFragment2().setLabel();
                                                       adapter.getFragment2().loadItems();
                                                       adapter.getFragment2().makeBalanceTable();
                                                   }
                                               }

                                               @Override
                                               public void onTabUnselected(TabLayout.Tab tab) {

                                               }

                                               @Override
                                               public void onTabReselected(TabLayout.Tab tab) {

                                               }
                                           }
        );


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
            viewPager = (ViewPager) findViewById(R.id.pager);
        }
        return viewPager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
