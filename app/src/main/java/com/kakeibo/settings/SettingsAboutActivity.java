package com.kakeibo.settings;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kakeibo.BuildConfig;
import com.kakeibo.MyExceptionHandler;
import com.kakeibo.R;

public class SettingsAboutActivity extends AppCompatActivity {
    private final static String TAG = SettingsAboutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_about);

        /*** this part is to handle unexpected crashes ***/
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        if (getIntent().getBooleanExtra("crash", false)) {
            Log.e(TAG, "crashed");
        }

        TextView txvVersion = findViewById(R.id.txv_version);
        TextView txvVersionName = findViewById(R.id.txv_version_name);

        String versionName = BuildConfig.VERSION_NAME;
        Log.d("asdf",versionName);
        int version = Integer.parseInt(versionName.split(".")[1]);

        switch (version) {
            case 0:
                txvVersion.setText(getString(R.string.app_name));
                break;
            case 1:
                txvVersion.setText(getString(R.string.kakeibo_plus));
                break;
            case 2:
                txvVersion.setText(getString(R.string.kakeibo_plus_plus));
                break;
        }
    }
}
