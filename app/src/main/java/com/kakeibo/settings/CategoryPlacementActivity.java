package com.kakeibo.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.kakeibo.MyExceptionHandler;
import com.kakeibo.R;
import com.kakeibo.ViewPagerAdapter;
import com.kakeibo.util.UtilCategory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class CategoryPlacementActivity extends AppCompatActivity {
    private final static String TAG = CategoryPlacementActivity.class.getSimpleName();
    private final static int VIEWPAGER_OFF_SCREEN_PAGE_LIMIT = 2;
    private final static int NUM_PAGES = 3;

    public static int sNumColumns;

    private NonSwipeableViewPager _viewPager;
    private ViewPagerAdapter _adapter;
    private CategoryPlacementRemovalFragment _fragmentRemoval;
    private CategoryPlacementAdditionFragment _fragmentAddition;
    private CategoryPlacementReorderFragment _fragmentReorder;
    private List<ImageView> _lstDots;
    private List<Integer> _modCategoryCodeList;
    private List<Integer> _tmpRemovedCategoryCodes;
    private List<Integer> _tmpAddedCategoryCodes;

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
        _viewPager.setOffscreenPageLimit(VIEWPAGER_OFF_SCREEN_PAGE_LIMIT);

        /*** restoring fragment's instances ***/
        if (savedInstanceState != null) {
            _fragmentRemoval = (CategoryPlacementRemovalFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState, CategoryPlacementRemovalFragment.TAG);
            _fragmentAddition = (CategoryPlacementAdditionFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState, CategoryPlacementAdditionFragment.TAG);
            _fragmentReorder = (CategoryPlacementReorderFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState, CategoryPlacementReorderFragment.TAG);
        } else {
            _fragmentRemoval = CategoryPlacementRemovalFragment.newInstance();
            _fragmentAddition = CategoryPlacementAdditionFragment.newInstance();
            _fragmentReorder = CategoryPlacementReorderFragment.newInstance();
        }

        /*** setting adapter ***/
        _adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        _adapter.addFragment(_fragmentRemoval, CategoryPlacementRemovalFragment.TAG);
        _adapter.addFragment(_fragmentAddition, CategoryPlacementAdditionFragment.TAG);
        _adapter.addFragment(_fragmentReorder, CategoryPlacementReorderFragment.TAG);
        _viewPager.setAdapter(_adapter);

        /*** for dots indicator for pages ***/
        addDots();

        /*** initializing the set with dspCategories ***/
        _modCategoryCodeList = new ArrayList<>(UtilCategory.getDspCategoryCodeList(getApplicationContext())); // ordered by location
    }



    @Override
    protected void onSaveInstanceState(@Nonnull Bundle outState) {
        super.onSaveInstanceState(outState);

        /*** saving fragment's instance ***/
        getSupportFragmentManager().putFragment(outState, CategoryPlacementRemovalFragment.TAG, _fragmentRemoval);
        getSupportFragmentManager().putFragment(outState, CategoryPlacementAdditionFragment.TAG, _fragmentAddition);
        getSupportFragmentManager().putFragment(outState, CategoryPlacementReorderFragment.TAG, _fragmentReorder);
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

    @Override
    protected void onPause() {
        super.onPause();
        finish();
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

    public void onNextPressed(int tag, List<Integer> list) {
        switch (tag) {
            case CategoryPlacementRemovalFragment.TAG_INT:
                /*** check before proceed: list = category codes to remove ***/
                if (_modCategoryCodeList.size()-list.size()>=UtilCategory.NUM_MAX_DSP_CATEGORIES) {
                    Toast.makeText(this, "Please remove at least one category", Toast.LENGTH_LONG).show();
                    return ;
                }

                _modCategoryCodeList.removeAll(list);
                _fragmentAddition.setRemainingCount(UtilCategory.NUM_MAX_DSP_CATEGORIES - _modCategoryCodeList.size());
                Log.d("asdf1","modlist size = "+_modCategoryCodeList.size() +" listsize="+list.size());
                _viewPager.setCurrentItem(CategoryPlacementAdditionFragment.TAG_INT);
                _tmpRemovedCategoryCodes = new ArrayList<>(list);
                break;
            case CategoryPlacementAdditionFragment.TAG_INT:
                /*** check before proceed: list = category codes to add ***/
                if (_modCategoryCodeList.size()+list.size()>UtilCategory.NUM_MAX_DSP_CATEGORIES) {
                    Toast.makeText(this, "You cannot exceed the MAX count: "+UtilCategory.NUM_MAX_DSP_CATEGORIES, Toast.LENGTH_LONG).show();
                    return ;
                }

                _modCategoryCodeList.addAll(list);
                Log.d("asdf2","modlist size = "+_modCategoryCodeList.size() +" listsize="+list.size());
                _fragmentReorder.setItemsOnGrid(_modCategoryCodeList, list);
                _viewPager.setCurrentItem(CategoryPlacementReorderFragment.TAG_INT);
                _tmpAddedCategoryCodes = new ArrayList<>(list);
                break;
            case CategoryPlacementReorderFragment.TAG_INT:
                /*** list: contains necessary categories ordered by location the user wants ***/
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setIcon(R.mipmap.ic_mikan);
                dialog.setTitle("Determine how categories get displayed on INPUT screen");
                dialog.setMessage("Do you want to proceed with the specified categories with specified order");
                dialog.setPositiveButton(R.string.yes, (DialogInterface d, int which) -> {
                    Toast.makeText(this, R.string.next, Toast.LENGTH_SHORT).show();
                    UtilCategory.updateDspTable(getApplicationContext(), list);
                    finish();
                });
                dialog.setNegativeButton(R.string.no, (DialogInterface d, int which) -> {});
                dialog.show();
                break;
        }
    }

    public void onBackPressed(int tag) {
        switch (tag) {
            case CategoryPlacementRemovalFragment.TAG_INT:
                super.onBackPressed();
                break;
            case CategoryPlacementAdditionFragment.TAG_INT:
                _modCategoryCodeList.addAll(_tmpRemovedCategoryCodes);
                _viewPager.setCurrentItem(CategoryPlacementRemovalFragment.TAG_INT);
                break;
            case CategoryPlacementReorderFragment.TAG_INT:
                _viewPager.setCurrentItem(CategoryPlacementAdditionFragment.TAG_INT);
                _modCategoryCodeList.removeAll(_tmpAddedCategoryCodes);
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
