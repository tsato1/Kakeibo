package com.kakeibo.settings;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kakeibo.MyExceptionHandler;
import com.kakeibo.R;
import com.kakeibo.ViewPagerAdapter;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class CategoryPlacementActivity extends AppCompatActivity implements SettingsCategoryEventListener {
    private final static String TAG = CategoryPlacementActivity.class.getSimpleName();

    private NonSwipeableViewPager _viewPager;
    private ViewPagerAdapter _adapter;
    private CategoryPlacementRemovalFragment _fragmentRemoval;
    private CategoryPlacementAdditionFragment _fragmentAddition;
    private CategoryPlacementReorderFragment _fragmentReorder;

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
    }

    @Override
    public void onNextPressed(int tag) {
        switch (tag) {
            case 0:
                _viewPager.setCurrentItem(1);
                break;
            case 1:
                _viewPager.setCurrentItem(2);
                break;
            case 2:
                Toast.makeText(this, R.string.next, Toast.LENGTH_SHORT).show();
                break;
        }
    }
    
    @Override
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
}
