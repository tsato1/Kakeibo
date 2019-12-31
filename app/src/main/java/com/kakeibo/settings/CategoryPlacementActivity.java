package com.kakeibo.settings;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.kakeibo.KkbCategory;
import com.kakeibo.MyExceptionHandler;
import com.kakeibo.R;
import com.kakeibo.ViewPagerAdapter;
import com.kakeibo.util.UtilCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class CategoryPlacementActivity extends AppCompatActivity {
    private final static String TAG = CategoryPlacementActivity.class.getSimpleName();
    private final static int NUM_PAGES = 3;

    public static int sNumColumns;

    private NonSwipeableViewPager _viewPager;
    private ViewPagerAdapter _adapter;
    private CategoryPlacementRemovalFragment _fragmentRemoval;
    private CategoryPlacementAdditionFragment _fragmentAddition;
    private CategoryPlacementReorderFragment _fragmentReorder;
    private List<ImageView> _lstDots;
    private Set<KkbCategory> _modKkbCategorySet;
    private List<KkbCategory> _kkbCategoryRmvList;
    private List<KkbCategory> _kkbCategoryAddList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_category_placement);

        /*** this part is to handle unexpected crashes ***/
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        if (getIntent().getBooleanExtra("crash", false)) {
            Log.e(TAG, "crashed");
        }

        /*** hide home button on actionbar ***/
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        /*** find views ***/
        _viewPager = findViewById(R.id.view_pager);

        /*** setting adapter ***/
        _adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        _fragmentRemoval = CategoryPlacementRemovalFragment.newInstance();
        _fragmentAddition = CategoryPlacementAdditionFragment.newInstance();
        _fragmentReorder = CategoryPlacementReorderFragment.newInstance();
        _adapter.addFragment(_fragmentRemoval, CategoryPlacementRemovalFragment.TAG);
        _adapter.addFragment(_fragmentAddition, CategoryPlacementAdditionFragment.TAG);
        _adapter.addFragment(_fragmentReorder, CategoryPlacementReorderFragment.TAG);
        _viewPager.setAdapter(_adapter);

        /*** for dots indicator for pages ***/
        addDots();

        /*** initializing the set with dspCategories ***/
        _modKkbCategorySet = new HashSet<>(UtilCategory.getDspKkbCategoryList(getApplicationContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");

        /*** SharedPreference: num category icons per row ***/
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        String numColumnsIndex = pref.getString(getString(R.string.pref_key_num_columns), getString(R.string.def_num_columns));
        String[] numColumns = getResources().getStringArray(R.array.pref_list_num_columns);
        sNumColumns = Integer.parseInt(numColumns[Integer.parseInt(numColumnsIndex)]);

        Log.d(TAG, "sNumColumns:"+sNumColumns);
    }

    public void addDots() {
        _lstDots = new ArrayList<>();
        LinearLayout dotsLayout = findViewById(R.id.dots);

        for(int i = 0; i < NUM_PAGES; i++) {
            ImageView dot = new ImageView(this);

            if (i==0) {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pager_dot_selected));
            } else {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pager_dot_not_selected));
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
                    int drawableId = (i==position)?
                            (R.drawable.pager_dot_selected):(R.drawable.pager_dot_not_selected);
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), drawableId);
                    _lstDots.get(i).setImageDrawable(drawable);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void onNextPressed(int tag, List<KkbCategory> list) {
        switch (tag) {
            case 0:
                _viewPager.setCurrentItem(1);
                _kkbCategoryRmvList = new ArrayList<>(list);

                for (KkbCategory item: list) {
                    _modKkbCategorySet.remove(item);
                    Log.d(TAG, "1, location:"+item.getLocation()+" "+item.getName());
                }

                break;
            case 1:
                _viewPager.setCurrentItem(2);
                _kkbCategoryAddList = new ArrayList<>(list);

                List<KkbCategory> out = new ArrayList<>(_modKkbCategorySet);

                Collections.sort(out, (KkbCategory o1, KkbCategory o2) -> {
                    return o1.getLocation() - o2.getLocation();
                });

                for (KkbCategory item: list) {
                    out.add(item);
                    Log.d(TAG, "2, location:"+item.getLocation()+" "+item.getName());
                }
                _fragmentReorder.setItemsOnGrid(out);
                break;
            case 2:
                //todo dialog

                UtilCategory.updateDspTable(getApplicationContext(), list);


                Toast.makeText(this, R.string.next, Toast.LENGTH_SHORT).show();

                int j = 0;
                for (KkbCategory i: list) {
                    Log.d(TAG, "3, j="+j+" location:"+i.getLocation()+" "+i.getName());
                    j++;
                }

                finish();
                break;
        }
    }

    public void onBackPressed(int tag) {
        switch (tag) {
            case -1:
                super.onBackPressed();
                break;
            case 0:
                _viewPager.setCurrentItem(0);
                break;
            case 1:
                _viewPager.setCurrentItem(1);
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
}
