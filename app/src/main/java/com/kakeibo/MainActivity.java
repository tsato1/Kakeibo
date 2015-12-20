package com.kakeibo;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;

//import com.parse.Parse;
//import com.parse.ParseInstallation;

import java.util.Calendar;
//import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "TextToSpeach";
    private final int REQ_CODE_SPEECH_INPUT = 100;

    public static final String[] weekName = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    public static final String[] defaultCategory = {"Income", "Comm", "Meal", "Until", "Health", "Edu", "Cloth", "Trans", "Ent", "Ins", "Tax", "Other"};
    public static final String[] categoryColor = {"#2b381d", "#40552b", "#557238", "#6a8d47", "#80aa55", "#95b872", "#aac78d", "#bfd5aa", "#d5e2c7", "#eaf1e2", "#fafcf8"};

    ViewPager viewPager;
    PagerAdapter adapter;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Parse.initialize(this, "NVRkrfw3G8YX9xIimvVKzgH16LZrqgU13gyjKra2", "GODP3rm78l2XVQyGLWQY2eKa56UB2r0UA0IS2vcW");
        //ParseInstallation.getCurrentInstallation().saveInBackground();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayShowHomeEnabled(false);

        final TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.input));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.report));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager)findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                               @Override
                                               public void onTabSelected(TabLayout.Tab tab) {
                                                   viewPager.setCurrentItem(tab.getPosition());

                                                   if (adapter == null || viewPager == null) {
                                                       Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                                       startActivity(intent);
                                                   }

                                                   if (adapter != null) {
                                                       //if (tab.getText().toString().equals(R.string.input)) {
                                                       adapter.getFragment1().onResume();
                                                       //} else if (tab.getText().toString().equals(R.string.report)) {
                                                       adapter.getFragment2().onResume();
                                                       //}
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
            adapter.getFragment2().focusOnSavedItem(y, m, d);
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
