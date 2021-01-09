package com.kakeibo.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.kakeibo.R;

public class SettingsAboutActivity extends AppCompatActivity {
    private final static String TAG = SettingsAboutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_about);
    }

    /*** call set up in layout xml ***/
    public void onClick(View v) {
        if (v.getId()==R.id.txv_how_to_use_app) {
            String url = "https://sites.google.com/view/kakeibo/home/how-to-use-the-app";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
//        } else if (v.getId()==R.id.txv_read_documentation) {
//            String url = "https://sites.google.com/view/kakeibo/home";
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);
        }
    }
}
