package com.kakeibo.settings;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.kakeibo.MyExceptionHandler;
import com.kakeibo.R;
import com.kakeibo.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class CategoryPlacementActivity extends AppCompatActivity {
    private final static String TAG = CategoryPlacementActivity.class.getSimpleName();
    private final static int NUM_PAGES = 3;

    private NonSwipeableViewPager _viewPager;
    private ViewPagerAdapter _adapter;
    private CategoryPlacementRemovalFragment _fragmentRemoval;
    private CategoryPlacementAdditionFragment _fragmentAddition;
    private CategoryPlacementReorderFragment _fragmentReorder;
    private List<ImageView> _lstDots;

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
    }

    public void addDots() {
        _lstDots = new ArrayList<>();
        LinearLayout dotsLayout = (LinearLayout)findViewById(R.id.dots);

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
        if (_fragmentReorder._dgvCategory.isEditMode()) {
            _fragmentReorder._dgvCategory.stopEditMode();
        } else {
            super.onBackPressed();
        }
    }
}
