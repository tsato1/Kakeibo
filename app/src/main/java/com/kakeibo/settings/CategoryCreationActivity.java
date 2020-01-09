package com.kakeibo.settings;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.kakeibo.MyExceptionHandler;
import com.kakeibo.R;

public class CategoryCreationActivity extends AppCompatActivity {
    private static final String TAG = CategoryCreationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_category_creation);

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
    }
}
