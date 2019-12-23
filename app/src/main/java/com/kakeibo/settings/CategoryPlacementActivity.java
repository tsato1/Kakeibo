package com.kakeibo.settings;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kakeibo.MyExceptionHandler;
import com.kakeibo.R;

public class CategoryPlacementActivity extends AppCompatActivity implements SettingsCategoryEventListener {
    private final static String TAG = CategoryPlacementActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_category_placement);

        /*** this part is to handle unexpected crashes ***/
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        if (getIntent().getBooleanExtra("crash", false)) {
            Log.e(TAG, "crashed");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frl_settings_category_placement_container, new CategoryPlacementRemovalFragment())
                .addToBackStack(CategoryPlacementRemovalFragment.TAG)
                .commit();
    }

    @Override
    public void onNextPressed(int tag) {
        switch (tag) {
            case 0:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frl_settings_category_placement_container, new CategoryPlacementAdditionFragment())
                        .addToBackStack(CategoryPlacementRemovalFragment.TAG)
                        .commit();
                break;
            case 1:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frl_settings_category_placement_container, new CategoryPlacementReorderFragment())
                        .addToBackStack(CategoryPlacementRemovalFragment.TAG)
                        .commit();
                break;
            case 2:
                Toast.makeText(this, R.string.next, Toast.LENGTH_SHORT).show();
                break;
        }

    }
    
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            Log.d("asdf", "oioi");
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            Log.d("asdf", "oioi2");
            super.onBackPressed();
        }
    }
}
